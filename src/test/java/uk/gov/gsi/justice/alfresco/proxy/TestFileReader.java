package uk.gov.gsi.justice.alfresco.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;

public class TestFileReader {
    public InputStream getFileAsStream(final String fileName) throws IOException {
        return Optional.ofNullable(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(fileName))
                .orElseThrow(IOException::new);
    }

    public String readFile(final String fileName) throws IOException {
        final InputStream inputStream = getFileAsStream(fileName);

        return new Scanner(inputStream, "UTF-8")
                .useDelimiter("\\A")
                .next();
    }
}
