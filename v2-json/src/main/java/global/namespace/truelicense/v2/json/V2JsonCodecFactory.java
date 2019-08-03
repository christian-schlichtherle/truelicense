/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v2.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import global.namespace.truelicense.api.License;
import global.namespace.truelicense.api.codec.Codec;
import global.namespace.truelicense.api.codec.CodecFactory;

import javax.security.auth.x500.X500Principal;
import java.io.IOException;

/**
 * A codec factory for use with V2/JSON format license keys.
 */
@SuppressWarnings("WeakerAccess")
public class V2JsonCodecFactory implements CodecFactory {

    public final Codec codec() {
        return new V2JsonCodec(this);
    }

    /**
     * Returns a new object mapper.
     */
    public ObjectMapper objectMapper() {
        return configure(new ObjectMapper());
    }

    /**
     * Configures and returns the given object mapper.
     */
    protected final ObjectMapper configure(final ObjectMapper mapper) {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        final SimpleModule module = new SimpleModule();
        module.addAbstractTypeMapping(License.class, V2JsonLicense.class);
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
        return mapper;
    }
}
