package uk.gov.gsi.justice.po.alfresco.proxy.provider;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Instant;

public class GsonProvider {
    public Gson getGson() {
        final GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(Instant.class, new InstantJsonSerializer());

        return gsonBuilder
                .setPrettyPrinting()
                .create();
    }

    static public class InstantJsonSerializer implements JsonSerializer<Instant> {
        @Override
        public JsonElement serialize(final Instant src, final Type typeOfSrc, final JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }
}
