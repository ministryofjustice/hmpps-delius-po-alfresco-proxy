package uk.gov.gsi.justice.alfresco.proxy.utils;

import static java.time.Instant.now;

import java.time.Clock;
import java.time.Instant;

public class DefaultTimestampProvider implements TimestampProvider {
  @Override
  public Instant getTimestamp() {
    return now(Clock.systemUTC());
  }
}
