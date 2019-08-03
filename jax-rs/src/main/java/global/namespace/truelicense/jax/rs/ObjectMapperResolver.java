/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.jax.rs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import javax.security.auth.x500.X500Principal;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class ObjectMapperResolver implements ContextResolver<ObjectMapper> {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        final SimpleModule module = new SimpleModule();
        module.addSerializer(new StdSerializer<X500Principal>(X500Principal.class) {

            @Override
            public void serialize(X500Principal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
                gen.writeString(value.getName());
            }
        });
        module.addDeserializer(X500Principal.class, new StdDeserializer<X500Principal>(X500Principal.class) {

            @Override
            public X500Principal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                return new X500Principal(p.readValueAs(String.class));
            }
        });
        mapper.registerModule(module);
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }
}
