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
<#list syncableProperties as propertyKey, property>
    <div>
        <p>
            ${propertyKey}
        </p>
    </div>
</#list>
<hr>
</body>
</html>