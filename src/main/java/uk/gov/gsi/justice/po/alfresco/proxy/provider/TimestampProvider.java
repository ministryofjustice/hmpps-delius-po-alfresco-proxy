package uk.gov.gsi.justice.po.alfresco.proxy.provider;

import java.time.Instant;

public interface TimestampProvider {
    Instant getTimestamp();
}
