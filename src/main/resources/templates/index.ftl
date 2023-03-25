<#-- @ftlvariable name="syncableProperties" type="kotlin.collections.Map<String, ch.bildspur.ledforest.configuration.sync.SyncableProperty>" -->
<!DOCTYPE html>
<html lang="en">
<head>
    <title>LEDA Web Configuration</title>
</head>
<body style="text-align: center; font-family: sans-serif">
<h1>Leda Web Configuration</h1>
<p><i>created by bildspur</i></p>
<hr>

<form>
<#list syncableProperties as propertyKey, property>
    <div class="form-group">
        <label for="input${propertyKey}">${propertyKey}</label>
        <p>${property.dataType.simpleName}</p>
        <#if property.dataType.simpleName == "Boolean">
            <input type="checkbox" class="form-control" id="input${propertyKey}" ${property.instance.value?string('checked', '')}>
        <#elseif property.dataType.simpleName == "Float">
            <input type="number" class="form-control" id="input${propertyKey}" value="${property.instance.value}">
        </#if>
    </div>
</#list>
</form>
<hr>
</body>
</html>