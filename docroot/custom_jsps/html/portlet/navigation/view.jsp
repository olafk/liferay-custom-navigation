<%--
/**
 * Copyright (c) 2013 Olaf Kock. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@page import="javax.sound.midi.SysexMessage"%>
<%@page import="java.util.LinkedList"%>
<%@page import="com.liferay.portal.service.LayoutServiceUtil"%>
<%@page import="com.liferay.portal.kernel.util.StringUtil"%>
<%@page import="com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil"%>
<%@page import="com.liferay.portlet.expando.model.ExpandoValue"%>
<%@page import="com.liferay.portal.kernel.util.UnicodeProperties"%>
<%@page import="com.liferay.portlet.expando.model.ExpandoColumnConstants"%>
<%@page import="com.liferay.portlet.expando.service.ExpandoColumnLocalServiceUtil"%>
<%@page import="com.liferay.portal.kernel.exception.SystemException"%>
<%@page import="com.liferay.portal.kernel.exception.PortalException"%>
<%@page import="com.liferay.portlet.expando.model.ExpandoColumn"%>
<%@page import="com.liferay.portlet.expando.DuplicateTableNameException"%>
<%@page import="com.liferay.portlet.expando.service.ExpandoTableLocalServiceUtil"%>
<%@page import="com.liferay.portlet.expando.model.ExpandoTable"%>
<%@ include file="/html/portlet/navigation/init.jsp" %>

<%-- This page implements the original liferay-ui:navigation tag with the
     changes that are required to render the modified navigation introduced
     by this plugin.
     
     The code more or less is taken from that tag implementation and adapted
     where needed.
--%>

<%
boolean preview = PrefsParamUtil.getBoolean(portletPreferences, renderRequest, "preview");

String[] displayStyleDefinition = _getDisplayStyleDefinition(displayStyle);

if ((displayStyleDefinition != null) && (displayStyleDefinition.length != 0)) {
	headerType = displayStyleDefinition[0];
	includedLayouts = displayStyleDefinition[3];

	if (displayStyleDefinition.length > 4) {
		nestedChildren = GetterUtil.getBoolean(displayStyleDefinition[4]);
	}

	rootLayoutLevel = GetterUtil.getInteger(displayStyleDefinition[2]);
	rootLayoutType = displayStyleDefinition[1];
}

%>

<c:if test="<%= layout != null %>">

	<%
	Layout rootLayout = null;
	boolean hidden = false;

	List<Layout> branchLayouts = new ArrayList<Layout>();

	branchLayouts.add(layout);
	branchLayouts.addAll(layout.getAncestors());

	if (rootLayoutType.equals("relative")) {
		if ((rootLayoutLevel >= 0) && (rootLayoutLevel < branchLayouts.size())) {
			rootLayout = branchLayouts.get(rootLayoutLevel);
		}
		else {
			rootLayout = null;
		}
	}
	else if (rootLayoutType.equals("absolute")) {
		int ancestorIndex = branchLayouts.size() - rootLayoutLevel;

		if ((ancestorIndex >= 0) && (ancestorIndex < branchLayouts.size())) {
			rootLayout = branchLayouts.get(ancestorIndex);
		}
		else if (ancestorIndex == branchLayouts.size()) {
			rootLayout = null;
		}
		else {
			hidden = true;
		}
	}
	
	StringBundler sb = new StringBundler();

	if (!hidden) {
		_buildNavigation(rootLayout, layout, branchLayouts, themeDisplay, 1, includedLayouts, nestedChildren, sb);
	}
	%>

	<div class="nav-menu nav-menu-style-<%= bulletStyle %>">
		<c:choose>
			<c:when test='<%= headerType.equals("root-layout") && (rootLayout != null) %>'>
				<h2>
					<a href="<%= PortalUtil.getLayoutURL(rootLayout, themeDisplay) %>" <%= PortalUtil.getLayoutTarget(rootLayout) %>><%= rootLayout.getName(locale) %></a>
				</h2>
			</c:when>
			<c:when test='<%= headerType.equals("portlet-title") %>'>
				<h2><%= portletDisplay.getTitle() %></h2>
			</c:when>
			<c:when test='<%= headerType.equals("breadcrumb") %>'>
				<liferay-ui:breadcrumb />
			</c:when>
			<c:when test="<%= preview && (sb.length() == 0) %>">
				<div class="alert alert-info">
					<liferay-ui:message key="there-are-no-pages-to-display-for-the-current-page-level" />
				</div>
			</c:when>
		</c:choose>

		<%= sb.toString() %>
	</div>
</c:if>

<%!
private void _buildNavigation(Layout rootLayout, Layout selLayout, List<Layout> branchLayouts, ThemeDisplay themeDisplay, int layoutLevel, String includedLayouts, boolean nestedChildren, StringBundler sb) throws Exception {
	List<Layout> childLayouts = null;

	if (rootLayout != null) {
		childLayouts = rootLayout.getChildren(themeDisplay.getPermissionChecker());
	}
	else {
		try {
			childLayouts = getLayouts(selLayout.getGroupId(), selLayout.isPublicLayout(), selLayout.getCompanyId());
		} catch (Exception e) {
			System.err.println("ignoring " + e.getClass().getName() + " on page " + themeDisplay.getURLCurrent() );
			e.printStackTrace();
		}
		if(childLayouts.isEmpty()) {
			childLayouts = LayoutLocalServiceUtil.getLayouts(selLayout.getGroupId(), selLayout.isPrivateLayout(), LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);
		}
	}

	if (childLayouts.isEmpty()) {
		return;
	}

	StringBundler tailSB = null;

	if (!nestedChildren) {
		tailSB = new StringBundler();
	}

	sb.append("<ul class=\"layouts level-");
	sb.append(layoutLevel);
	sb.append("\">");

	for (Layout childLayout : childLayouts) {
		if (!childLayout.isHidden() && LayoutPermissionUtil.contains(themeDisplay.getPermissionChecker(), childLayout, ActionKeys.VIEW)) {
			boolean open = false;

			if (includedLayouts.equals("auto") && branchLayouts.contains(childLayout) && !childLayout.getChildren().isEmpty()) {
				open = true;
			}

			if (includedLayouts.equals("all")) {
				open = true;
			}

			String className = StringPool.BLANK;

			if (open) {
				className += "open ";
			}

			if ((selLayout.getLayoutId() == childLayout.getLayoutId()) &&
				(selLayout.isPublicLayout() == childLayout.isPublicLayout()) &&
				(childLayout.getGroupId() == themeDisplay.getScopeGroupId()) ) {
				className += "selected layoutId" + childLayout.getLayoutId() + " ";
			}

			sb.append("<li ");

			if (Validator.isNotNull(className)) {
				sb.append("class=\"");
				sb.append(className);
				sb.append("\" ");
			}

			sb.append("><a ");

			if (Validator.isNotNull(className)) {
				sb.append("class=\"");
				sb.append(className);
				sb.append("\" ");
			}

			sb.append("href=\"");
			sb.append(HtmlUtil.escapeHREF(PortalUtil.getLayoutURL(childLayout, themeDisplay)));
			sb.append("\" ");
			sb.append(PortalUtil.getLayoutTarget(childLayout));
			sb.append("> ");
			sb.append(HtmlUtil.escape(childLayout.getName(themeDisplay.getLocale())));
			sb.append("</a>");

			if (open) {
				StringBundler childLayoutSB = null;

				if (nestedChildren) {
					childLayoutSB = sb;
				}
				else {
					childLayoutSB = tailSB;
				}

				_buildNavigation(childLayout, selLayout, branchLayouts, themeDisplay, layoutLevel + 1, includedLayouts, nestedChildren, childLayoutSB);
			}

			sb.append("</li>");
		}
	}

	sb.append("</ul>");

	if (!nestedChildren) {
		sb.append(tailSB);
	}
}


public LinkedList<Layout> getLayouts(final long scopeGroupId, final boolean isPublic,
		long companyId) throws PortalException, SystemException {
	LinkedList<Layout> layouts = new LinkedList<Layout>();
		ExpandoTable table = ExpandoTableLocalServiceUtil.getDefaultTable(
			 	companyId, Group.class.getName());
		long tableId = table.getTableId();
		final String columnName = isPublic ? "customNavigationSitesPublic" : "customNavigationSitesPrivate";
		ExpandoColumn column = ExpandoColumnLocalServiceUtil.getColumn(tableId, columnName);
		ExpandoValue value = ExpandoValueLocalServiceUtil.getValue(column.getTableId(), column.getColumnId(), scopeGroupId);
		if(value != null) {
			String[] groupIds = StringUtil.splitLines(value.getStringArray()[0]);
			List<GroupConfig> decodedGroups = decode(groupIds);
			if((decodedGroups.size() > 1 )) { 
				for(GroupConfig groupConfig:decodedGroups) {
					layouts.addAll(LayoutServiceUtil.getLayouts(groupConfig.getGroupId(), groupConfig.isPrivate(), 0L)); // toplevel pages (0L)
				}
			}
		}
	return layouts;
}

// Code taken from ConfigurationUtil - as that class can't be used from a
// JSP running in portal class loader. 

public List<GroupConfig> decode(String[] strings) {
	int elements = strings == null? 0 : strings.length;
	ArrayList<GroupConfig> result = new ArrayList<GroupConfig>(elements);
	for(int i = 0; i < elements; i++) {
		GroupConfig element = decode(strings[i]);
		if(element != null) {
			result.add(element);
		}
	}
	return result;
}

private GroupConfig decode(String string) {
	String[] split = StringUtil.split(string);
	if(split == null || split.length!=2) {
		return null;
	}
	long groupId = Long.valueOf(split[0]);
	if(groupId == 0) {
		return null;
	}
	if(split[1].equals("public")) {
		return new GroupConfig(groupId, false);
	} else if(split[1].equals("private")) {
		return new GroupConfig(groupId, true);
	}
	return null;
}

// code taken from GroupConfig class as that class can't be used from
// a JSP running in portal classloader

public class GroupConfig {

	private final long groupId;
	private final boolean isPrivate;

	public GroupConfig(long groupId, boolean isPrivate) {
		this.groupId = groupId;
		this.isPrivate = isPrivate;
	}

	public long getGroupId() {
		return groupId;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

}

private String[] _getDisplayStyleDefinition(String displayStyle) {
	return PropsUtil.getArray("navigation.display.style", new Filter(displayStyle));
}

%>
