<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Pinecone</title>
<link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.9.2/themes/pepper-grinder/jquery-ui.css" type="text/css" rel="Stylesheet" />
<link href="css/calendar-display.css" rel="stylesheet">
<link href="css/main.css" rel="stylesheet">
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.9.2/jquery-ui.min.js"></script>
<script type="text/javascript" src="js/date.js"></script>
<script type="text/javascript" src="js/calendar-display.js"></script>
<script type="text/javascript">
	var mydisplay;
	$(function() {
		$('#button_logout').button();

		mydisplay = new CalendarDisplay($('#main_display'), $('#sidebar'));
		mydisplay.initialize();
	});
</script>
</head>
<body class="ui-widget ui-widget-content">

  <div id="main_content">
    <div id="header">
      <div id="user_data">
        <span id="user_id">${ sessionScope.userId }</span>
        <a id="button_logout" href="logout">logout</a>
      </div>
    </div>

    <div id="sidebar"></div>
    <div id="main_display">
      <div id="display_header">
        <div id="calendar_navigation">
          <a id="calendar_navigation_today"
            class="button_calendar_navigation">today</a>
          <a id="calendar_navigation_prev"
            class="button_calendar_navigation" title="previous">previous</a>
          <input type="text" id="selected_date_display"
            class="button_calendar_navigation" readonly="readonly" />
          <a id="calendar_navigation_next"
            class="button_calendar_navigation" title="next">next</a>
        </div>
        <div id="calendar_timeframe">
          <input type="radio" id="display_period_day"
            name="display_timeframe" checked="checked" value="day" />
          <label for="display_period_day">day</label>
          <input type="radio" id="display_period_week"
            name="display_timeframe" value="week" />
          <label for="display_period_week">week</label>
          <input type="radio" id="display_period_month"
            name="display_timeframe" value="month" />
          <label for="display_period_month">month</label>
        </div>
      </div>
      <div id="display_body">
        <table id="display_table">
          <thead>
            <tr>
              <td>calendar</td>
            </tr>
          </thead>
          <tbody></tbody>
        </table>
      </div>
    </div>

  </div>


</body>
</html>