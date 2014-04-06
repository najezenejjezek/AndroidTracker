
<%@ page import="androidtrackerweb.Location" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'location.label', default: 'Location')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-location" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-location" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list location">
			
				<g:if test="${locationInstance?.longitude}">
				<li class="fieldcontain">
					<span id="longitude-label" class="property-label"><g:message code="location.longitude.label" default="Longitude" /></span>
					
						<span class="property-value" aria-labelledby="longitude-label"><g:fieldValue bean="${locationInstance}" field="longitude"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${locationInstance?.latitude}">
				<li class="fieldcontain">
					<span id="latitude-label" class="property-label"><g:message code="location.latitude.label" default="Latitude" /></span>
					
						<span class="property-value" aria-labelledby="latitude-label"><g:fieldValue bean="${locationInstance}" field="latitude"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${locationInstance?.time}">
				<li class="fieldcontain">
					<span id="time-label" class="property-label"><g:message code="location.time.label" default="Time" /></span>
					
						<span class="property-value" aria-labelledby="time-label"><g:formatDate date="${locationInstance?.time}" /></span>
					
				</li>
				</g:if>

                <g:if test="${locationInstance?.secUser}">
                    <li class="fieldcontain">
                        <span id="secUser-label" class="property-label"><g:message code="location.secUser.label" default="User" /></span>

                        <span class="property-value" aria-labelledby="secUser-label">${locationInstance?.secUser?.username}</span>

                    </li>
                </g:if>
			
				<g:if test="${locationInstance?.accuracy}">
				<li class="fieldcontain">
					<span id="accuracy-label" class="property-label"><g:message code="location.accuracy.label" default="Accuracy" /></span>
					
						<span class="property-value" aria-labelledby="accuracy-label"><g:fieldValue bean="${locationInstance}" field="accuracy"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${locationInstance?.provider}">
				<li class="fieldcontain">
					<span id="provider-label" class="property-label"><g:message code="location.provider.label" default="Provider" /></span>
					
						<span class="property-value" aria-labelledby="provider-label"><g:fieldValue bean="${locationInstance}" field="provider"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:locationInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${locationInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
