package uk.gov.gsi.justice.po.alfresco.proxy.spg;

import org.apache.cxf.interceptor.AbstractOutDatabindingInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;

public class ProxyUDDebugOutInterceptor extends AbstractOutDatabindingInterceptor {

    public ProxyUDDebugOutInterceptor() {
        this(Phase.SEND);
    }

    public ProxyUDDebugOutInterceptor(String phase) {
        super(phase);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        String currentRequestUri = (String) message.get("org.apache.cxf.request.uri");
    }
}
