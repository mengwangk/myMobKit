<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<!--                                                               -->
<!-- Consider inlining CSS to reduce the number of requested files -->
<!--                                                               -->
<link type="text/css" rel="stylesheet" href="css/viewer.css">

<!--                                           -->
<!-- Any title is fine                         -->
<!--                                           -->
<title>Surveillance Photo Viewer</title>

<!--                                           -->
<!-- This script loads your compiled module.   -->
<!-- If you add any GWT meta tags, they must   -->
<!-- be added before this line.                -->
<!--                                           -->


<script type="text/javascript" language="javascript"
	src="mymobkitweb/mymobkitweb.nocache.js"></script>



</head>

<!--                                           -->
<!-- The body can have arbitrary html, or      -->
<!-- you can leave the body empty if you want  -->
<!-- to create a completely dynamic UI.        -->
<!--                                           -->
<body data-spy="scroll">

	<%@ include file="header.jsp"%>

	<%@ include file="common.jsp"%>

	<!-- OPTIONAL: include this if you want history support -->
	<iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1'
		style="position: absolute; width: 0; height: 0; border: 0"></iframe>

	<!-- RECOMMENDED if your web app will not function without JavaScript enabled -->
	<noscript>
		<div
			style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
			Your web browser must have JavaScript enabled in order for this
			application to display correctly.</div>
	</noscript>

	<div class="container content container-fluid" id="home">
		<h1>Surveillance Photo Viewer</h1>

		<div id="loginPanel"></div>
		
		<div id="message">Loading... please wait</div>
		
		<div id="gallery"></div>
	</div>
</body>
</html>
