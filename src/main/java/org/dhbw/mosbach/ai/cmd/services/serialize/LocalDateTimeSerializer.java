package org.dhbw.mosbach.ai.cmd.services.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.dhbw.mosbach.ai.cmd.util.CmdConfig;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeResolver extends JsonSerializer<LocalDateTime> {

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.format(CmdConfig.API_DATE_FORMATTER));
    }
}
