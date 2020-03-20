package uk.gov.gsi.justice.po.alfresco.proxy.http;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RestClient {
    @GET
    Call<ResponseBody> getResource(@Url String url);
}
