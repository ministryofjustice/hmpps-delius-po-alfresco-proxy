package uk.gov.gsi.justice.po.alfresco.proxy.spg.spg_core_rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("deprecation")
public class SPGUnstructuredServiceTest {
    private static final Log log = LogFactory.getLog(SPGUnstructuredServiceTest.class);

   // @Test
    public void testApp() {
        String url = "http://localhost:8282/cxf/spg-proxy-ud";
        HttpResponse response;

        HttpGet httpGet = new HttpGet(url);

        @SuppressWarnings("resource")
        HttpClient httpclient = new DefaultHttpClient();

        try {
            response = httpclient.execute(httpGet);
            assertEquals(200, response.getStatusLine().getStatusCode());

        } catch (IOException e) {
            log.error(e);
        }
    }
    //@Test
    public void testAppfail() {
        String url = "http://192.168.99.1:8289/cxf/spg-proxy-ud";
        HttpResponse response;

        HttpGet httpGet = new HttpGet(url);

        @SuppressWarnings("resource")
        HttpClient httpclient = new DefaultHttpClient();

        try {
            response = httpclient.execute(httpGet);
            assertEquals(404, response.getStatusLine().getStatusCode());

        } catch (IOException e) {
            log.error(e);
        }
    }
}
