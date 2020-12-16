package com.ms.wmbanking.azure.common.azure;

import com.google.gson.Gson;
import lombok.*;

import java.util.Date;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Should use {@link EventGridEvent}, but somehow it's not included in Functions libs (requires extra dep'y).
 * @see EventGridEvent
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@With
@ToString
@EqualsAndHashCode
public class EventMessage {
    public String id;
    public String subject;
    public String data;
    public String eventType;
    public String dataVersion;
    public String metadataVersion;
    public Date eventTime;
    public String topic;

    public <T> T getAs(final Gson mapper, final Class<T> clazz) {
        return isNotBlank(data)
               ? mapper.fromJson(data, clazz)
               : null;
    }
}
