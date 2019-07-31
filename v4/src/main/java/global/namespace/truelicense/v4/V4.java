/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v4;

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
import global.namespace.truelicense.core.Core;
import global.namespace.truelicense.obfuscate.Obfuscate;
import global.namespace.truelicense.v4.auth.V4RepositoryContext;

import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.util.function.Supplier;
import java.util.zip.Deflater;

import static global.namespace.fun.io.bios.BIOS.deflate;

/**
 * This facade provides a static factory method for license management context builders for Version 4 (V4) format
 * license keys.
 * This class should not be used by applications because the created license management context builders are only
 * partially configured.
 */
public final class V4 {

    @Obfuscate
    private static final String ENCRYPTION_ALGORITHM = "PBEWithHmacSHA256AndAES_128";

    @Obfuscate
    private static final String KEYSTORE_TYPE = "PKCS12";

    /**
     * Returns a new license management context builder for managing V4 format license keys.
     */
    public static LicenseManagementContextBuilder builder() {
        return builder(ObjectMapper::new);
    }

    /**
     * Returns a new license management context builder for managing V4 format license keys using the given object
     * mapper factory.
     */
    public static LicenseManagementContextBuilder builder(Supplier<ObjectMapper> factory) {
        return Core
                .builder()
                .codec(new JsonCodec(() -> configure(factory.get())))
                .compression(deflate(Deflater.BEST_COMPRESSION))
                .encryptionAlgorithm(ENCRYPTION_ALGORITHM)
                .encryptionFactory(V4Encryption::new)
                .licenseFactory(new V4LicenseFactory())
                .repositoryContext(new V4RepositoryContext())
                .keystoreType(KEYSTORE_TYPE);
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

    private V4() {
    }
}
