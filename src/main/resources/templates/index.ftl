<!DOCTYPE HTML>
<html>
<head> 
    <title>QCRI+IBM Data Connect - Ji's Demo</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<!--<link rel="stylesheet" href="/css/bootstrap.min.css" /> -->
     <link href="//netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/css/bootstrap-combined.min.css" rel="stylesheet"/>
</head>
<body>
<div class="container">
     <div class="control-group">	
		<h2 class="muted">Tweet Sense Disambiguation</h2>
    	<form name="question" action="getOutput" method="post">
            <div class="control-group">
                <label class="control-label" for="text">Validate Text</label>
            </div>
            <div class="controls">
				<textarea name="text" rows="5" cols="800" style="width: 98%;"></textarea>
            </div>
    		<div class="controls"> 
    			<input type="submit" class="btn btn-primary">
    		</div>
    	</form>
	 </div>
	<div>
	<#if finalOutputs?size != 0 >
    <table class="table table-bordered table-striped">
		<thead>
			<tr>
				<th>Text</th>
				<th>Person Centroid Score</th>
				<th>Country Centroid Score</th>
			</tr>
		</thead>
		<tbody>
			<#list finalOutputs as finalOutput>
				<tr>
					<td>${finalOutput.text}</td>
					<td>${finalOutput.person_score}</td>
					<td>${finalOutput.country_score}</td>
				</tr>
			</#list>
		</tbody>
    </table>
    </#if>
    </div>
</div>	
</body>
</html>