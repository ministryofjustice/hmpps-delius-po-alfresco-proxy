package uk.gov.gsi.justice.alfresco.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;

public class TestFileReader {
    public String readFile(final String fileName) throws IOException {
        final InputStream inputStream = Optional.ofNullable(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(fileName))
                .orElseThrow(IOException::new);

        return new Scanner(inputStream, "UTF-8")
                .useDelimiter("\\A")
                .next();
    }
}
