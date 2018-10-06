/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.truelicense.v2.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import net.truelicense.api.LicenseManagementContextBuilder;
import net.truelicense.v2.core.V2;

/**
 * This facade provides a static factory method for license management context builders for Version 2-with-JSON
 * (V2/JSON) format license keys.
 *
 * @author Christian Schlichtherle
 */
public final class V2Json {

    /**
     * Returns a new license management context builder for managing Version 2-with-JSON (V2/JSON) format license keys.
     */
    public static LicenseManagementContextBuilder builder() { return builder(objectMapper()); }

    /**
     * Returns a new license management context builder for managing Version 2-with-JSON (V2/JSON) format license keys
     * using the given object mapper.
     */
    public static LicenseManagementContextBuilder builder(ObjectMapper mapper) {
        return V2.builder().codec(new JsonCodec(mapper));
    }

    private static ObjectMapper objectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        mapper.registerModule(new JaxbAnnotationModule());
        return mapper;
    }

    private V2Json() { }
}
