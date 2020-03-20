package uk.gov.gsi.justice.po.alfresco.proxy.http;

import io.vavr.control.Either;
import uk.gov.gsi.justice.po.alfresco.proxy.model.HttpFault;
import uk.gov.gsi.justice.po.alfresco.proxy.model.HttpSuccess;

public interface RestHttpClient {
    Either<HttpFault, HttpSuccess> getResource(String endpoint);
}
