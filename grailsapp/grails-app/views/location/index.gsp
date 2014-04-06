
<%@ page import="androidtrackerweb.Location" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'location.label', default: 'Location')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-location" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
            </ul>
		</div>
		<div id="list-location" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
                        <th>Coordinates</th>

						<g:sortableColumn property="time" title="${message(code: 'location.time.label', default: 'Time')}" />

                        <g:sortableColumn property="secUser" title="${message(code: 'location.secUser.label', default: 'User')}" />
					
						<g:sortableColumn property="accuracy" title="${message(code: 'location.accuracy.label', default: 'Accuracy')}" />
					
						<g:sortableColumn property="provider" title="${message(code: 'location.provider.label', default: 'Provider')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${locationInstanceList}" status="i" var="locationInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                        <g:set var="lat" value="${fieldValue(bean: locationInstance, field: "latitude")}" />
                        <g:set var="lon" value="${fieldValue(bean: locationInstance, field: "longitude")}" />

                        <td><a href="https://maps.google.cz/maps?q=${lat}+${lon}">${lat} ${lon}</a></td>

						<td><g:formatDate timeZone="CET" date="${locationInstance.time}" /></td>

                        <td>${locationInstance?.secUser?.username}</td>
					
						<td>${fieldValue(bean: locationInstance, field: "accuracy")}</td>
					
						<td>${fieldValue(bean: locationInstance, field: "provider")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${locationInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
