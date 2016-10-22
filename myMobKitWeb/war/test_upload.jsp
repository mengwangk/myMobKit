<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>

<%
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	String url = blobstoreService.createUploadUrl("/upload");
	url = url.replaceAll("MEKOH3", "127.0.0.1");
%>

<html>
    <head>
        <title>Upload Test</title>
    </head>
    <body>
        <form action="<%= url %>" method="post" enctype="multipart/form-data">
            <input type="text" name="email" value="mengwangk@gmail.com">
             <input type="text" name="name" value="testimages">
            <input type="file" name="images_1">
            <input type="file" name="images_2">
            <input type="submit" value="Submit">
        </form>
    </body>
</html>
