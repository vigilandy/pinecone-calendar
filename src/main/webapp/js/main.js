var allCalendars = {};
var selectedDate = new Date();

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
		'onchange' : 'displaySelectedCalendars();',
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

var addNewCalendarMenu = function(i, calendar) {
	if (calendar.accessRole == 'owner') {
		addToCalendarMenu($('#my_calendar_menu'), calendar);
	} else {
		addToCalendarMenu($('#other_calendar_menu'), calendar);
	}
};

var displaySelectedCalendars = function() {

	/* iterate through list of selected calendars */
	$.each($('.calendar_menu_entry:checked'), function(i, entry) {
		var calendar = allCalendars[entry.id];

		var calendarDisplayId = 'calendar_' + calendar.id;
		var calendarDisplay = $('#' + escapeSelector(calendarDisplayId));
		if (!calendarDisplay.length) {

		}

		/* get calendar data and display */
		// $.getJSON('rest/event', {
		// action : 'get',
		// id : calendar.id,
		// timeMin : periodStart.toISOString(),
		// timeMax : periodEnd.toISOString(),
		// }, function(data) {
		// if (data.items == null) {
		// return;
		// }
		// $.each(data.items, function(i, event) {
		// $.displayEvent(event);
		// });
		// });
	});

};

var getCalendars = function() {
	$.getJSON('rest/calendar', {
		action : 'get',
		id : 'all',
	}, function(data) {
		$.each(data.items, addNewCalendarMenu);
		displaySelectedCalendars();
	});
};

function escapeSelector(str) {
	if (str)
		return str.replace(/([ #;&,.+*~\':"!^$[\]()=>|\/@])/g, '\\$1');
	else
		return str;
}