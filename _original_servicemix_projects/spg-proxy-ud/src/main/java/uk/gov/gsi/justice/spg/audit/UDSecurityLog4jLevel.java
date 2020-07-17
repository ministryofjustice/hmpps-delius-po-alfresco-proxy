

package uk.gov.gsi.justice.spg.audit;
import org.apache.log4j.Level;

/**
 * @author ALERT.com
 * 
 */
 
@SuppressWarnings("serial")
public class UDSecurityLog4jLevel extends Level {

    /**
     * Value of ALERTLog4jLevel level. Set to WARN as custom value was getting lost through the commons facade
     *
     */
    public static final int SECURITY_INT = INFO_INT;

    /**
     * Level representing my log level
     */
    public static final Level SECURITY = new UDSecurityLog4jLevel(SECURITY_INT, "SECURITY", 6);

    /**
     * Constructor
     */
    protected UDSecurityLog4jLevel(int level, String levelStr, int syslogEquivalent) {
        super(level, levelStr, syslogEquivalent);
 
    }
 
    /**
     * Checks whether logArgument is "SECURITY" level. If yes then returns
     * SECURITY}, else calls SECURITYLog4jLevel#toLevel(String, Level) passing
     * it Level#DEBUG as the defaultLevel.
     */
    public static Level toLevel(String logArgument) {
        if (logArgument != null && "SECURITY".equalsIgnoreCase(logArgument)) {
            return SECURITY;
        }
        return toLevel(logArgument);
    }
 
    /**
     * Checks whether val is SECURITYLog4jLevel#SECURITY_INT. If yes then
     * returns SECURITYLog4jLevel#SECURITY, else calls
     * SECURITYLog4jLevel#toLevel(int, Level) passing it Level#DEBUG as the
     * defaultLevel
     * 
     */
    public static Level toLevel(int val) {
        if (val == SECURITY_INT) {
            return SECURITY;
        }
        return toLevel(val, Level.DEBUG);
    }
 
    /**
     * Checks whether val is ALERTLog4jLevel#ALERT_INT. If yes
     * then returns ALERTLog4jLevel#ALERT, else calls Level#toLevel(int, org.apache.log4j.Level)
     * 
     */
    public static Level toLevel(int val, Level defaultLevel) {
        if (val == SECURITY_INT) {
            return SECURITY;
        }
        return Level.toLevel(val, defaultLevel);
    }
 
    /**
     * Checks whether logArgument is "SECURITY" level. If yes then returns
     * SECURITYLog4jLevel#SECURITY, else calls
     * Level#toLevel(java.lang.String, org.apache.log4j.Level)
     * 
     */
    public static Level toLevel(String logArgument, Level defaultLevel) {
        if (logArgument != null && "SECURITY".equalsIgnoreCase(logArgument)) {
            return SECURITY;
        }
        return Level.toLevel(logArgument, defaultLevel);
    }
}