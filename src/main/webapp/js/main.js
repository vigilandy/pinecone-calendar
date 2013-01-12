var allCalendars = {};

var calendarDisplay = {
	selectedDate : new Date(),
	periodStart : new Date(),
	periodEnd : new Date(),
	periods : {
		day : {
			startHour : 8,
			endHour : 18,
			intervalMinutes : 30,

			displayCalendar : function(calendar) {
			},

			makeTimeSlots : function(bodyTimes) {
				$('<th>', {
					html : 'all day',
				}).appendTo(bodyTimes);

				var timeSlot = new Date(calendarDisplay.selectedDate
						.getMilliseconds());
				timeSlot.setHours(calendarDisplay.dayPeriod.startHour, 0, 0, 0);
				$('<th>', {
					id : 'time_slot_' + timeString(timeSlot),
					html : timeString(timeSlot),
				}).appendTo(bodyTimes);
				do {
					timeSlot.setMinutes(timeSlot.getMinutes()
							+ calendarDisplay.dayPeriod.intervalMinutes, 0, 0);
					$('<th>', {
						id : 'time_slot_' + timeString(timeSlot),
						html : timeString(timeSlot),
					}).appendTo(bodyTimes);
				} while (timeSlot.getHours() < calendarDisplay.dayPeriod.endHour);
			},

			setPeriod : function(display) {
				display.periodStart.setDate(display.selectedDate.getDate());
				display.periodEnd = new Date(display.periodStart.valueOf());
				display.periodEnd.setDate(display.periodStart.getDate() + 1);
			},

		},
		week : {

			setPeriod : function(display) {
				/* start week on monday */
				display.periodStart.setDate(display.selectedDate.getDate()
						+ (1 - display.selectedDate.getDay()));
				display.periodEnd = new Date(display.periodStart.valueOf());
				display.periodEnd.setDate(display.periodEnd.getDate() + 7);
			},

		},
		month : {

			setPeriod : function(display) {
				display.periodStart.setDate(1);
				display.periodEnd = new Date(display.periodStart.valueOf());
				display.periodEnd.setMonth(display.periodStart.getMonth() + 1);
			},

		},
	},

	createDisplayHeader : function() {
		/* navigation buttons */
		$('#calendar_navigation_today').button().click(function() {
			this.updateSelectedDate('today');
		});
		$('#calendar_navigation_prev').button({
			icons : {
				primary : 'ui-icon-circle-triangle-w',
			},
			text : false,
		}).click(function() {
			this.updateSelectedDate('prev');
		});
		$('#calendar_navigation_next').button({
			icons : {
				primary : 'ui-icon-circle-triangle-e',
			},
			text : false,
		}).click(function() {
			this.updateSelectedDate('next');
		});

		/* title */
		updateDisplayTitle();

		/* period selectors */
		$('#display_period').buttonset();
		$('#display_period_week').button("disable");
	},

	createDisplayBody : function() {

	},

	displayCalendar : function(calendar) {
		getCurrentPeriod().displayCalendar(calendar);
	},

	getCurrentPeriod : function() {
		var displayPeriodButton = $('input[name=display_period]:radio:checked');
		var currentPeriod = displayPeriodButton.length ? displayPeriodButton
				.val() : 'day';
		return this.periods[currentPeriod];
	},

	updatePeriods : function() {
		/*
		 * set periodStart and periodEnd depending on the periodType and
		 * selectedDate
		 */
		this.periodStart = new Date(this.selectedDate.valueOf());
		this.periodStart.setHours(0);
		this.periodStart.setMinutes(0);
		this.periodStart.setSeconds(0);
		this.periodStart.setMilliseconds(0);

		this.getCurrentPeriod().setPeriod(this);

	},

	updateSelectedDate : function(moveType) {
		var direction = 0;
		switch (moveType) {
		case 'today':
			if (dateStringShort(calendarDisplay.selectedDate) == dateStringShort(new Date())) {
				return;
			}
			calendarDisplay.selectedDate = new Date();
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
				calendarDisplay.selectedDate
						.setDate(calendarDisplay.selectedDate.getDate()
								+ direction);
				break;
			case 'week':
				calendarDisplay.selectedDate
						.setDate(calendarDisplay.selectedDate.getDate()
								+ (direction * 7));
				break;
			case 'month':
				calendarDisplay.selectedDate
						.setMonth(calendarDisplay.selectedDate.getMonth()
								+ direction);
				break;
			}
		}

		updateDisplayTitle();
		updatePeriods();
		displaySelectedCalendars();
	},

};

