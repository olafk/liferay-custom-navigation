package de.olafkock.liferay.navigation;

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.LayoutServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.expando.NoSuchValueException;
import com.liferay.portlet.expando.model.ExpandoColumn;
import com.liferay.portlet.expando.model.ExpandoValue;
import com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomNavigationPreAction extends Action {
	public CustomNavigationPreAction() {
		super();
	}

	public void run(HttpServletRequest request, HttpServletResponse response) throws ActionException {
		try {
			ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
			final long scopeGroupId = themeDisplay.getScopeGroupId();
			ExpandoColumn column = ExpandoNavigationUtil.getNavigationColumn(themeDisplay.getCompanyId());

			try {
				ExpandoValue value = ExpandoValueLocalServiceUtil.getValue(column.getTableId(), column.getColumnId(), scopeGroupId);
				String[] groupIds = StringUtil.splitLines(value.getStringArray()[0]);
				List<GroupConfig> decodedGroups = ConfigurationUtil.decode(groupIds);
				if((decodedGroups.size() > 1 && decodedGroups.get(0).getGroupId() != scopeGroupId)) {
					LinkedList<Layout> layouts = new LinkedList<Layout>();

					for(GroupConfig groupConfig:decodedGroups) {
						layouts.addAll(LayoutServiceUtil.getLayouts(groupConfig.getGroupId(), groupConfig.isPrivate(), 0L)); // toplevel pages (0L)
					}
					themeDisplay.setLayouts(layouts);
				}
			} catch (NoSuchValueException e) {
				// fine - don't do any special treatment
			}
		} catch (PortalException e1) {
			e1.printStackTrace();
		} catch (SystemException e1) {
			e1.printStackTrace();
		} catch (Exception ignore) {
			System.err.println(ignore.getClass().getName());
		}
	}

}