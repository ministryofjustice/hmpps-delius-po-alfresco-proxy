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

            final String body = Optional.ofNullable(response.body())
                    .map(ThrowingFunction.unchecked(ResponseBody::string))
                    .orElse("");

            return Either.right(new HttpSuccess(response.code(), response.message(), body));
        } catch (IOException e) {
            return Either.left(new HttpFault(0, "", e.getMessage()));
        }
    }
}
