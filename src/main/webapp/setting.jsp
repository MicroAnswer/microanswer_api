<%--
  Created by IntelliJ IDEA.
  User: Microanswer
  Date: 2017/7/6
  Time: 14:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>设置</title>
</head>
<body>

<h2>封面图片上传</h2>
<form action="./phonemp3/uploadCover" method="post" enctype="multipart/form-data">
    <label for="imginput">选择封面图片:</label>
    <input id="imginput" type="file" accept="image/jpeg,image/png" name="file">
    <input type="submit" value="上传"/>
</form>
<hr/>
</body>
</html>
