package com.betfair.foe.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ISO8601DateTypeAdapter{
    public static final String ISO_8601_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String ISO_8601_TIMEZONE = "UTC";

    private DateFormat dateFormat;

    public ISO8601DateTypeAdapter() {
        dateFormat = new SimpleDateFormat(ISO_8601_FORMAT_STRING);
        dateFormat.setTimeZone(TimeZone.getTimeZone(ISO_8601_TIMEZONE));
    }

    public Date getDateFromString(String stringToBeParsed) throws ParseException {
    	return dateFormat.parse(stringToBeParsed);
    }
    
}
