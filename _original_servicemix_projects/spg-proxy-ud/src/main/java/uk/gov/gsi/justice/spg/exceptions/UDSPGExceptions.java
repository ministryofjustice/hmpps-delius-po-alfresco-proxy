package uk.gov.gsi.justice.spg.exceptions;

/**
 * A class containing exceptions for use in spg-proxy-ud.
 * Very similar to SPGExceptions in spg-common, if it is decided that spg-common will be deployed alongside spg-proxy-ud,
 * these should be refactored to be in SPGExceptions instead of it in it's own separate class.
 */
public class UDSPGExceptions {

    public static class SenderDisabledException extends Exception {

        public SenderDisabledException(String message) {
            super(message);
        }
    }
}
