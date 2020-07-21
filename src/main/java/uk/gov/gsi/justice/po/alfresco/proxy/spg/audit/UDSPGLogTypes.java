package uk.gov.gsi.justice.po.alfresco.proxy.spg.audit;

/**
 * An enum containing audit log types used in spg-proxy-ud.
 * Very similar to SPGLogTypes in spg-common, if it is decided that spg-common will be deployed alongside spg-proxy-ud,
 * these should be refactored to be in SPGLogTypes instead of it in it's own separate enum.
 */
public enum UDSPGLogTypes {
    LOG_PROXY_UD_ABSORBED_MESSAGE("logProxyUDAbsorbedMessage");

    private final String text;

    UDSPGLogTypes(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
