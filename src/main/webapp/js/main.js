var allCalendars = {};
var selectedDate = new Date();
var periodStart = new Date();
var periodEnd = new Date();

var addToCalendarMenu = function(calendarMenu, calendar) {
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
	}).change(displaySelectedCalendars).appendTo(menuEntry);

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

var addNewCalendarMenu = function(i, calendar) {
	if (calendar.accessRole == 'owner') {
		addToCalendarMenu($('#my_calendar_menu'), calendar);
	} else {
		addToCalendarMenu($('#other_calendar_menu'), calendar);
	}
};

var displaySelectedCalendars = function() {

	/* check calendar selector */
	// $(".calendar_menu_entry:checked").parent().css("border", "2px solid
	// green");
	// $(".calendar_menu_entry:not(:checked)").parent().css("border",
	// "2px solid red");
	/* remove non-checked calendars */
	$.each($(".calendar_menu_entry:not(:checked)"),
			function(i, entry) {
				var calendar = allCalendars[entry.id];
				var calendarDisplay = $('#'
						+ escapeSelector('calendar_' + calendar.id));
				if (calendarDisplay.length) {
					calendarDisplay.hide('slow').remove();
				}
			});

	/* iterate through list of selected calendars */
	$.each($('.calendar_menu_entry:checked'),
			function(i, entry) {
				var calendar = allCalendars[entry.id];

				var calendarDisplay = $('#'
						+ escapeSelector('calendar_' + calendar.id));
				if (!calendarDisplay.length) {
					var row = $('<tr>', {
						id : 'calendar_' + calendar.id
					}).appendTo($('#calendar_body'));
					$('<td/>').appendTo(row).append($('<span/>', {
						html : calendar.summary
					}));
					$('<td/>').appendTo(row).append($('<div/>', {
						id : 'events_' + calendar.id,
						html : 'loading event data...',
					}));
				}

				/* get calendar data and display */
				$.getJSON('rest/event', {
					action : 'get',
					id : calendar.id,
					timeMin : periodStart.toISOString(),
					timeMax : periodEnd.toISOString(),
				}, function(data) {
					$('#' + escapeSelector('events_' + calendar.id)).empty();
					if (data.items) {
						$.each(data.items, function(i, event) {
							displayEvent(calendar.id, event);
						});
					}
				});
			});

};

var displayEvent = function(calendarId, event) {
	var eventContent = event.summary + ' (' + event.start.date + ' - '
			+ event.start.dateTime + ')';
	$('<div/>', {
		'id' : event.id,
		'class' : 'event_display_entry',
		'html' : eventContent,
	}).appendTo($('#' + escapeSelector('events_' + calendarId)));
};

var getCalendars = function() {

	updatePeriods();
	createHeader();
	createBody();

	$.getJSON('rest/calendar', {
		action : 'get',
		id : 'all',
	}, function(data) {
		$.each(data.items, addNewCalendarMenu);
		displaySelectedCalendars();
	});
};

var createHeader = function() {
	/* navigation buttons */
	$('#calendar_navigation_today').button();
	$('#calendar_navigation_prev').button({
		icons : {
			primary : 'ui-icon-circle-triangle-w',
		},
		text : false,
	});
	$('#calendar_navigation_next').button({
		icons : {
			primary : 'ui-icon-circle-triangle-e',
		},
		text : false,
	});

	/* title */
	$('#display_title').text(dateStringShort(selectedDate));

	/* period selectors */
	$('#display_period').buttonset();
};

var createBody = function() {
};

var updatePeriods = function(periodType) {
	/*
	 * set periodStart and periodEnd depending on the periodType and
	 * selectedDate
	 */
	periodStart = new Date(selectedDate.valueOf());
	periodStart.setHours(0);
	periodStart.setMinutes(0);
	periodStart.setSeconds(0);
	periodStart.setMilliseconds(0);

	if (!periodType) {
		periodType = 'day';
	}
	switch (periodType) {
	case 'day':
		periodStart.setDate(selectedDate.getDate());
		periodEnd = new Date(periodStart.valueOf());
		periodEnd.setDate(periodStart.getDate() + 1);
		break;
	case 'week':
		/* start week on monday */
		periodStart.setDate(selectedDate.getDate()
				+ (1 - selectedDate.getDay()));
		periodEnd = new Date(periodStart.valueOf());
		periodEnd.setDate(periodEnd.getDate() + 7);
		break;
	case 'month':
		periodStart.setDate(1);
		periodEnd = new Date(periodStart.valueOf());
		periodEnd.setMonth(periodStart.getMonth() + 1);
		break;
	}
};

var escapeSelector = function(str) {
	if (str)
		return str.replace(/([ #;&,.+*~\':"!^$[\]()=>|\/@])/g, '\\$1');
	else
		return str;
};