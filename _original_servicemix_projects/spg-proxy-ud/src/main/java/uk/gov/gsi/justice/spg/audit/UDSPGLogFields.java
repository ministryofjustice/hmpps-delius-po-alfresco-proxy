package uk.gov.gsi.justice.spg.audit;

/**
 * An enum containing audit log fields used in spg-proxy-ud.
 * Very similar to SPGLogFields in spg-common, if it is decided that spg-common will be deployed alongside spg-proxy-ud,
 * these should be refactored to be in SPGLogFields instead of it in it's own separate enum.
 */
public enum UDSPGLogFields {

    // SPG PROXY UD audit fields, no camel expression required for these log fields, as their value is set manually
    UD_ID("id"),
    UD_FROM("from"),
    UD_TO("to"),
    UD_CRN("caseReferenceNumber"),
    UD_STATUS("status"),
    UD_MESSAGE_TYPE("messageType"),
    UD_HEADERS("headers"),
    UD_URI("uri"),
    UD_ERRDESC("errorDescription"),
    UD_ROUNDTRIP_TIME("roundTripTime"),
    UD_PAYLOAD("payload");

    private final String text;

    UDSPGLogFields(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
