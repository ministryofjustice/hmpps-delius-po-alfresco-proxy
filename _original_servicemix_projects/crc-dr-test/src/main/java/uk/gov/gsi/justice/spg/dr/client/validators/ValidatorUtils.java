package uk.gov.gsi.justice.spg.dr.client.validators;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class ValidatorUtils {


    public final static List<String> validDocTypeId = Arrays.asList("DOCUMENT", "PREVIOUS_CONVICTION", "CPS_PACK");
    public final static List<String> validSortParams = Arrays.asList("docType", "entityType", "entityId", "fileName", "fileSize", "lastModified", "fileExtension");

    public static boolean isLong(String s) {
        try {
            Long.parseLong(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }

    public static boolean isBoolean(String s) {
        return "true".equals(s) || "false".equals(s);
    }

    public static boolean isValidCRN(String crn) {
        Pattern p = Pattern.compile("[A-Z0-9]{7}");
        return p.matcher(crn).matches();
    }
}
