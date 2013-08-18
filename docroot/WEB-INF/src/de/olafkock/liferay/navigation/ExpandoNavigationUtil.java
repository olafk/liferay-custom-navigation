package de.olafkock.liferay.navigation;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.LayoutServiceUtil;
import com.liferay.portlet.expando.DuplicateColumnNameException;
import com.liferay.portlet.expando.DuplicateTableNameException;
import com.liferay.portlet.expando.model.ExpandoColumn;
import com.liferay.portlet.expando.model.ExpandoColumnConstants;
import com.liferay.portlet.expando.model.ExpandoTable;
import com.liferay.portlet.expando.model.ExpandoValue;
import com.liferay.portlet.expando.service.ExpandoColumnLocalServiceUtil;
import com.liferay.portlet.expando.service.ExpandoTableLocalServiceUtil;
import com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil;

import java.util.LinkedList;
import java.util.List;

public class ExpandoNavigationUtil {
	/***
	 *  Get a reference to the ExpandoTable (for a group/site), 
	 *  create if not yet existing
	 */
	private static ExpandoTable getGroupExpandoTable(long companyId)
			throws PortalException, SystemException {
		ExpandoTable table = null;
	
		try {
		 	table = ExpandoTableLocalServiceUtil.addDefaultTable(
			 	companyId, Group.class.getName());
		}
		catch(DuplicateTableNameException dtne) {
		 	table = ExpandoTableLocalServiceUtil.getDefaultTable(
			 	companyId, Group.class.getName());
		}
		return table;
	}

	
	public static ExpandoColumn getNavigationColumn(long companyId) throws PortalException, SystemException {
		long tableId = getGroupExpandoTable(companyId).getTableId();
		String name = CustomNavigationKeys.GROUP_IDS;
		try {
			ExpandoColumn column = ExpandoColumnLocalServiceUtil.addColumn(
				tableId, name, ExpandoColumnConstants.STRING_ARRAY);
		
			// Add Unicode Properties
		
			UnicodeProperties properties = new UnicodeProperties();
			properties.setProperty(
					ExpandoColumnConstants.INDEX_TYPE, Boolean.FALSE.toString());
			column.setTypeSettingsProperties(properties);
			column.setDefaultData("unconfigured");
			ExpandoColumnLocalServiceUtil.updateExpandoColumn(column);
			return column;
		}
		catch(DuplicateColumnNameException dcne) {
			return ExpandoColumnLocalServiceUtil.getColumn(
					tableId, name);
		}
	}


	public static LinkedList<Layout> getLayouts(final long scopeGroupId,
			long companyId) throws PortalException, SystemException {
		ExpandoColumn column = getNavigationColumn(companyId);
		ExpandoValue value = ExpandoValueLocalServiceUtil.getValue(column.getTableId(), column.getColumnId(), scopeGroupId);
		String[] groupIds = StringUtil.splitLines(value.getStringArray()[0]);
		List<GroupConfig> decodedGroups = ConfigurationUtil.decode(groupIds);
		LinkedList<Layout> layouts = new LinkedList<Layout>();
		if((decodedGroups.size() > 1 )) { 
			for(GroupConfig groupConfig:decodedGroups) {
				layouts.addAll(LayoutServiceUtil.getLayouts(groupConfig.getGroupId(), groupConfig.isPrivate(), 0L)); // toplevel pages (0L)
			}
		}
		return layouts;
	}

}
