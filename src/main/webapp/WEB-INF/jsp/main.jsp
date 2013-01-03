<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Pinecone</title>
<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
<script type="text/javascript">
	var allCalendars = {};
	allCalendars.calendars = [];

	$(document).ready(
			function() {

				var loggedIn = $('#logged_in').val();

				if (loggedIn == 'true') {

					$('#message').text("loading calendars...");
					$.getJSON('json/calendars', function(data) {
						$('#message').hide('fast');
						$.each(data.items, function(i, item) {

							allCalendars.calendars.push(item);

							var calDiv = $('<div/>', {
								'id' : item.id,
								'class' : 'calendar, hidden',
							});

							var styleString = 'color: ' + item.foregroundColor
									+ '; background-color: '
									+ item.backgroundColor + ';';

							$('<span/>', {
								'style' : styleString,
								html : item.summary
							}).appendTo(calDiv);

							calDiv.appendTo('#my_calendars').show('slow');

						});
					});

				}

			});
</script>
<style type="text/css">
body,div {
	margin: 0px;
	padding: 0px;
}

#header {
	margin: 10px;
}

#user_data {
	float: right;
}

.clear {
	clear: both;
}

#my_calendars {
	float: left;
	max-width: 200px;
	margin-left: 20px;
}

.calendar {
	margin: 1em;
	border: 1px solid grey;
}

#calendar_display {
	border: 2px solid black;
	margin-left: 200px;
	min-width: 400px;
}

.hidden {
	display: none;
}

#footer {
	padding-top: 15px;
	color: #355;
	font-size: .7em;
}

#footer_timestamp {
	float: right;
}
</style>
</head>
<body>

	<div id="header">
		<div id="user_data">
			<input type="hidden" id="logged_in" name="logged_in"
				value="${ sessionScope.loggedIn }" />
			<c:choose>
				<c:when test="${ sessionScope.loggedIn }">
					<span id="user_id">${ sessionScope.userId }</span>
					<a href="logout">logout</a>
				</c:when>
				<c:otherwise>
					<a href="login">login</a>
				</c:otherwise>
			</c:choose>
		</div>
		<div class="clear"></div>
	</div>

	<div id="main_content">
		<div id="my_calendars">
			<div class="calendars_title">My Calendars</div>
			<div id="message">${ message }</div>
		</div>

		<div id="calendar_display">display</div>
	</div>

	<div class="clear"></div>

	<div id="footer">
		<span id="footer_timestamp">${ timestamp }</span> <span
			onclick="alert(JSON.stringify(allCalendars))">check all
			calendars</span>
	</div>

</body>
</html>