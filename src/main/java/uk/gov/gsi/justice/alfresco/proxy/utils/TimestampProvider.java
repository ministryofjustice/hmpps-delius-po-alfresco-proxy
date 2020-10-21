package uk.gov.gsi.justice.alfresco.proxy.utils;

import java.time.Instant;

public interface TimestampProvider {
  Instant getTimestamp();
}
