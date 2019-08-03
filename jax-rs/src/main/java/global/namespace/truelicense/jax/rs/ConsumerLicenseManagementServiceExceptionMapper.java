/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.jax.rs;

import global.namespace.truelicense.jax.rs.dto.ErrorDTO;

import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Objects;

import static javax.ws.rs.core.MediaType.*;

/**
 * Maps a consumer license management service exception to an HTTP response.
 * This class is immutable.
 */
@Provider
@Produces({APPLICATION_JSON, TEXT_PLAIN})
public final class ConsumerLicenseManagementServiceExceptionMapper
        implements ExceptionMapper<ConsumerLicenseManagementServiceException> {

    private final HttpHeaders headers;

    public ConsumerLicenseManagementServiceExceptionMapper(final @Context HttpHeaders headers) {
        this.headers = Objects.requireNonNull(headers);
    }

    @Override
    public Response toResponse(final ConsumerLicenseManagementServiceException ex) {
        final String message = ex.getMessage();
        final ResponseBuilder rb = Response.status(ex.getStatus()).type(TEXT_PLAIN_TYPE).entity(message);
        for (final MediaType mt : headers.getAcceptableMediaTypes()) {
            if (APPLICATION_JSON_TYPE.equals(mt)) {
                final ErrorDTO e = new ErrorDTO();
                e.error = message;
                rb.type(mt).entity(e);
                break;
            }
        }
        return rb.build();
    }
}
