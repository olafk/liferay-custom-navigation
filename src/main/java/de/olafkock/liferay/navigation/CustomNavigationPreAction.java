package de.olafkock.liferay.navigation;

import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalServiceUtil;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.LayoutServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import de.olafkock.liferay.navigation.util.ConfigurationUtil;
import de.olafkock.liferay.navigation.util.CustomNavigationKeys;
import de.olafkock.liferay.navigation.util.GroupConfig;

@Component(
		property = { "key=servlet.service.events.pre" },
		service = LifecycleAction.class
)
public class CustomNavigationPreAction extends Action {

	@Override
	public void run(HttpServletRequest request, HttpServletResponse response) throws ActionException {
		try {
			ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
			final long scopeGroupId = themeDisplay.getScopeGroupId();
			long companyId = themeDisplay.getCompanyId();
			Layout layout = themeDisplay.getLayout();
			List<Layout> layouts = getLayouts(scopeGroupId, layout.isPublicLayout(), companyId);
			if(! layouts.isEmpty()) {
				themeDisplay.setLayouts(layouts);
			}
		} catch (SystemException e1) {
			e1.printStackTrace();
		} catch (Exception ignore) {
			ignore.printStackTrace();
		}		
	}
	
	private LinkedList<Layout> getLayouts(final long scopeGroupId,
			boolean publicLayout, long companyId) throws PortalException, SystemException {
		LinkedList<Layout> layouts = new LinkedList<Layout>();
		ExpandoColumn column = getNavigationColumn(publicLayout, companyId);
		ExpandoValue value = ExpandoValueLocalServiceUtil.getValue(column.getTableId(), column.getColumnId(), scopeGroupId);
		if(value != null) {
			String[] configValues = value.getStringArray();
			List<GroupConfig> decodedGroups = ConfigurationUtil.decode(configValues);
			if((decodedGroups.size() > 1 )) { 
				for(GroupConfig groupConfig:decodedGroups) {
					layouts.addAll(LayoutServiceUtil.getLayouts(groupConfig.getGroupId(), groupConfig.isPrivate(), 0L)); // toplevel pages (0L)
				}
			}
		}
		return layouts;
	}	
	
	/***
	 *  Get a reference to the ExpandoTable (for a group/site), 
	 *  create if not yet existing
	 */
	private ExpandoTable getGroupExpandoTable(long companyId)
			throws PortalException, SystemException {
		ExpandoTable table = null;
		table = expandoTableLocalService.getDefaultTable(
			 	companyId, Group.class.getName());
		if(table == null) {
		 	table = expandoTableLocalService.addDefaultTable(
			 	companyId, Group.class.getName());
		}
		return table;
	}

	
	private ExpandoColumn getNavigationColumn(boolean publicLayouts, long companyId) throws PortalException, SystemException {
		long tableId = getGroupExpandoTable(companyId).getTableId();
		String name = publicLayouts ? 
				CustomNavigationKeys.PUBLIC_GROUP_IDS : 
				CustomNavigationKeys.PRIVATE_GROUP_IDS;

		ExpandoColumn column = expandoColumnLocalService.getColumn(tableId, name);

		if(column == null) {
			column = expandoColumnLocalService.addColumn(
				tableId, name, ExpandoColumnConstants.STRING_ARRAY);
		
			// Add Unicode Properties
		
			UnicodeProperties properties = new UnicodeProperties();
			properties.setProperty(
					ExpandoColumnConstants.INDEX_TYPE, Boolean.FALSE.toString());
			column.setTypeSettingsProperties(properties);
			column.setDefaultData("unconfigured");
			expandoColumnLocalService.updateExpandoColumn(column);
		}
		return column;
	}

	@Reference
	private LayoutLocalService layoutLocalService;
	@Reference
	private LayoutService layoutService;
	@Reference
	private ExpandoTableLocalService expandoTableLocalService;
	@Reference
	private ExpandoColumnLocalService expandoColumnLocalService;
	@Reference
	private ExpandoValueLocalService expandoValueLocalService;
}