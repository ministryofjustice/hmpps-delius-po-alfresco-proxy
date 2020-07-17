

package uk.gov.gsi.justice.spg.audit;
import org.apache.log4j.Level;
 
/**
 * @author AUDIT.com
 * 
 */
 
@SuppressWarnings("serial")
public class UDAuditLog4jLevel extends Level {
 
    /**
     * Value of AUDITLog4jLevel level. Set to WARN as custom value was getting lost through the commons facade
     *
     */
    public static final int AUDIT_INT = WARN_INT;
 
    /**
     * Level representing my log level
     */
    public static final Level AUDIT = new UDAuditLog4jLevel(AUDIT_INT, "AUDIT", 4);
 
    /**
     * Constructor
     */
    protected UDAuditLog4jLevel(int arg0, String arg1, int arg2) {
        super(arg0, arg1, arg2);
 
    }
 
    /**
     * Checks whether logArgument is "AUDIT" level. If yes then returns
     * AUDIT}, else calls AUDITLog4jLevel#toLevel(String, Level) passing
     * it Level#DEBUG as the defaultLevel.
     */
    public static Level toLevel(String logArgument) {
        if (logArgument != null && "AUDIT".equalsIgnoreCase(logArgument)) {
            return AUDIT;
        }
        return toLevel(logArgument);
    }
 
    /**
     * Checks whether val is AUDITLog4jLevel#AUDIT_INT. If yes then
     * returns AUDITLog4jLevel#AUDIT, else calls
     * AUDITLog4jLevel#toLevel(int, Level) passing it Level#DEBUG as the
     * defaultLevel
     * 
     */
    public static Level toLevel(int val) {
        if (val == AUDIT_INT) {
            return AUDIT;
        }
        return toLevel(val, Level.DEBUG);
    }
 
    /**
     * Checks whether val is AUDITLog4jLevel#AUDIT_INT. If yes
     * then returns AUDITLog4jLevel#AUDIT, else calls Level#toLevel(int, org.apache.log4j.Level)
     * 
     */
    public static Level toLevel(int val, Level defaultLevel) {
        if (val == AUDIT_INT) {
            return AUDIT;
        }
        return Level.toLevel(val, defaultLevel);
    }
 
    /**
     * Checks whether logArgument is "AUDIT" level. If yes then returns
     * AUDITLog4jLevel#AUDIT, else calls
     * Level#toLevel(java.lang.String, org.apache.log4j.Level)
     * 
     */
    public static Level toLevel(String logArgument, Level defaultLevel) {
        if (logArgument != null && "AUDIT".equalsIgnoreCase(logArgument)) {
            return AUDIT;
        }
        return Level.toLevel(logArgument, defaultLevel);
    }
}