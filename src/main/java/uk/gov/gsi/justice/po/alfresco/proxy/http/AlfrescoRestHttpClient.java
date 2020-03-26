package uk.gov.gsi.justice.po.alfresco.proxy.http;

import io.vavr.control.Either;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import retrofit2.Call;
import retrofit2.Response;
import uk.gov.gsi.justice.po.alfresco.proxy.function.ThrowingFunction;
import uk.gov.gsi.justice.po.alfresco.proxy.model.HttpFault;
import uk.gov.gsi.justice.po.alfresco.proxy.model.HttpSuccess;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.Optional;

@Named
public class AlfrescoRestHttpClient implements RestHttpClient {
    private final RestClient alfrescoClient;
    private final String baseUrl;

    @Inject
    public AlfrescoRestHttpClient(final RestClient alfrescoClient,
                                  @Value("${alfresco.base.url}") final String baseUrl) {
        this.alfrescoClient = alfrescoClient;
        this.baseUrl = baseUrl;
    }

    @Override
    public Either<HttpFault, HttpSuccess> getResource(final String endpoint) {
        final Call<ResponseBody> call = alfrescoClient.getResource(baseUrl + endpoint);

        try {
            final Response<ResponseBody> response = call.execute();
            return response.isSuccessful() ?
                    handleSuccess(response.code(), response.message(), response.body()) :
                    handleFailure(response.code(), response.message());
        } catch (IOException e) {
            return handleFailure(0, e.getMessage());
        }
    }

    private Either<HttpFault, HttpSuccess> handleSuccess(final int code,
                                                         final String message,
                                                         final ResponseBody body) {
        return Optional.ofNullable(body)
                .map(ThrowingFunction.unchecked(ResponseBody::string))
                .map(x -> Either.<HttpFault, HttpSuccess>right(new HttpSuccess(code, message, x)))
                .orElse(handleFailure(code, message));
    }

    private Either<HttpFault, HttpSuccess> handleFailure(final int code, final String message) {
        return Either.left(new HttpFault(code, message));
    }
}
