package de.olafkock.liferay.navigation;

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
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
//			Map<String, Object> vmVariables = new HashMap<String, Object>();
	
			ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
	
			final long scopeGroupId = themeDisplay.getScopeGroupId();
			ExpandoColumn column = ExpandoNavigationUtil.getNavigationColumn(themeDisplay.getCompanyId());
			try {
				ExpandoValue value = ExpandoValueLocalServiceUtil.getValue(column.getTableId(), column.getColumnId(), scopeGroupId);
				long[] groupIds = value.getLongArray();
				if(groupIds != null 
						&& !(groupIds.length == 1 && groupIds[0] != scopeGroupId) 
						&&  (groupIds[0] != 0L)) {
					List<Layout> originalLayouts = themeDisplay.getLayouts();
					LinkedList<Layout> layouts = new LinkedList<Layout>();

					boolean hadCurrentGroupId = false;
					boolean isPrivateLayoutSet = themeDisplay.getLayoutSet().isPrivateLayout();

					for(long groupId:groupIds) {
						hadCurrentGroupId |= (groupId == scopeGroupId);
						layouts.addAll(LayoutServiceUtil.getLayouts(groupId, isPrivateLayoutSet, 0L)); // public (false), toplevel pages (0L)
					}
					if(!hadCurrentGroupId) {
						layouts.addAll(0, originalLayouts);
					}
					themeDisplay.setLayouts(layouts);
				}
			} catch (NoSuchValueException e) {
				// fine - don't do any special treatment
			}
//			List<Layout> layouts = Collections.EMPTY_LIST;
//			layouts = LayoutLocalServiceUtil.getLayouts(17702, false, 0L);
//			layouts.addAll(layouts);		
		} catch (PortalException e1) {
			e1.printStackTrace();
		} catch (SystemException e1) {
			e1.printStackTrace();
		} catch (Exception ignore) {
			System.err.println(ignore.getClass().getName());
		}
	}

}