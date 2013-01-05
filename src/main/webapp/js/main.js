var allCalendars = {};
var events = {};
var selectedDate = new Date();
var periodStart = new Date();
var periodEnd = new Date();
var selectedPanel;
var selectedTabIndex = 0;

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

	selectedPanel = $('#display_tabs div.ui-tabs-panel:not(.ui-tabs-hide)');
	selectedPanel.empty();
	selectedTabIndex = $('#display_tabs').tabs("option", "active");
	$.createDisplayHeader();
	$.createDisplayBody();

	/* iterate through list of selected calendars */
	$.each($('.calendar_menu_entry:checked'), function(i, entry) {
		var calendar = allCalendars[entry.id];

		/* get calendar data and display */
		$.getJSON('rest/event', {
			action : 'get',
			id : calendar.id,
			timeMin : periodStart.toISOString(),
			timeMax : periodEnd.toISOString(),
		}, function(data) {
			if (data.items == null) {
				return;
			}
			$.each(data.items, function(i, event) {
				$.displayEvent(event);
			});
		});

	});

	$('#display_tabs').refresh;
};

$.displayEvent = function(event) {
	var eventContent = event.summary + ' (' + event.start.date + ' - '
			+ event.start.dateTime + ')';
	var eventDate = event.start.date == null ? event.start.dateTime.substring(
			0, 10) : event.start.date;

	if ($('#display_' + eventDate) != null) {
		$('<div/>', {
			'id' : event.id,
			'class' : 'event_display_entry',
			'html' : eventContent,
		}).appendTo($('#display_' + eventDate));
	}
};

$.createDisplayHeader = function() {

	/*
	 * set periodStart and periodEnd depending on the selected tab and
	 * selectedDate
	 */
	/* selectedTabIndex: 0 = day, 1 = week, 2 = month */
	periodStart = new Date(selectedDate.valueOf());
	periodStart.setHours(0);
	periodStart.setMinutes(0);
	periodStart.setSeconds(0);
	periodStart.setMilliseconds(0);

	switch (selectedTabIndex) {
	case 0:
		periodStart.setDate(selectedDate.getDate());
		periodEnd = new Date(periodStart.valueOf());
		periodEnd.setDate(periodStart.getDate() + 1);
		break;
	case 1:
		/* start week on monday */
		periodStart.setDate(selectedDate.getDate()
				+ (1 - selectedDate.getDay()));
		periodEnd = new Date(periodStart.valueOf());
		periodEnd.setDate(periodEnd.getDate() + 7);
		break;
	case 2:
		periodStart.setDate(1);
		periodEnd = new Date(periodStart.valueOf());
		periodEnd.setMonth(periodStart.getMonth() + 1);
		break;
	}

	var header = $('<div/>', {
		'class' : 'display_header'
	});
	header.text($.dateTimeStringShort(periodStart) + ' ~ '
			+ $.dateTimeStringShort(periodEnd));
	header.appendTo(selectedPanel);
};

$.createDisplayBody = function() {
	switch (selectedTabIndex) {
	case 0:
		$.createDisplayBodyDay();
		break;
	case 1:
		$.createDisplayBodyWeek();
		break;
	case 2:
		$.createDisplayBodyMonth();
		break;
	}
};

$.createDisplayBodyDay = function() {
	$('<div/>', {
		'id' : 'display_' + $.dateStringShort(selectedDate),
	}).appendTo(selectedPanel);
};

$.createDisplayBodyWeek = function() {
	// TODO
	var table = $('<table/>').appendTo(selectedPanel);
	var header = $('<tr/>').appendTo(table);
	var row = $('<tr/>').appendTo(table);

	for ( var day = 0; day < 7; day++) {

	}

};
$.createDisplayBodyMonth = function() {
	// TODO
};

$.showCurrentTime = function() {
	$('#current_timestamp').empty();
	$('<span/>', {
		'html' : new Date().toLocaleDateString()
	}).appendTo($('#current_timestamp'));
	$('<span/>', {
		'html' : new Date().toLocaleTimeString()
	}).appendTo($('#current_timestamp'));
	setTimeout($.showCurrentTime, 500);
};

$(document).ready(function() {

	$.showCurrentTime();
	$('.showAllCalendars').click(function() {
		$('<div/>', {
			'html' : JSON.stringify(allCalendars),
		}).dialog();
	});

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
