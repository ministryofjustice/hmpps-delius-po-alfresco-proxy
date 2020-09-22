package uk.gov.gsi.justice.alfresco.proxy.utils;

import java.time.Clock;
import java.time.Instant;

import static java.time.Instant.now;

public class DefaultTimestampProvider implements TimestampProvider {
    @Override
    public Instant getTimestamp() {
        return now(Clock.systemUTC());
    }
}
