/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.ws.rs;

import net.java.truelicense.core.util.Objects;
import net.java.truelicense.obfuscate.Obfuscate;

import javax.annotation.concurrent.Immutable;
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

import static javax.ws.rs.core.MediaType.*;

/**
 * Maps a license consumer service exception to an HTTP response.
 *
 * @author Christian Schlichtherle
 * @since TrueLicense 2.3
 */
@Provider
@Produces({APPLICATION_JSON, APPLICATION_XML, TEXT_XML, TEXT_PLAIN})
@Immutable
public final class LicenseConsumerServiceExceptionMapper
        implements ExceptionMapper<LicenseConsumerServiceException> {

    @Obfuscate
    private static final String MESSAGE = "message";

    private static final QName message = new QName(MESSAGE);

    private final HttpHeaders headers;

    public LicenseConsumerServiceExceptionMapper(final @Context HttpHeaders headers) {
        this.headers = Objects.requireNonNull(headers);
    }

    @Override
    public Response toResponse(final LicenseConsumerServiceException ex) {
        final String msg = ex.getMessage();
        final ResponseBuilder rb = Response.status(ex.getStatus()).type(TEXT_PLAIN_TYPE).entity(msg);
        for (final MediaType mt : headers.getAcceptableMediaTypes()) {
            if (APPLICATION_JSON_TYPE.equals(mt)) {
                rb.type(APPLICATION_JSON_TYPE).entity(msg);
                break;
            } else if (APPLICATION_XML_TYPE.equals(mt)) {
                rb.type(APPLICATION_XML_TYPE).entity(new JAXBElement<String>(message, String.class, msg));
                break;
            } else if (TEXT_XML_TYPE.equals(mt)) {
                rb.type(TEXT_XML_TYPE).entity(new JAXBElement<String>(message, String.class, msg));
                break;
            }
        }
        return rb.build();
    }
}
