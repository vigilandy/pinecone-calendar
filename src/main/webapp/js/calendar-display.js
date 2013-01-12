var CalendarDisplay = function(mainDisplay, calendarMenu) {

  var self = this;

  this.allCalendars = {};
  this.selectedDate = null;
  this.timeframe = {
    start : new Date(),
    end : new Date(),
  };

  this.addToCalendarMenu = function(calendar) {
    this.allCalendars[calendar.id] = calendar;

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
    }).change(this.updateCalendarData).appendTo(menuEntry);

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

  this.createEventContainers = function(calendar) {
    // TODO

    /* delete previous event containers */
    // var eventContainerClass = 'events_' + calendarId;
    // $('.' + escapeSelector(eventContainerClass)).parent().remove();
  };

  this.displayEvent = function(calendarId, event) {
    // TODO
  };

  this.getSelectedTimeframe = function() {

    var displayPeriodButton = $('input[name=display_timeframe]:radio:checked');
    var currentPeriod = displayPeriodButton.length ? displayPeriodButton.val()
        : 'day';
    return currentPeriod;

  };

  this.initialize = function() {

    this.setupDisplayHeader();
    this.setupDisplayBody();
    this.setupCalendarMenu();
    this.initializeCalendarData();

  };

  this.initializeCalendarData = function() {

    $.getJSON('rest/calendar', {
      action : 'get',
      id : 'all',
    }, function(data) {
      $.each(data.items, function(i, calendar) {
        self.addToCalendarMenu(calendar);
      });

      self.updateCalendarData();
    });

  };

  this.setSelectedDate = function(newDate) {

    if (null != this.selectedDate
        && dateStringShort(this.selectedDate) == dateStringShort(newDate)) {
      return;
    }

    this.selectedDate = newDate;
    this.updateTimeframe();
    this.updateDisplayTitle();

  };

  this.setupCalendarMenu = function() {
  };

  this.setupCalendarNavigation = function() {
    var navDiv = $('<div>', {
      id : 'calendar_navigation'
    }).appendTo(displayHeader);

    $('<a>', {
      text : 'today',
      'class' : 'button_calendar_navigation',
      title : dateStringShort(new Date()),
    }).button().click(function() {
      self.moveSelectedDate('today');
    }).tooltip().appendTo(navDiv);

    $('<a>', {
      text : 'prev',
      'class' : 'button_calendar_navigation',
    }).button({
      icons : {
        primary : 'ui-icon-circle-triangle-w',
      },
      text : false,
    }).click(function() {
      self.moveSelectedDate('prev');
    }).appendTo(navDiv);

    $('<input>', {
      type : 'text',
      id : 'selected_date_display',
      'class' : 'button_calendar_navigation',
      readonly : 'readonly',
    }).change(function() {
      self.setSelectedDate($('#selected_date_display').datepicker('getDate'));
    }).datepicker({
      dateFormat : 'yy-mm-dd',
      showOtherMonths : true,
      selectOtherMonths : true,
      showAnim : 'fold',
    }).button().appendTo(navDiv);

    $('<a>', {
      text : 'next',
      'class' : 'button_calendar_navigation',
    }).button({
      icons : {
        primary : 'ui-icon-circle-triangle-e',
      },
      text : false,
    }).click(function() {
      self.moveSelectedDate('next');
    }).appendTo(navDiv);
  };

  this.setupDisplayBody = function() {
    displayBody = $('<div>', {
      id : 'display_body',
    }).appendTo(mainDisplay);
    // TODO
  };

  this.setupDisplayHeader = function() {
    displayHeader = $('<div>', {
      id : 'display_header',
    }).appendTo(mainDisplay);
    this.setupCalendarNavigation();
    this.setupDisplayTimeframe();
    this.setSelectedDate(new Date());
  };

  this.setupDisplayTimeframe = function() {

    var timeframeDiv = $('<div>', {
      id : 'calendar_timeframe'
    }).appendTo(displayHeader);

    $('<input>', {
      id : 'display_period_day',
      name : 'display_timeframe',
      type : 'radio',
      checked : 'checked',
      value : 'day'
    }).appendTo(timeframeDiv);

    $('<label>', {
      'for' : 'display_period_day',
      text : 'day',
    }).appendTo(timeframeDiv);

    $('<input>', {
      id : 'display_period_week',
      name : 'display_timeframe',
      type : 'radio',
      value : 'week',
    // disabled : 'disabled',
    }).appendTo(timeframeDiv);

    $('<label>', {
      'for' : 'display_period_week',
      text : 'week',
    }).appendTo(timeframeDiv);

    $('<input>', {
      id : 'display_period_month',
      name : 'display_timeframe',
      type : 'radio',
      value : 'month',
    // disabled : 'disabled',
    }).appendTo(timeframeDiv);

    $('<label>', {
      'for' : 'display_period_month',
      text : 'month',
    }).appendTo(timeframeDiv);

    timeframeDiv.buttonset();

  };

  this.updateDisplayTitle = function() {
    var display = dateStringShort(this.selectedDate);
    $('#selected_date_display').val(display);
  };

  this.moveSelectedDate = function(moveType) {
    var direction = 0;
    switch (moveType) {
      case 'today':
        if (dateStringShort(this.selectedDate) == dateStringShort(new Date())) {
          return;
        }
        this.selectedDate = new Date();
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
      switch (this.getSelectedTimeframe()) {
        case 'day':
          this.selectedDate.setDate(this.selectedDate.getDate() + direction);
          break;
        case 'week':
          this.selectedDate.setDate(this.selectedDate.getDate()
              + (direction * 7));
          break;
        case 'month':
          this.selectedDate.setMonth(this.selectedDate.getMonth() + direction);
          break;
      }
    }

    this.updateTimeframe();
    this.updateDisplayTitle();
    this.updateCalendarData();
  };

  this.updateCalendarData = function() {
    // TODO

    /* remove non-selected calendars */
    $.each($(".calendar_menu_entry:not(:checked)"), function(i, entry) {
      var calendar = self.allCalendars[entry.id];
      var calendarDisplay = $('#' + escapeSelector('calendar_' + calendar.id));
      if (calendarDisplay.length) {
        calendarDisplay.hide('slow').promise().done(function() {
          this.remove();
        });
      }
    });

    /* add selected calendars */
    $.each($('.calendar_menu_entry:checked'), function(i, entry) {
      var calendar = self.allCalendars[entry.id];
      var calendarDisplay = $('#' + escapeSelector('calendar_' + calendar.id));
      if (!calendarDisplay.length) {
        var styleString = 'color: ' + calendar.foregroundColor
            + '; background-color: ' + calendar.backgroundColor + ';';

        var row = $('<div>', {
          id : 'calendar_' + calendar.id,
          'class' : 'display_row hidden',
        }).appendTo($('#display_body'));
        $('<div>', {
          'class' : 'display_row_label',
          style : styleString,
        }).appendTo(row).append($('<span/>', {
          html : calendar.summary,
        }));
        row.show('slow');
      }

      /* create new event container(s) */
      // self.createEventContainers(calendar);
      /* get calendar data and display */
      // $.getJSON('rest/event', {
      // action : 'get',
      // id : calendar.id,
      // timeMin : self.timeframe.start.toISOString(),
      // timeMax : self.timeframe.end.toISOString(),
      // }, function(data) {
      // $('#' + escapeSelector(eventContainerId)).empty();
      // if (data.items) {
      // $.each(data.items, function(i, event) {
      // self.displayEvent(calendar.id, event);
      // });
      // }
      // });
    });

  };

  this.updateTimeframe = function() {

    /*
     * set timeframe depending on the selected time frame and the selected date
     */
    this.timeframe.start = new Date(this.selectedDate.valueOf());
    this.timeframe.start.setHours(0);
    this.timeframe.start.setMinutes(0);
    this.timeframe.start.setSeconds(0);
    this.timeframe.start.setMilliseconds(0);

    switch (this.getSelectedTimeframe()) {
      case 'day':
        this.timeframe.start.setDate(this.selectedDate.getDate());
        this.timeframe.end = new Date(this.timeframe.start.valueOf());
        this.timeframe.end.setDate(this.timeframe.start.getDate() + 1);
        break;
      case 'week':
        /* start week on sunday */
        this.timeframe.start.setDate(this.selectedDate.getDate()
            - this.selectedDate.getDay());
        this.timeframe.end = new Date(this.timeframe.start.valueOf());
        this.timeframe.end.setDate(this.timeframe.end.getDate() + 7);
        break;
      case 'month':
        this.timeframe.start.setDate(1);
        this.timeframe.end = new Date(this.timeframe.start.valueOf());
        this.timeframe.end.setMonth(this.timeframe.start.getMonth() + 1);
        break;
    }

  };

};

var escapeSelector = function(str) {
  if (str)
    return str.replace(/([ #;&,.+*~\':"!^$[\]()=>|\/@])/g, '\\$1');
  else
    return str;
};
