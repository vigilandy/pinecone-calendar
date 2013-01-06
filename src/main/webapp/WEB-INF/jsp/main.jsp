<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Pinecone</title>
<link href="css/ui-lightness/jquery-ui-1.9.2.custom.min.css"
	rel="stylesheet">
<link href="css/base.css" rel="stylesheet">
<link href="css/main.css" rel="stylesheet">
<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.2.custom.min.js"></script>
<script type="text/javascript" src="js/date.js"></script>
<script type="text/javascript" src="js/main.js"></script>
<script type="text/javascript">
	$(document).ready(function() {

		showCurrentTime();

		$('#button_login, #button_logout').button();

		if ('true' != $('#logged_in').val()) {
			return;
		}

		/* debugging data */
		$('.showAllCalendars').click(function() {
			$('#debug_data').html(JSON.stringify(allCalendars));
		});

		getCalendars();
	});

	var showCurrentTime = function() {
		$('#current_timestamp').empty();
		$('<span/>', {
			'html' : new Date().toLocaleDateString()
		}).appendTo($('#current_timestamp'));
		$('<span/>', {
			'html' : new Date().toLocaleTimeString()
		}).appendTo($('#current_timestamp'));
		setTimeout(showCurrentTime, 500);
	};
</script>
</head>
<body>

	<div id="main_content">

		<div id="header">
			<div id="user_data">
				<input type="hidden" id="logged_in" name="logged_in"
					value="${ sessionScope.loggedIn }" />
				<c:choose>
					<c:when test="${ sessionScope.loggedIn }">
						<span id="user_id">${ sessionScope.userId }</span>
						<a id="button_logout" href="logout"> <span
							class="ui-button-text">logout</span>
						</a>
					</c:when>
					<c:otherwise>
						<a id="button_login" href="login"> <span
							class="ui-button-text">login</span>
						</a>
					</c:otherwise>
				</c:choose>
			</div>
			<div class="clear"></div>
		</div>

		<c:if test="${ sessionScope.loggedIn }">
			<div id="sidebar">
				<h4>my calendars</h4>
				<div id="my_calendar_menu"></div>
				<h4>other calendars</h4>
				<div id="other_calendar_menu"></div>
			</div>

			<div id="main_display">
				<div id="calendar_display">
					<div id="display_header">
						<div id="calendar_navigation">
							<div id="calendar_navigation_today">today</div>
							<div id="calendar_navigation_prev">prev</div>
							<div id="calendar_navigation_next">next</div>
						</div>
						<div id="display_period">
							<input type="radio" id="display_period_day" name="display_period"
								checked="checked" value="day"><label
								for="display_period_day">day</label> <input type="radio"
								id="display_period_week" name="display_period" value="week"><label
								for="display_period_week">week</label>
						</div>
						<div id="display_title"></div>
					</div>
					<table id="calendar_body">
						<tbody></tbody>
					</table>
				</div>
			</div>
		</c:if>

		<div class="clear"></div>

		<div id="footer">
			<div id="current_timestamp"></div>
			<div>
				<span class="showAllCalendars">check all calendars</span>
			</div>
		</div>

		<div id="debug_data"></div>

	</div>


</body>
</html>