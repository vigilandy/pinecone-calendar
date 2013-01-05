$.dateStringShort = function(date) {
	return date.getFullYear() + '-' + (date.getMonth() + 1) + '-'
			+ date.getDate();
};

$.timeString = function(date) {
	var h = date.getHours();
	var m = date.getMinutes();
	if (h < 10) {
		h = '0' + h;
	}
	if (m < 10) {
		m = '0' + m;
	}
	return h + ':' + m;
};

$.dateTimeStringShort = function(date) {
	return $.dateStringShort(date) + ' ' + $.timeString(date);
};
