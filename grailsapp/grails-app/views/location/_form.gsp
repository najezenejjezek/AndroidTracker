<%@ page import="androidtrackerweb.SecUser; androidtrackerweb.Location" %>


<div class="fieldcontain ${hasErrors(bean: locationInstance, field: 'latitude', 'error')} required">
    <label for="latitude">
        <g:message code="location.latitude.label" default="Latitude" />
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="latitude" maxlength="64" required="" value="${locationInstance?.latitude}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: locationInstance, field: 'longitude', 'error')} required">
	<label for="longitude">
		<g:message code="location.longitude.label" default="Longitude" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="longitude" maxlength="64" required="" value="${locationInstance?.longitude}"/>
</div>



<div class="fieldcontain ${hasErrors(bean: locationInstance, field: 'time', 'error')} required">
	<label for="time">
		<g:message code="location.time.label" default="Time" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="time" precision="day"  value="${locationInstance?.time}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: locationInstance, field: 'secUser', 'error')} ">
    <label for="secUser">
        <g:message code="location.secUser.label" default="User" />

    </label>
    <g:select name="secUser" from="${ SecUser.list()}" value="${locationInstance?.secUser?.id}"  optionKey="id" optionValue="username"/>
</div>

<div class="fieldcontain ${hasErrors(bean: locationInstance, field: 'accuracy', 'error')} ">
	<label for="accuracy">
		<g:message code="location.accuracy.label" default="Accuracy" />
		
	</label>
	<g:textField name="accuracy" value="${locationInstance?.accuracy}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: locationInstance, field: 'provider', 'error')} ">
	<label for="provider">
		<g:message code="location.provider.label" default="Provider" />
		
	</label>
	<g:textField name="provider" value="${locationInstance?.provider}"/>
</div>

