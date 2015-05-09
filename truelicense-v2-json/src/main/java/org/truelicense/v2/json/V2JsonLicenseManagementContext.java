/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.v2.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.truelicense.api.codec.Codec;
import org.truelicense.v2.commons.V2LicenseManagementContext;
import org.truelicense.v2.json.codec.JsonCodec;

/**
 * The root context for the management of Version-2-with-JSON (V2/JSON) format
 * license keys.
 * Note that there is no compatibility between different format license keys.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
public class V2JsonLicenseManagementContext
extends V2LicenseManagementContext {

    private volatile ObjectMapper mapper;

    /**
     * Constructs a V2/JSON license management context.
     * The provided string should get computed on demand from an obfuscated
     * form, e.g. by annotating a constant string value with the
     * {@literal @}{@link org.truelicense.obfuscate.Obfuscate} annotation
     * and processing it with the TrueLicense Maven Plugin.
     *
     * @param subject the licensing subject, i.e. a product name with an
     *        optional version range, e.g. {@code MyApp 1}.
     */
    public V2JsonLicenseManagementContext(String subject) { super(subject); }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link V2JsonLicenseManagementContext}
     * returns a new {@link JsonCodec}.
     */
    @Override
    public Codec codec() {
        return new JsonCodec(mapper());
    }

    /**
     * Returns the object mapper to use for {@link #codec}.
     * <p>
     * The implementation in the class {@link V2JsonLicenseManagementContext}
     * lazily resolves this property by calling {@link #newMapper}.
     */
    public ObjectMapper mapper() {
        final ObjectMapper m = mapper;
        return null != m ? m : (mapper = newMapper());
    }

    /**
     * Returns a new object mapper for use with {@linkplain #license licenses}
     * and {@linkplain #repositoryContext repositories}.
     * This method is normally only called once.
     * In a multi-threaded environment, it may get called more than once, but
     * then each invocation must return an object which behaves equivalent to
     * any previously returned object.
     */
    protected ObjectMapper newMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        mapper.registerModule(new JaxbAnnotationModule());
        return mapper;
    }
}
