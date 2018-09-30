package org.state.patch.sql.util;

import com.fasterxml.jackson.databind.util.StdDateFormat;

public interface Json {
    public static final String        DATE_TIMEZONE = "UTC";
    public static final String        DATE_PATTERN  = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final StdDateFormat DATE_FORMAT   = new StdDateFormat();
}
