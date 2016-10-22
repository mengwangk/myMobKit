<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<title>myMobKit</title>

<%@ include file="header.jsp" %>

<script  type="text/javascript" src="/_ah/channel/jsapi"></script>

<style type="text/css">
  a:link { color: #ffffff; }
  a:visited {color: #ffffff; }
  html, body {
    background-color: #000000;
    height: 100%;
    font-family:Verdana, Arial, Helvetica, sans-serif;
  }
  body {
    margin: 0;
    padding: 0;
  }
  #container {
    position: relative;
    min-height: 100%;
    width: 100%;
    margin: 0px auto;
  }
  #footer {
    spacing: 4px;
    position: absolute;
    bottom: 0;
    width: 100%;
    height: 28px;
    background-color: #3F3F3F;
    color: rgb(255, 255, 255);
    font-size:13px; font-weight: bold;
    line-height: 28px;
    text-align: center;
  }
  #logo {
    display: block;
    top:4;
    right:4;
    position:absolute;
    float:right;
    #opacity: 0.8;
  }

</style>
</head>

<body data-spy="scroll">

<%@ include file="common.jsp" %>

<div id="container">
  <div id="footer">
    Sorry, this room is full.
    <a href="spy?r=<%= (String)request.getAttribute("room_key") %>">Click here</a> to try again.
  </div>
</div>

<%@ include file="footer.jsp" %>
</body>
</html>