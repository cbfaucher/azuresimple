package com.ms.wmbanking.azure.jackson;


import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class LocalDateTimeSerializer extends TypeAdapter<LocalDateTime> implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {


    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext context) {
        return Optional.ofNullable(localDateTime)
                       .map(dt -> (JsonElement) new JsonPrimitive(dt.format(DATE_TIME_FORMATTER)))
                       .orElse(JsonNull.INSTANCE);
    }

    @Override
    public LocalDateTime deserialize(JsonElement json,
                                     Type typeOfT,
                                     JsonDeserializationContext context) throws JsonParseException {

        return Optional.ofNullable(json.getAsString())
                       .filter(StringUtils::isNotBlank)
                       .map(s -> LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                       .orElse(null);
    }

    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value != null) {
            out.jsonValue("\"" + value.format(DATE_TIME_FORMATTER) + "\"");
        } else {
            out.nullValue();
        }
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        return  Optional.ofNullable(in.nextString())
                        .filter(StringUtils::isNotBlank)
                        .map(s -> LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .orElse(null);
    }
}
