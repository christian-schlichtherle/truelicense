/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.v2.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import net.truelicense.api.codec.Codec;
import net.truelicense.v2.commons.V2LicenseApplicationContext;
import net.truelicense.v2.json.codec.JsonCodec;

/**
 * The root context for applications which need to manage of Version-2-with-JSON
 * (V2/JSON) format license keys.
 * Note that there is no compatibility between different format license keys.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
public class V2JsonLicenseApplicationContext
extends V2LicenseApplicationContext {

    private volatile ObjectMapper mapper;

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link V2JsonLicenseApplicationContext}
     * returns a new {@link JsonCodec}.
     */
    @Override
    public Codec codec() {
        return new JsonCodec(objectMapper());
    }

    private ObjectMapper objectMapper() {
        final ObjectMapper m = mapper;
        return null != m ? m : (mapper = newObjectMapper());
    }

    /**
     * Returns a new object mapper for use with {@linkplain #license licenses}
     * and {@linkplain #repositoryContext repositories}.
     * This method is normally only called once.
     * In a multi-threaded environment, it may get called more than once, but
     * then each invocation must return an object which behaves equivalent to
     * any previously returned object.
     */
    protected ObjectMapper newObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        mapper.registerModule(new JaxbAnnotationModule());
        return mapper;
    }
}
