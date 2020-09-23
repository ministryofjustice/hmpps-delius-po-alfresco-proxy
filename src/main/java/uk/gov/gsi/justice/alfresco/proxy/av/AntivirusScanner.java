package uk.gov.gsi.justice.alfresco.proxy.av;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.gsi.justice.alfresco.proxy.exceptions.AntivirusException;
import uk.gov.gsi.justice.alfresco.proxy.utils.ClamAvConnectionParametersProvider;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @deprecated
 * Use AntivirusClient instead
 */
@Deprecated
public class AntivirusScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(AntivirusScanner.class);
    private static final int CHUNK_SIZE = 2048;
    private static final byte[] INSTREAM = "zINSTREAM\0".getBytes();
    private static final int RETRY_TIMES = 3;

    private final ClamAvConnectionParametersProvider clamAvConnectionParametersProvider;

    public AntivirusScanner(final ClamAvConnectionParametersProvider clamAvConnectionParametersProvider) {
        this.clamAvConnectionParametersProvider = clamAvConnectionParametersProvider;
    }

    public AntivirusResponse scanBytes(InputStream is) {
        final String host = clamAvConnectionParametersProvider.host();
        final int port = clamAvConnectionParametersProvider.port();
        final int socketTimeout = clamAvConnectionParametersProvider.timeout();

        LOGGER.info("============================== Connecting to ClamAV with the following parameters ==============================");
        LOGGER.info("Host: {}", host);
        LOGGER.info("Port: {}", port);
        LOGGER.info("Timeout: {}", socketTimeout);
        LOGGER.info("================================================================================================================");

        Socket socket = new Socket();

        try {
            socket.connect(
                    new InetSocketAddress(
                            host,
                            port
                    )
            );
            socket.setSoTimeout(socketTimeout);
        } catch (IOException e) {
            LOGGER.error("Could not connect or set socket timeout to " + socketTimeout + "ms", e);
            return new AntivirusResponse(new AntivirusException("Could not connect to AV socket", e));
        }
        DataOutputStream dos = null;
        String response = "";
        try {
            try {
                dos = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                LOGGER.error("could not open socket OutputStream", e);
                return new AntivirusResponse(new AntivirusException("Could not open socket OutputStream", e));
            }

            try {
                dos.write(INSTREAM);
            } catch (IOException e) {
                LOGGER.debug("Error writing INSTREAM command", e);
                return new AntivirusResponse(new AntivirusException("Error writing INSTREAM command", e));
            }

            int read = CHUNK_SIZE;
            byte[] buffer = new byte[CHUNK_SIZE];
            while (read == CHUNK_SIZE) {
                try {

                    read = is.read(buffer);
                } catch (IOException e) {
                    LOGGER.debug("Error reading from InputStream", e);
                    return new AntivirusResponse(new AntivirusException("Error reading from InputStream", e));
                }

                if (read > 0) {
                    for (int i = 1; i <= RETRY_TIMES; i++) {
                        try {
                            dos.writeInt(read);
                            dos.write(buffer, 0, read);
                            break;
                        } catch (IOException e) {
                            LOGGER.debug("Error writing data to socket");
                            LOGGER.debug("Retry times writing data to socket: " + i);
                            if (i == RETRY_TIMES) {
                                LOGGER.debug("Reached maximum retry limit of " + RETRY_TIMES + " times.");
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
                LOGGER.debug("Error writing zero-length chunk to socket", e);
            }

            try {
                read = socket.getInputStream().read(buffer);
            } catch (IOException e) {
                LOGGER.debug("Error reading result from socket", e);
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
                LOGGER.debug("Exception closing socket", e);
            }
        }
        return new AntivirusResponse(response);
    }
}