package io.github.jlmc.spi.json;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

public class JsonProducer {

    public static final Jsonb INSTANCE = JsonbBuilder.create(createJsonbConfig());

    public static String toJson(Object entity) {
        return INSTANCE.toJson(entity);
    }

    private static JsonbConfig createJsonbConfig() {
        return new JsonbConfig()
                //.withEncoding(StandardCharsets.UTF_8.name())
                .withNullValues(true)
                .withPropertyVisibilityStrategy(FieldAccessStrategy.INSTANCE);
    }
}
