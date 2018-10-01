package org.state.patch.sql.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.util.StdDateFormat;

public interface Json {
    public static final String        DATE_TIMEZONE = "UTC";
    public static final String        DATE_PATTERN  = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final StdDateFormat DATE_PARSE    = new StdDateFormat();

    @SuppressWarnings("serial")
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN) {
        {
            this.setTimeZone(TimeZone.getTimeZone(DATE_TIMEZONE));
        }
    };

    public static Date parseDate(String json) {
        try {
            return DATE_PARSE.parse(json);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String formatDate(Date java) {
        return DATE_FORMAT.format(java);
    }
}
