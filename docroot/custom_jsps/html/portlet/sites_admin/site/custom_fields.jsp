<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ include file="/html/portlet/sites_admin/init.jsp" %>

<liferay-util:buffer var="original">
  <liferay-util:include page="/html/portlet/sites_admin/site/custom_fields.portal.jsp"/>
</liferay-util:buffer>

<%=original %>
<div class="portlet-msg-info">
<h3>
<liferay-ui:message key="customNavigationSites-help-headline"></liferay-ui:message>
</h3>
<p>
	<liferay-ui:message arguments="<%=themeDisplay.getScopeGroupId()%>" key="customNavigationSites-help-text-paragraph1-x"></liferay-ui:message>
</p><p>
	<liferay-ui:message arguments="<%=themeDisplay.getScopeGroupId()%>" key="customNavigationSites-help-text-paragraph2-x"></liferay-ui:message>
</p>
</div>