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
import global.namespace.truelicense.api.LicenseManagementContextBuilder;
import global.namespace.truelicense.v2.core.V2;

import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.util.function.Supplier;

/**
 * This facade provides a static factory method for license management context builders for Version 2-with-JSON
 * (V2/JSON) format license keys.
 *
 * @deprecated Since TrueLicense 4, this format is deprecated and should not be used for new applications.
 */
@Deprecated
public final class V2Json {

    /**
     * Returns a new license management context builder for managing Version 2-with-JSON (V2/JSON) format license keys.
     */
    public static LicenseManagementContextBuilder builder() {
        return builder(ObjectMapper::new);
    }

    /**
     * Returns a new license management context builder for managing Version 2-with-JSON (V2/JSON) format license keys
     * using the given object mapper factory.
     */
    public static LicenseManagementContextBuilder builder(Supplier<ObjectMapper> factory) {
        return V2.builder().codec(new JsonCodec(() -> configure(factory.get())));
    }

    private static ObjectMapper configure(final ObjectMapper mapper) {
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
        return mapper;
    }

    private V2Json() {
    }
}
