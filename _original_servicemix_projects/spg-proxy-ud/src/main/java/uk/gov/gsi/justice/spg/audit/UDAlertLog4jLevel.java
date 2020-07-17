

package uk.gov.gsi.justice.spg.audit;
import org.apache.log4j.Level;
 
/**
 * @author ALERT.com
 * 
 */
 
@SuppressWarnings("serial")
public class UDAlertLog4jLevel extends Level {
 
    /**
     * Value of ALERTLog4jLevel level. Set to WARN as custom value was getting lost through the commons facade
     *
     */
    public static final int ALERT_INT = ERROR_INT;
 
    /**
     * Level representing my log level
     */
    public static final Level ALERT = new UDAlertLog4jLevel(ALERT_INT, "ALERT", 4);
 
    /**
     * Constructor
     */
    protected UDAlertLog4jLevel(int arg0, String arg1, int arg2) {
        super(arg0, arg1, arg2);
 
    }
 
    /**
     * Checks whether logArgument is "ALERT" level. If yes then returns
     * ALERT}, else calls ALERTLog4jLevel#toLevel(String, Level) passing
     * it Level#DEBUG as the defaultLevel.
     */
    public static Level toLevel(String logArgument) {
        if (logArgument != null && "ALERT".equalsIgnoreCase(logArgument)) {
            return ALERT;
        }
        return toLevel(logArgument);
    }
 
    /**
     * Checks whether val is ALERTLog4jLevel#ALERT_INT. If yes then
     * returns ALERTLog4jLevel#ALERT, else calls
     * ALERTLog4jLevel#toLevel(int, Level) passing it Level#DEBUG as the
     * defaultLevel
     * 
     */
    public static Level toLevel(int val) {
        if (val == ALERT_INT) {
            return ALERT;
        }
        return toLevel(val, Level.DEBUG);
    }
 
    /**
     * Checks whether val is ALERTLog4jLevel#ALERT_INT. If yes
     * then returns ALERTLog4jLevel#ALERT, else calls Level#toLevel(int, org.apache.log4j.Level)
     * 
     */
    public static Level toLevel(int val, Level defaultLevel) {
        if (val == ALERT_INT) {
            return ALERT;
        }
        return Level.toLevel(val, defaultLevel);
    }
 
    /**
     * Checks whether logArgument is "ALERT" level. If yes then returns
     * ALERTLog4jLevel#ALERT, else calls
     * Level#toLevel(java.lang.String, org.apache.log4j.Level)
     * 
     */
    public static Level toLevel(String logArgument, Level defaultLevel) {
        if (logArgument != null && "ALERT".equalsIgnoreCase(logArgument)) {
            return ALERT;
        }
        return Level.toLevel(logArgument, defaultLevel);
    }
}