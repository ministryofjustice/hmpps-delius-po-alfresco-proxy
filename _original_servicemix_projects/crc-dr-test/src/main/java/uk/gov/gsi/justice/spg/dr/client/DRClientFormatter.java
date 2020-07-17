package uk.gov.gsi.justice.spg.dr.client;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class DRClientFormatter extends Formatter{
    @Override
    public String format(LogRecord record) {
        return record.getMessage() + "\r\n";
    }
}
