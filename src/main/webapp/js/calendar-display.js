function CalendarDisplay(mainDisplay, calendarMenu) {

  var self = this;

  this.allCalendars = {};
  this.selectedDate = null;
  this.timeframe = {
    start : new Date(),
    end : new Date(),
  };
  this.startHour = 7;
  this.endHour = 20;
  this.timeslotWidth=100;

  this.addToCalendarMenu = function(calendar) {
    this.allCalendars[calendar.id] = calendar;

    var styleString = 'color: ' + calendar.foregroundColor
        + '; background-color: ' + calendar.backgroundColor + ';';

    var menuEntry = $('<div>', {
      'style' : styleString,
      'class' : 'calendar_menu_entry_container hidden',
    });

    var entryCheckbox = $('<input>', {
      'type' : 'checkbox',
      'id' : calendar.id,
      'class' : 'calendar_menu_entry',
    }).change(this.updateCalendarData).appendTo(menuEntry);

    /* check the user's calendar by default */
    if (calendar.id == $('#user_id').text()) {
      entryCheckbox.prop('checked', true);
    }

    $('<label>', {
      'for' : calendar.id,
      html : calendar.summary
    }).appendTo(menuEntry);

    menuEntry.appendTo(calendarMenu).show('slow');
  };

  this.displayEvent = function(calendarId, event) {
    // TODO
    var calendarRowId = 'calendar_' + calendarId;
    var timeContainer;

    if (self.isAllDayEvent(event)) {
      timeContainer = $('#' + escapeSelector(calendarRowId) + '_top td.'
          + event.start.date + '_all_day');
    } else {
      var ;
      var eventDate = event.start.dateTime.substring(0, 10);
      var eventHour = event.start.dateTime.substring(11, 13);
      timeContainer = $('#' + escapeSelector(calendarRowId) + '_bottom td.'
          + eventDate + '_' + eventHour);
      timeContainer.find('span').remove();
    }

    if (timeContainer.length) {

      var eventDiv = $('<div>', {
        'class' : 'event_entry',
        text : event.summary,
      });

      timeContainer.find('span').remove();
      timeContainer.append(eventDiv).show();

      if (!self.isAllDayEvent(event)) {
        var eventDuration='';
        eventDiv.css('width', '300px');
        // eventDiv.css('position', 'absolute');
      }
    }

  };

  this.getSelectedTimeframe = function() {

    var displayPeriodButton = $('input[name=display_timeframe]:radio:checked');
    var currentPeriod = displayPeriodButton.length ? displayPeriodButton.val()
        : 'day';
    return currentPeriod;

  };

  this.initialize = function() {

    this.createDisplayHeader();
    this.createDisplayTable();
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

  this.isAllDayEvent = function(event) {
    return null != event.start.date;
  };

  this.setSelectedDate = function(newDate) {

    if (null != this.selectedDate
        && dateStringShort(this.selectedDate) == dateStringShort(newDate)) { return; }

    this.selectedDate = newDate;
    this.updateTimeframe();
    this.updateDisplayTitle();

  };

  this.createDisplayTable = function() {

    var tableHeaderRow = $('#display_table thead tr');

    for ( var hour = self.startHour; hour <= self.endHour; hour++) {
      $('<td>', {
        'class' : 'timeslot_header',
        text : hour + ':00',
      }).appendTo(tableHeaderRow);
    }

  };

  this.createDisplayHeader = function() {

    $('.button_calendar_navigation').button();

    /* today */
    $('#calendar_navigation_today').attr('title', dateStringShort(new Date()))
        .click(function() {
          self.moveSelectedDate('today');
        }).tooltip();

    /* previous */
    $('#calendar_navigation_prev').button('option', 'icons', {
      primary : 'ui-icon-circle-triangle-w'
    }).button("option", "text", false).click(function() {
      self.moveSelectedDate('prev');
    }).tooltip();

    /* selected date */
    $('#selected_date_display').datepicker({
      dateFormat : 'yy-mm-dd',
      showOtherMonths : true,
      selectOtherMonths : true,
      showAnim : 'fold',
    }).change(function() {
      self.setSelectedDate($('#selected_date_display').datepicker('getDate'));
      self.updateCalendarData();
    });

    /* next */
    $('#calendar_navigation_next').button('option', 'icons', {
      primary : 'ui-icon-circle-triangle-e'
    }).button("option", "text", false).click(function() {
      self.moveSelectedDate('next');
    }).tooltip();

    /* time frame button set */
    $('#calendar_timeframe').buttonset();

    /* disable week, month */
    $('#display_period_week,#display_period_month').button('disable');

    this.setSelectedDate(new Date());
  };

  this.updateDisplayTitle = function() {
    var display = dateStringShort(this.selectedDate);
    $('#selected_date_display').val(display);
  };

  this.moveSelectedDate = function(moveType) {
    var direction = 0;
    switch (moveType) {
      case 'today':
        if (dateStringShort(this.selectedDate) == dateStringShort(new Date())) { return; }
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
    // return;

    /* remove non-selected calendars */
    $.each($(".calendar_menu_entry:not(:checked)"), function(i, entry) {
      var calendar = self.allCalendars[entry.id];

      $('[id^="' + 'calendar_' + calendar.id + '_"]').hide('slow').promise()
          .done(function() {
            this.remove();
          });

    });

    /* add selected calendars */
    $.each($('.calendar_menu_entry:checked'), function(i, entry) {
      var calendar = self.allCalendars[entry.id];

      /* redraw calendar row, this should be optimized at some point */
      $('[id^="' + 'calendar_' + calendar.id + '_"]').empty().remove();

      var styleString = 'color: ' + calendar.foregroundColor
          + '; background-color: ' + calendar.backgroundColor + ';';

      var topRow = $('<tr>', {
        id : 'calendar_' + calendar.id + '_top',
        'class' : 'display_row display_row_top',
      }).appendTo($('#display_table tbody'));
      $('<td>', {
        'class' : 'display_row_label',
        style : styleString,
        rowspan : '2',
      }).appendTo(topRow).append($('<span>', {
        html : calendar.summary,
      }));

      /* create new event container(s) */
      switch (self.getSelectedTimeframe()) {
        case 'day':
          var dateString = dateStringShort(self.selectedDate);
          $('<td>', {
            'class' : dateString + '_all_day',
            colspan : self.endHour - self.startHour + 1,
            html : '<span>&nbsp;</span>',
          }).appendTo(topRow);

          var bottomRow = $('<tr>', {
            id : 'calendar_' + calendar.id + '_bottom',
            'class' : 'display_row display_row_bottom',
          }).appendTo($('#display_table tbody'));
          for ( var hour = self.startHour; hour <= self.endHour; hour++) {
            $('<td>', {
              'class' : dateString + '_' + hour + ' timeslot',
              html : '<span>&nbsp;</span>',
            }).appendTo(bottomRow);
          }
          break;
        case 'week':
          // TODO
          break;
        case 'month':
          // TODO
          break;
      }

      /* get calendar data and display */
      $.getJSON('rest/event', {
        action : 'get',
        id : calendar.id,
        timeMin : self.timeframe.start.toISOString(),
        timeMax : self.timeframe.end.toISOString(),
      }, function(data) {
        if (data.items) {
          $.each(data.items, function(i, event) {
            self.displayEvent(calendar.id, event);
          });
        }
      });
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

function escapeSelector(str) {
  if (str) return str.replace(/([ #;&,.+*~\':"!^$[\]()=>|\/@])/g, '\\$1');
  else return str;
};
