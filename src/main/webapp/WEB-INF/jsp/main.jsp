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

	$.addToCalendarMenu = function(calendarMenu, calendar) {
		allCalendars[calendar.id] = calendar;

		var styleString = 'color: ' + calendar.foregroundColor
				+ '; background-color: ' + calendar.backgroundColor + ';';

		var menuEntry = $('<div/>', {
			'style' : styleString,
			'class' : 'calendar_menu_entry_container hidden',
		});

		var entryCheckbox = $('<input/>', {
			'type' : 'checkbox',
			'id' : calendar.id,
			'class' : 'calendar_menu_entry',
			'onchange' : '$.displaySelectedCalendars();',
		}).appendTo(menuEntry);

		/* check the user's calendar by default */
		if (calendar.id == $('#user_id').text()) {
			entryCheckbox.prop('checked', true);
		}

		$('<label/>', {
			'for' : calendar.id,
			html : calendar.summary
		}).appendTo(menuEntry);

		// 		menuEntry.insertAfter(calendarMenu).show('slow');
		menuEntry.appendTo(calendarMenu).show('slow');
	};

	$.addNewCalendarMenu = function(i, calendar) {
		if (calendar.accessRole == 'owner') {
			$.addToCalendarMenu($('#my_calendar_menu'), calendar);
		} else {
			$.addToCalendarMenu($('#other_calendar_menu'), calendar);
		}
	};

	$.displaySelectedCalendars = function() {

		var selectedPanel = $('#display_tabs div.ui-tabs-panel:not(.ui-tabs-hide)');

		selectedPanel.empty();

		/* get list of selected calendars */
		$.each($('.calendar_menu_entry:checked'), function(i, entry) {
			calendar = allCalendars[entry.id];

			var content = 'calendar ' + calendar.summary + ' selected, id='
					+ calendar.id;

			$('<div>', {
				html : content,
			}).appendTo(selectedPanel);

			/*  get calendar data and display */
			$.getJSON('rest/events', {
				action : 'get',
				id : calendar.id,
			}, function(data) {
				$.each(data.items, function(i, event) {
				});
			});

		});

		$('#display_tabs').refresh;
	};

	$(document).ready(function() {

		var loggedIn = $('#logged_in').val();
		if (loggedIn != 'true') {
			return;
		}

		$('#display_tabs').tabs({
			heightStyle : "content",
			activate : function(event, ui) {
				$.displaySelectedCalendars();
			},
		});

		/* get calenders */
		var loadingMessage = $('<span/>', {
			'class' : 'message',
			'html' : "loading calendars..."
		}).appendTo('#my_calendar_menu');

		$.getJSON('rest/calendar', {
			action : 'get',
			id : 'all',
		}, function(data) {
			$.each(data.items, $.addNewCalendarMenu);
			loadingMessage.hide('fast').remove();
			$.displaySelectedCalendars();
		});

	});
</script>
<style type="text/css">
#my_calendar_menu {
	
}

.calendar_menu_entry_container {
	margin-bottom: 2px;
}

#display_tabs {
	/* this is needed to fix a bug with the tab header height when used with a floating div nearby */
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

		<c:if test="${ sessionScope.loggedIn }">
			<div id="sidebar">
				<h4>my calendars</h4>
				<div id="my_calendar_menu"></div>
				<h4>other calendars</h4>
				<div id="other_calendar_menu"></div>
			</div>

			<div id="main_display">
				<div id="display_tabs">
					<ul>
						<li><a href="#tabs-1">day</a></li>
						<li><a href="#tabs-2">week</a></li>
						<li><a href="#tabs-3">month</a></li>
					</ul>
					<div id="tabs-1"></div>
					<div id="tabs-2"></div>
					<div id="tabs-3"></div>
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
		</div>

	</div>


</body>
</html>