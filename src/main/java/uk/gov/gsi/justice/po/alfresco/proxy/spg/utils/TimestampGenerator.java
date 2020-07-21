package uk.gov.gsi.justice.po.alfresco.proxy.spg.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimestampGenerator {

    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String TIMESTAMP_LOCAL_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String FILE_NAME_TIMESTAMP_FORMAT = "yyyy-MM-dd_HH-mm-ss.SSS"; // timestamp format when used in file names

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT); // DateTimeFormatter is threadsafe
    private final DateTimeFormatter dateLocalFormatter = DateTimeFormatter.ofPattern(TIMESTAMP_LOCAL_FORMAT); 
    private final DateTimeFormatter fileNameDateFormatter = DateTimeFormatter.ofPattern(FILE_NAME_TIMESTAMP_FORMAT);
    private final static Log log = LogFactory.getLog(TimestampGenerator.class);

    public TimestampGenerator() {

    }

    /**
     * Method to return now time with the timezone offset
     * @return
     */
    public String getCurrentTimeStamp() {
    	ZonedDateTime zdt = ZonedDateTime.now();
        return zdt.format(dateFormatter);
    }


}
