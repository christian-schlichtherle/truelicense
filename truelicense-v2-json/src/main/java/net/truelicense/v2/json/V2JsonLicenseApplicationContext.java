/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.v2.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import net.truelicense.api.LicenseManagementContextBuilder;
import net.truelicense.v2.commons.V2LicenseApplicationContext;
import net.truelicense.v2.json.codec.JsonCodec;

/**
 * The root context for applications which need to manage of Version-2-with-JSON (V2/JSON) format license keys.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
public final class V2JsonLicenseApplicationContext extends V2LicenseApplicationContext {

    @Override
    public LicenseManagementContextBuilder context() { return super.context().codec(new JsonCodec(objectMapper())); }

    private ObjectMapper objectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        mapper.registerModule(new JaxbAnnotationModule());
        return mapper;
    }
}
