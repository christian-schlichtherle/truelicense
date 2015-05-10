/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.jax.rs;

import org.truelicense.obfuscate.Obfuscate;

import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.Objects;

import static javax.ws.rs.core.MediaType.*;

/**
 * Maps a consumer license management service exception to an HTTP response.
 * This class is immutable.
 *
 * @since  TrueLicense 2.3
 * @author Christian Schlichtherle
 */
@Provider
@Produces({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML, TEXT_PLAIN })
public final class ConsumerLicenseManagementServiceExceptionMapper
implements ExceptionMapper<ConsumerLicenseManagementServiceException> {

    @Obfuscate private static final String MESSAGE = "message";

    private static final QName message = new QName(MESSAGE);

    private final HttpHeaders headers;

    public ConsumerLicenseManagementServiceExceptionMapper(final @Context HttpHeaders headers) {
        this.headers = Objects.requireNonNull(headers);
    }

    @Override public Response toResponse(final ConsumerLicenseManagementServiceException ex) {
        final String msg = ex.getMessage();
        final MediaType mt = headers.getMediaType();
        final ResponseBuilder rb = Response.status(ex.getStatus());
        if (APPLICATION_JSON_TYPE.equals(mt))
            rb.type(APPLICATION_JSON_TYPE)
              .entity('"' + msg + '"');
        else if (APPLICATION_XML_TYPE.equals(mt))
            rb.type(APPLICATION_XML_TYPE)
              .entity(new JAXBElement<String>(message, String.class, msg));
        else if (TEXT_XML_TYPE.equals(mt))
            rb.type(TEXT_XML_TYPE)
              .entity(new JAXBElement<String>(message, String.class, msg));
        else
            rb.type(TEXT_PLAIN_TYPE)
              .entity(msg);
        return rb.build();
    }
}
