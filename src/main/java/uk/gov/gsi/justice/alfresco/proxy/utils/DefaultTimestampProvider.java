package uk.gov.gsi.justice.alfresco.proxy.utils;

import javax.inject.Named;
import java.time.Clock;
import java.time.Instant;

import static java.time.Instant.now;

@Named
public class DefaultTimestampProvider implements TimestampProvider {
    @Override
    public Instant getTimestamp() {
        return now(Clock.systemUTC());
    }
}
