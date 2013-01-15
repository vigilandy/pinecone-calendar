<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Pinecone</title>
<link
  href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.9.2/themes/pepper-grinder/jquery-ui.css"
  type="text/css" rel="Stylesheet" />
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.9.2/jquery-ui.min.js"></script>
<script type="text/javascript">
	$(function() {
		$('#button_login').button();
	});
</script>
<style type="text/css">
div {
  text-align: center;
  font-weight: bold;
}
</style>
</head>
<body>
  <div>successfully logged out</div>
  <div>
    <a id="button_login" href="main">login</a>
  </div>
</body>
</html>