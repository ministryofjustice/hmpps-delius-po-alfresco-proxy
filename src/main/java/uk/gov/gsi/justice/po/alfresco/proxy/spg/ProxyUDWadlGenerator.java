package uk.gov.gsi.justice.po.alfresco.proxy.spg;

import org.apache.cxf.jaxrs.model.ClassResourceInfo;
import org.apache.cxf.jaxrs.model.wadl.WadlGenerator;
import org.apache.cxf.message.Message;

import javax.ws.rs.core.UriInfo;
import java.util.List;

public class ProxyUDWadlGenerator extends WadlGenerator {

    public static final String PUBLISHED_WADL_ADDRESS_PROPERTY_NAME = "spg.unstructured.proxy.published.wadl.address";
    public static final String PUBLISHED_WADL_ADDRESS_PROPERTY_PLACEHOLDER = "${" + PUBLISHED_WADL_ADDRESS_PROPERTY_NAME + "}";

    private final String publishedWadlAddress;

    public ProxyUDWadlGenerator(String publishedWadlAddress) {
        this.publishedWadlAddress = publishedWadlAddress;
    }

    @Override
    public StringBuilder generateWADL(String baseURI,
                                      List<ClassResourceInfo> cris,
                                      boolean isJson,
                                      Message m,
                                      UriInfo ui) {
        if (publishedWadlAddress != null && !publishedWadlAddress.isEmpty() && !publishedWadlAddress.equals(PUBLISHED_WADL_ADDRESS_PROPERTY_PLACEHOLDER))
            return super.generateWADL(publishedWadlAddress, cris, isJson, m, ui);
        else
            return super.generateWADL(baseURI, cris, isJson, m, ui);
    }
}
