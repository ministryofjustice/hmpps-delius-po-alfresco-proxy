package uk.gov.gsi.justice.po.alfresco.proxy.spg.av;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.gov.gsi.justice.po.alfresco.proxy.spg.exceptions.AntivirusException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class AntivirusScanner {
    private static Log log = LogFactory.getLog(AntivirusScanner.class);
    private static final int CHUNK_SIZE = 2048;
    private static final byte[] INSTREAM = "zINSTREAM\0".getBytes();
    private static final int RETRY_TIMES = 3;

    private String host;
    private int port;
    private int timeout;

    public AntivirusScanner(String host, int port, int timeout) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }

    public AntivirusResponse scanBytes(InputStream is) {
        Socket socket = new Socket();

        try {
            socket.connect(new InetSocketAddress(host, port));
            socket.setSoTimeout(timeout);
        } catch (IOException e) {
            log.error("Could not connect or set socket timeout to " + timeout + "ms", e);
            return new AntivirusResponse(new AntivirusException("Could not connect to AV socket", e));
        }
        DataOutputStream dos = null;
        String response = "";
        try {
            try {
                dos = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                log.error("could not open socket OutputStream", e);
                return new AntivirusResponse(new AntivirusException("Could not open socket OutputStream", e));
            }

            try {
                dos.write(INSTREAM);
            } catch (IOException e) {
                log.debug("Error writing INSTREAM command", e);
                return new AntivirusResponse(new AntivirusException("Error writing INSTREAM command", e));
            }

            int read = CHUNK_SIZE;
            byte[] buffer = new byte[CHUNK_SIZE];
            while (read == CHUNK_SIZE) {
                try {

                    read = is.read(buffer);
                } catch (IOException e) {
                    log.debug("Error reading from InputStream", e);
                    return new AntivirusResponse(new AntivirusException("Error reading from InputStream", e));
                }

                if (read > 0) {
                    for (int i = 1; i <= RETRY_TIMES; i++){
                        try {
                            dos.writeInt(read);
                            dos.write(buffer, 0, read);
                            break;
                        } catch (IOException e) {
                            log.debug("Error writing data to socket");
                            log.debug("Retry times writing data to socket: " + i);
                            if (i == RETRY_TIMES){
                                log.debug("Reached maximum retry limit of " + RETRY_TIMES + " times.");
                                return new AntivirusResponse(new AntivirusException("Error writing data to socket", e));
                            }
                        }
                    }
                }
            }

            try {
                dos.writeInt(0);
                dos.flush();
            } catch (IOException e) {
                log.debug("Error writing zero-length chunk to socket", e);
            }

            try {
                read = socket.getInputStream().read(buffer);
            } catch (IOException e) {
                log.debug("Error reading result from socket", e);
                read = 0;
            }
            if (read > 0) {
                response = new String(buffer, 0, read);
            }

        } finally {
            try {
                if (dos != null) {
                    dos.close();
                }
                socket.close();
            } catch (IOException e) {
                log.debug("Exception closing socket", e);
            }
        }
        return new AntivirusResponse(response);
    }
}
