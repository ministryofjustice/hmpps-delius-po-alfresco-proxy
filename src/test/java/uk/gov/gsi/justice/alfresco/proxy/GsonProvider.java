package uk.gov.gsi.justice.alfresco.proxy;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.Instant;

public class GsonProvider {
  public Gson getGson() {
    final GsonBuilder gsonBuilder = new GsonBuilder();

    gsonBuilder.registerTypeAdapter(Instant.class, new InstantJsonSerializer());

    return gsonBuilder.serializeNulls().setPrettyPrinting().create();
  }

  public static class InstantJsonSerializer implements JsonSerializer<Instant> {
    @Override
    public JsonElement serialize(
        final Instant src, final Type typeOfSrc, final JsonSerializationContext context) {
      return new JsonPrimitive(src.toString());
    }
  }
}
