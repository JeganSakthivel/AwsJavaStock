package com.microservice.stock.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;

public class DateTimeSerializer extends StdSerializer<Date> {
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");


    public DateTimeSerializer() {
        this(null);
    }
    public DateTimeSerializer(Class<Date> t) {
        super(t);
    }

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        try {
            String s = dateFormatter.format(value);
            gen.writeString(s);
        } catch (DateTimeParseException e) {
            System.err.println(e);
            gen.writeString("");
        }
    }

}
