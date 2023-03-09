package com.nike.ncp.common.utilities.util;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class IsoDateUtil {

    private static final String GMT = "GMT";
    private static final String ISO_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private IsoDateUtil() {
    }

    public static String fromDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone(GMT));
        return date == null ? null : sdf.format(date);
    }

    public static Date toDate(String isoDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_DATE_PATTERN, Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            return StringUtils.isEmpty(isoDate) ? null : sdf.parse(isoDate);
        } catch (ParseException e) {
            throw new RuntimeException("Unable to parse date " + isoDate, e);
        }
    }

    public static LocalDateTime toUtc(final LocalDateTime time) {
        final ZonedDateTime zonedTime = time.atZone(ZoneId.systemDefault());
        final ZonedDateTime converted = zonedTime.withZoneSameInstant(ZoneOffset.UTC);
        return converted.toLocalDateTime();
    }

}
