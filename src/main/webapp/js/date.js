var dateStringShort = function(date) {
	var y = date.getFullYear();
	var m = padZero(date.getMonth() + 1);
	var d = padZero(date.getDate());

	return y + '-' + m + '-' + d;
};

var timeString = function(date) {
	var h = padZero(date.getHours());
	var m = padZero(date.getMinutes());

	return h + ':' + m;
};

var dateTimeStringShort = function(date) {
	return $.dateStringShort(date) + ' ' + $.timeString(date);
};

var padZero = function(value) {
	if (value < 10) {
		return '0' + value;
	}
	return value;
};
