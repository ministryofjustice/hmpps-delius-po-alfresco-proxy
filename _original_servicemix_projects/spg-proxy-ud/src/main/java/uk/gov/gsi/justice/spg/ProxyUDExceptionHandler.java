package uk.gov.gsi.justice.spg;

import org.apache.camel.component.cxf.CxfOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.gsi.justice.spg.av.AntiVirusInterceptor;
import uk.gov.gsi.justice.spg.exceptions.AntivirusException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.net.ConnectException;

public class ProxyUDExceptionHandler implements ExceptionMapper<Throwable> {

    private Logger log = LoggerFactory.getLogger(ProxyUDExceptionHandler.class);

    @Override
    public Response toResponse(Throwable exception) {

        int httpCode = 500;

        String exceptionMessage;

        //If ALF service is unavailable SPG returns generic InternalServerErrorException back to the client with wrapped ConnectException.
        if (exception.getCause() instanceof ConnectException) {
            ConnectException connectException = (ConnectException) exception.getCause();
            exceptionMessage = trimExceptionMessage(connectException.getMessage());

            httpCode = 503;
        }
       else if (exception instanceof CxfOperationException) { //SPG throws HTTP 404 Not found exception wrapped in CxfOperationException
            CxfOperationException cxfOperationException = (CxfOperationException) exception;
            httpCode = cxfOperationException.getStatusCode();
            exceptionMessage = trimExceptionMessage(cxfOperationException.getResponseBody());
        }
        else if(exception instanceof AntivirusException)
        {
            AntivirusException antiVirusException = (AntivirusException) exception;
            httpCode = AntiVirusInterceptor.VIRUS_FOUND_HTTP_CODE;
            exceptionMessage = trimExceptionMessage(antiVirusException.getMessage());
        }
        else {
            exceptionMessage = trimExceptionMessage(exception.getMessage());
        }

        log.info("Exception", exception);

        return Response.status(httpCode).entity(exceptionMessage).type(MediaType.APPLICATION_JSON).build();
    }

    private String trimExceptionMessage(String exceptionMessage) {
        return exceptionMessage.length() > 499 ? exceptionMessage.substring(0, 499) : exceptionMessage;
    }

}
