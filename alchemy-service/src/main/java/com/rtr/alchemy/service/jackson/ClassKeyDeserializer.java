package com.rtr.alchemy.service.jackson;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

import java.io.IOException;

/**
 * Allows Jackson to deserialize Map keys of type Class&lt;?&gt;
 */
public class ClassKeyDeserializer extends KeyDeserializer {
    @Override
    public Object deserializeKey(String className, DeserializationContext context) throws IOException {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IOException(String.format("could not find class %s", className));
        }
    }
}
