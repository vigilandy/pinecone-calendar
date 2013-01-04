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
<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.2.custom.min.js"></script>
<script type="text/javascript" src="js/main.js"></script>
<script type="text/javascript">
	var allCalendars = {};
	allCalendars.calendars = [];

	$(function() {
		$('#display_tabs').tabs({
			heightStyle : "auto",
			activate : function(event, ui) {
				$.displayCalendar(ui.newPanel);
			},
		});
	});

	$.addCalendar = function(i, item) {
		// alert('adding calendar #' + i + ', ' + item.summary);
		allCalendars.calendars.push(item);

		var styleString = 'color: ' + item.foregroundColor
				+ '; background-color: ' + item.backgroundColor + ';';

		var menuEntry = $('<div/>', {
			'style' : styleString,
			'class' : 'calendar_menu_entry_container hidden',
		});

		$('<input/>', {
			'type' : 'checkbox',
			'id' : item.id,
			'class' : 'calendar_menu_entry',
		}).appendTo(menuEntry);

		$('<label/>', {
			'for' : item.id,
			html : item.summary
		}).appendTo(menuEntry);

		menuEntry.appendTo('#my_calendar_menu').show('slow');

	};

	$.displayCalendar = function(displayPanel) {
		/* the index of the display tab currently selected */
		var activeTab = $('#display_tabs').tabs("option", "active");
		alert('select tab: ' + activeTab);

		/* get list of selected calendars */
		// get all .calendar_menu_entry checkbox id's
		$.each($('.calendar_menu_entry:checked'), function(i, entry) {
			alert('checked? #' + i + ': ' + entry.id);
			/* get calendar data and display */
			// 			$.getJSON('rest/calendar')
		});
	};

	$(document).ready(function() {

		var loggedIn = $('#logged_in').val();

		if (loggedIn == 'true') {

			var message = $('<span/>', {
				'class' : 'message',
				'html' : "loading calendars..."
			}).appendTo('#my_calendar_menu');

			$.getJSON('rest/calendar', function(data) {
				$.each(data.items, $.addCalendar);
				message.hide('fast').remove();
			});

		}

	});
</script>
<style type="text/css">
#my_calendar_menu {
	
}

.calendar_menu_entry_container {
	margin-bottom: 2px;
}

#display_tabs {
	/* this is needed to fix a bug with the tab header height when used with a floating div nearby*/
	overflow: hidden;
}

.calendar {
	margin: 1em;
	border: 1px solid grey;
}
</style>
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
						<a id="button_logout" href="logout"
							class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">
							<span class="ui-button-text">logout</span>
						</a>
					</c:when>
					<c:otherwise>
						<a id="button_login" href="login"
							class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">
							<span class="ui-button-text">login</span>
						</a>
					</c:otherwise>
				</c:choose>
			</div>
			<div class="clear"></div>
		</div>

		<div id="sidebar">
			<div class="calendars_title">my calendars</div>
			<div id="my_calendar_menu" class="calendar_menu"></div>
			<div class="calendars_title">other calendars</div>
			<div id="other_calendar_menu" class="calendar_menu"></div>
		</div>

		<c:if test="${ sessionScope.loggedIn }">
			<div id="main_display">
				<div id="display_tabs">
					<ul>
						<li><a href="#tabs-1">day</a></li>
						<li><a href="#tabs-2">week</a></li>
						<li><a href="#tabs-3">month</a></li>
					</ul>
					<div id="tabs-1">
						<p>day display</p>
					</div>
					<div id="tabs-2">
						<p>week display</p>
					</div>
					<div id="tabs-3">
						<p>month display</p>
					</div>
				</div>
			</div>
		</c:if>

		<div class="clear"></div>

		<div id="footer">
			<div id="footer_timestamp">${ timestamp }</div>
			<div>
				<span onclick="alert(JSON.stringify(allCalendars))">check all
					calendars</span>
			</div>
			<div>
				<a href="rewrite-status">rewrite status</a>
			</div>
		</div>

	</div>


</body>
</html>