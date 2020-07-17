package uk.gov.gsi.justice.spg.av;

import org.apache.cxf.attachment.AttachmentImpl;
import org.apache.cxf.attachment.ByteDataSource;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Attachment;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.gsi.justice.spg.audit.UDInterchangeAuditLogService;

import javax.activation.DataHandler;
import java.io.ByteArrayInputStream;
import java.util.Collections;

import static junit.framework.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class AntivirusInterceptorTest {

    private AntiVirusInterceptor interceptor;
    private AntivirusScanner mockAvScanner;
    private Message message;

    @Before
    public void setUp() throws Exception {
        mockAvScanner = Mockito.mock(AntivirusScanner.class);
        interceptor = new AntiVirusInterceptor();
        interceptor.setAntivirusScanner(mockAvScanner);
        interceptor.setAuditLogService(new UDInterchangeAuditLogService());
        interceptor.setScanForViruses(true);

        Attachment attachment = new AttachmentImpl("", new DataHandler(new ByteDataSource(new byte[0])));
        message = new MessageImpl();
        message.setAttachments(Collections.singletonList(attachment));

    }

    @Test
    public void testInterceptorWithValidMessage() throws Exception {
        //given
        when(mockAvScanner.scanBytes(any(ByteArrayInputStream.class))).thenReturn(new AntivirusResponse("stream: OK"));

        //when
        interceptor.handleMessage(message);

        //then the handle message should exit quietly
    }

    @Test(expected = Fault.class)
    public void testInterceptorWithAVirusMessage() throws Fault {
        //given
        when(mockAvScanner.scanBytes(any(ByteArrayInputStream.class))).thenReturn(new AntivirusResponse("stream: VIRUS FOUND"));

        //when
        interceptor.handleMessage(message);

        //then
        fail("It should throw a Fault exception");
    }
}