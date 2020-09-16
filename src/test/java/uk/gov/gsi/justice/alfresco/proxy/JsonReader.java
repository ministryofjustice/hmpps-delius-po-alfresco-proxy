package uk.gov.gsi.justice.alfresco.proxy;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import java.io.IOException;

public class JsonReader {
    private final TestFileReader fileReader = new TestFileReader();
    private final Gson gson = new GsonProvider().getGson();

    public String readFile(final String fileName) throws IOException {
        final String file = fileReader.readFile(fileName);

        return gson.toJson(JsonParser.parseString(file));
    }
}
