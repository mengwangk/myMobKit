<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>



<nav class="navbar navbar-default navbar-fixed-top" role="navigation">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="#" onclick="document.location='index.jsp#home'"><img src="img/mymobkit.png" alt="" border='0'></a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
         
          <ul class="nav navbar-nav navbar-right">
          
          	<%
			    UserService userService = UserServiceFactory.getUserService();
			    User user = userService.getCurrentUser();
			    if (user != null) {
			      pageContext.setAttribute("user", user);
			%>
			<li style="margin-top:25px;margin-right:30px"><span class="glyphicon glyphicon-user" aria-hidden="true"></span>&nbsp;&nbsp;&nbsp;${fn:escapeXml(user.nickname)}</li>
			
			<li><a a href="viewer.jsp"><span class="btn btn-large btn-info">Photo Viewer</span></a></li>			
			<li><a href="#" onclick="document.location='<%=userService.createLogoutURL(request.getRequestURI()) %>'" style="padding-right:0"><span class="btn btn-large btn-success">Logout</span></a></li>
			
			<%
			    } else {
			%>
			<li><a href="#" onclick="document.location='<%= userService.createLoginURL(request.getRequestURI()) %>'" style="padding-right:0"><span class="btn btn-large btn-success">Login</span></a></li>
         
            <%
			    }
			%>
          </ul>
        </div>
      </div>
    </nav>
    
    