$(function() {

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

var addNewCalendarMenu = function(i, calendar) {
	if (calendar.accessRole == 'owner') {
		addToCalendarMenu($('#my_calendar_menu'), calendar);
	} else {
		addToCalendarMenu($('#other_calendar_menu'), calendar);
	}
};

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

var createDisplayBody = function() {
	$('#display_body tbody').empty();

	var bodyTimes = $('<tr/>', {
		id : 'display_body_times'
	}).append($('<th/>', {
		html : 'calander'
	})).appendTo($('#display_body tbody'));

	switch (getPeriodType()) {
	case 'day':
		calendarDisplay.dayPeriod.makeTimeSlots(bodyTimes);
		break;
	case 'week':
		calendarDisplay.weekPeriod.makeTimeSlots(bodyTimes);
		break;
	case 'month':
		calendarDisplay.monthPeriod.makeTimeSlots(bodyTimes);
		break;
	}

	$('#display_body tbody').sortable({
		items : ":not(#display_body_times)",
		containment : "parent",
		cursor : "move",
		opacity : 0.5,
		revert : true,
	});
};

var createDisplayHeader = function() {
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

var displayEvent = function(calendarId, event) {

	var eventDate = event.start.date ? event.start.date : event.start.dateTime
			.substring(0, 10);
	var eventContainerId = 'events_' + eventDate + '_' + calendarId;
	if (!$('#' + escapeSelector(eventContainerId)).length) {
		return;
	}

	var eventContent = event.summary;
	// + ' (' + event.start.date + ' - '
	// + event.start.dateTime + ')';
	$('<div/>', {
		'id' : event.id,
		'class' : 'event_display_entry',
		'html' : eventContent,
	}).appendTo($('#' + escapeSelector(eventContainerId)));

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
				var eventContainerId = 'events_'
						+ dateStringShort(calendarDisplay.selectedDate) + '_'
						+ calendar.id;
				var eventContainerClass = 'events_' + calendar.id;

				var calendarDisplay = $('#'
						+ escapeSelector('calendar_' + calendar.id));
				if (!calendarDisplay.length) {
					var styleString = 'color: ' + calendar.foregroundColor
							+ '; background-color: ' + calendar.backgroundColor
							+ ';';

					var row = $('<tr>', {
						id : 'calendar_' + calendar.id
					}).appendTo($('#display_body tbody'));
					$('<td/>', {
						'class' : 'display_row_label',
						style : styleString,
					}).appendTo(row).append($('<span/>', {
						html : calendar.summary,
					}));
				}

				/* delete previous event containers */
				$('.' + escapeSelector(eventContainerClass)).parent().remove();

				/* create new event container(s) */
				calendarDisplay.createEventContainers(calendar);

				/* get calendar data and display */
				$.getJSON('rest/event', {
					action : 'get',
					id : calendar.id,
					timeMin : calendarDisplay.periodStart.toISOString(),
					timeMax : calendarDisplay.periodEnd.toISOString(),
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

var escapeSelector = function(str) {
	if (str)
		return str.replace(/([ #;&,.+*~\':"!^$[\]()=>|\/@])/g, '\\$1');
	else
		return str;
};

var getCalendars = function() {

	calendarDisplay.updatePeriods();
	calendarDisplay.createDisplayHeader();
	calendarDisplay.createDisplayBody();

	$.getJSON('rest/calendar', {
		action : 'get',
		id : 'all',
	}, function(data) {
		$.each(data.items, addNewCalendarMenu);
		displaySelectedCalendars();
	});
};

var updateDisplayTitle = function(periodType) {
	$('#display_title').text(dateStringShort(calendarDisplay.selectedDate));
};
