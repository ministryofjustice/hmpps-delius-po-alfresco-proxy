package uk.gov.gsi.justice.alfresco.proxy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;

public class JsonReader {
    private Gson gson = new GsonBuilder().serializeNulls().create();

    public String readFile(final String fileName) throws IOException {
        final InputStream inputStream = Optional.ofNullable(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(fileName))
                .orElseThrow(IOException::new);

        final String file = new Scanner(inputStream, "UTF-8")
                .useDelimiter("\\A")
                .next();

        return null;//gson.toJson(JsonParser.parseString(file));
    }
}
