<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
if (request.getAttribute("room_link") == null) {
	response.sendRedirect("/");
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<title>myMobKit Spy</title>

<%@ include file="header.jsp" %>

<link rel="canonical" href="<%= (String)request.getAttribute("room_link") %>"/>
<link rel="stylesheet" type="text/css" href="css/spy.css" />

<script type="text/javascript" src="/_ah/channel/jsapi"></script>
<script type="text/javascript" src="/js/spy.js"></script>
<script type="text/javascript" src="/js/adapter.js"></script>
</head>

<body data-spy="scroll">

<%@ include file="common.jsp" %>

<script type="text/javascript">
  var errorMessages = [<%= (String)request.getAttribute("error_messages") %>];
  var channelToken = '<%= (String)request.getAttribute("token") %>';
  var me = '<%= (String)request.getAttribute("me") %>';
  var roomKey = '<%= (String)request.getAttribute("room_key") %>';
  var roomLink = '<%= (String)request.getAttribute("room_link") %>';
  var initiator = <%= (String)request.getAttribute("initiator") %>;
  var pcConfig = <%= (String)request.getAttribute("pc_config") %>;
  var pcConstraints = <%= (String)request.getAttribute("pc_constraints") %>;
  var offerConstraints = <%= (String)request.getAttribute("offer_constraints") %>;
  var mediaConstraints = <%= (String)request.getAttribute("media_constraints") %>;
  var turnUrl = '<%= (String)request.getAttribute("turn_url") %>';
  var stereo = <%= (String)request.getAttribute("stereo") %>;
  var audio_send_codec = '<%= (String)request.getAttribute("audio_send_codec") %>';
  var audio_receive_codec = '<%= (String)request.getAttribute("audio_receive_codec") %>';
  var viewMode = 1;
  setTimeout(initialize, 1);
  
  /**
  $(document).ready(function(){
	  if (initiator == 0) {
		  if (window.location.search.indexOf('r=') < 0) {
			  var separator = (window.location.href.indexOf("?")===-1)?"?":"&";
			  window.location.href = window.location.href + separator + 'r=<%=(String)request.getAttribute("room_key") %>';
		  }
	  }
  }); 
  **/
  
</script>
<div id="container" ondblclick="enterFullScreen()">
  <div id="card">
    <div id="local">
      <video id="localVideo" autoplay="autoplay" muted="true" controls/>
    </div>
    <div id="remote">
      <video id="remoteVideo" autoplay="autoplay" controls>
      </video>
      <div id="mini">
        <video id="miniVideo" autoplay="autoplay" muted="true" controls/>
      </div>
    </div>
  </div>
</div>

<footer id="status">
</footer>
<div id="infoDiv"></div>
<%@ include file="footer.jsp" %>
</body>
</html>