package uk.gov.gsi.justice.po.alfresco.proxy.spg;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import javax.inject.Named;

public class ProxyUDDebugProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Message inMessage = exchange.getIn();
    }
}
