package uk.gov.gsi.justice.alfresco.proxy.service;

import java.util.List;
import java.util.Optional;
import javax.ws.rs.core.UriInfo;
import org.apache.cxf.jaxrs.model.ClassResourceInfo;
import org.apache.cxf.jaxrs.model.wadl.WadlGenerator;
import org.apache.cxf.message.Message;

public class ProxyUDWadlGenerator extends WadlGenerator {
  public static final String PUBLISHED_WADL_ADDRESS_PROPERTY_PLACEHOLDER =
      "${spg.alfresco.proxy.published.wadl.address}";

  private final String publishedWadlAddress;

  public ProxyUDWadlGenerator(String publishedWadlAddress) {
    this.publishedWadlAddress = publishedWadlAddress;
  }

  @Override
  public StringBuilder generateWADL(
      String baseURI, List<ClassResourceInfo> cris, boolean isJson, Message m, UriInfo ui) {
    return Optional.ofNullable(publishedWadlAddress)
        .map(x -> x.equals(PUBLISHED_WADL_ADDRESS_PROPERTY_PLACEHOLDER))
        .map(y -> super.generateWADL(baseURI, cris, isJson, m, ui))
        .orElse(super.generateWADL(publishedWadlAddress, cris, isJson, m, ui));
  }
}
