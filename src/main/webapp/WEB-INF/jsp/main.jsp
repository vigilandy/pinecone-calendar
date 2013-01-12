<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Pinecone</title>
<link href="css/pepper-grinder/jquery-ui-1.9.2.custom.min.css"
	rel="stylesheet">
<link href="css/calendar-display.css" rel="stylesheet">
<link href="css/main.css" rel="stylesheet">
<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.2.custom.min.js"></script>
<script type="text/javascript" src="js/date.js"></script>
<script type="text/javascript" src="js/calendar-display.js"></script>
<script type="text/javascript">
  var mydisplay;
  $(function() {
    $('#button_logout').button();

    mydisplay = new CalendarDisplay($('#main_display'), $('#sidebar'));
    mydisplay.initialize();
  });
</script>
</head>
<body class="ui-widget ui-widget-content">

	<div id="main_content">

		<div id="header">
			<div id="user_data">
				<span id="user_id">${ sessionScope.userId }</span> <a
					id="button_logout" href="logout">logout</a>
			</div>
		</div>

		<div id="sidebar"></div>
		<div id="main_display"></div>

	</div>


</body>
</html>