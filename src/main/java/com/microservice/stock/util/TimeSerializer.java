package com.microservice.stock.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;

public class TimeSerializer extends StdSerializer<Time> {
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");

    public TimeSerializer(){
        this(null);
    }

    protected TimeSerializer(Class<Time> t) {
        super(t);
    }


    @Override
    public void serialize(Time value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        try {
            String s = timeFormatter.format(value);
            gen.writeString(s);
        } catch (DateTimeParseException e) {
            System.err.println(e);
            gen.writeString("");
        }
    }

}
