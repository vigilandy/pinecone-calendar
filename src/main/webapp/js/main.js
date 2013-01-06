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

	/* remove non-selected calendars */
	$.each($(".calendar_menu_entry:not(:checked)"),
			function(i, entry) {
				var calendar = allCalendars[entry.id];
				var calendarDisplay = $('#'
						+ escapeSelector('calendar_' + calendar.id));
				if (calendarDisplay.length) {
					calendarDisplay.hide('slow').remove();
				}
			});

	/* add selected calendars */
	$.each($('.calendar_menu_entry:checked'),
			function(i, entry) {
				var calendar = allCalendars[entry.id];

				var calendarDisplay = $('#'
						+ escapeSelector('calendar_' + calendar.id));
				var eventContainerId = 'events_'
						+ dateStringShort(selectedDate) + '_' + calendar.id;
				var eventContainerClass = 'events_' + calendar.id;
				if (!calendarDisplay.length) {
					var styleString = 'color: ' + calendar.foregroundColor
							+ '; background-color: ' + calendar.backgroundColor
							+ ';';

					var row = $('<tr>', {
						id : 'calendar_' + calendar.id
					}).appendTo($('#calendar_body'));
					$('<td/>', {
						style : styleString,
					}).appendTo(row).append($('<span/>', {
						html : calendar.summary,
					}));
				}

				/* delete previous event containers */
				$('.' + escapeSelector(eventContainerClass)).parent().remove();

				/* create new event container(s) */
				// TODO create multiple containers depending on selected period
				$('<td/>').appendTo(
						$('#' + escapeSelector('calendar_' + calendar.id)))
						.append($('<div/>', {
							id : eventContainerId,
							'class' : eventContainerClass,
							html : 'loading event data...',
						}));

				/* get calendar data and display */
				$.getJSON('rest/event', {
					action : 'get',
					id : calendar.id,
					timeMin : periodStart.toISOString(),
					timeMax : periodEnd.toISOString(),
				}, function(data) {
					$('#' + escapeSelector(eventContainerId)).empty();
					if (data.items) {
						$.each(data.items, function(i, event) {
							displayEvent(calendar.id, event);
						});
					}
				});
			});

};

var displayEvent = function(calendarId, event) {

	var eventDate = event.start.date ? event.start.date : event.start.dateTime
			.substring(0, 10);
	var eventContainerId = 'events_' + eventDate + '_' + calendarId;
	if (!$('#' + escapeSelector(eventContainerId)).length) {
		return;
	}

	var eventContent = event.summary + ' (' + event.start.date + ' - '
			+ event.start.dateTime + ')';
	$('<div/>', {
		'id' : event.id,
		'class' : 'event_display_entry',
		'html' : eventContent,
	}).appendTo($('#' + escapeSelector(eventContainerId)));

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

var getPeriodType = function() {
	var displayPeriodButton = $('input[name=display_period]:radio:checked');
	return displayPeriodButton.length ? displayPeriodButton.val() : 'day';
};

var createHeader = function() {
	/* navigation buttons */
	$('#calendar_navigation_today').button().click(function() {
		updateSelectedDate('today');
	});
	$('#calendar_navigation_prev').button({
		icons : {
			primary : 'ui-icon-circle-triangle-w',
		},
		text : false,
	}).click(function() {
		updateSelectedDate('prev');
	});
	$('#calendar_navigation_next').button({
		icons : {
			primary : 'ui-icon-circle-triangle-e',
		},
		text : false,
	}).click(function() {
		updateSelectedDate('next');
	});

	/* title */
	updateDisplayTitle();

	/* period selectors */
	$('#display_period').buttonset();
	$('#display_period_week').button("disable");
};

var createBody = function() {
};

var updateDisplayTitle = function(periodType) {
	$('#display_title').text(dateStringShort(selectedDate));
};

var updatePeriods = function() {
	/*
	 * set periodStart and periodEnd depending on the periodType and
	 * selectedDate
	 */
	periodStart = new Date(selectedDate.valueOf());
	periodStart.setHours(0);
	periodStart.setMinutes(0);
	periodStart.setSeconds(0);
	periodStart.setMilliseconds(0);

	switch (getPeriodType()) {
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

var updateSelectedDate = function(moveType) {
	var direction = 0;
	switch (moveType) {
	case 'today':
		if (dateStringShort(selectedDate) == dateStringShort(new Date())) {
			return;
		}
		selectedDate = new Date();
		direction = 0;
		break;
	case 'prev':
		direction = -1;
		break;
	case 'next':
		direction = 1;
		break;
	}

	if (direction != 0) {
		switch (getPeriodType()) {
		case 'day':
			selectedDate.setDate(selectedDate.getDate() + direction);
			break;
		case 'week':
			selectedDate.setDate(selectedDate.getDate() + (direction * 7));
			break;
		case 'month':
			selectedDate.setMonth(selectedDate.getMonth() + direction);
			break;
		}
	}

	updateDisplayTitle();
	updatePeriods();
	displaySelectedCalendars();
};

var escapeSelector = function(str) {
	if (str)
		return str.replace(/([ #;&,.+*~\':"!^$[\]()=>|\/@])/g, '\\$1');
	else
		return str;
};