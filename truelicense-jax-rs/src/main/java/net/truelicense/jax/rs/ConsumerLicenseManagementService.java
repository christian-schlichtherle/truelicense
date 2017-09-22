/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.jax.rs;

import net.truelicense.api.ConsumerLicenseManager;
import net.truelicense.api.License;
import net.truelicense.api.LicenseManagementException;
import net.truelicense.obfuscate.Obfuscate;
import net.truelicense.spi.io.MemoryStore;

import javax.inject.Provider;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.net.URI;
import java.util.Objects;

import static javax.ws.rs.core.MediaType.*;
import static javax.ws.rs.core.Response.Status.*;

/**
 * A RESTful web service for license management in consumer applications.
 * This class is immutable.
 *
 * @since  TrueLicense 2.3
 * @author Christian Schlichtherle
 */
// TODO: Use Neuron DI for dependency injection.
@Path(ConsumerLicenseManagementService.LICENSE)
@Produces({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
public final class ConsumerLicenseManagementService {

    @Obfuscate private static final String FALSE = "false";
    @Obfuscate static final String LICENSE = "license";
    @Obfuscate private static final String SUBJECT = "subject";
    @Obfuscate private static final String VERIFY = "verify";

    private static final QName subject = new QName(SUBJECT);

    private static final URI licenseURI = URI.create(LICENSE);

    private final Provider<ConsumerLicenseManager> provider;

    public ConsumerLicenseManagementService(final Provider<ConsumerLicenseManager> provider) {
        this.provider = Objects.requireNonNull(provider);
    }

    private ConsumerLicenseManager manager() { return provider.get(); }

    @GET
    @Path(SUBJECT)
    @Produces(APPLICATION_JSON)
    public String subjectAsJson() { return '"' + subject() + '"'; }

    @GET
    @Path(SUBJECT)
    @Produces({ APPLICATION_XML, TEXT_XML })
    public JAXBElement<String> subjectAsXml() {
        return new JAXBElement<>(subject, String.class, subject());
    }

    @GET
    @Path(SUBJECT)
    @Produces(TEXT_PLAIN)
    public String subject() { return manager().context().subject(); }

    @POST
    public Response install(final byte[] key) throws ConsumerLicenseManagementServiceException {
        final MemoryStore store = new MemoryStore();
        store.data(key);
        try {
            manager().install(store);
        } catch (LicenseManagementException ex) {
            throw new ConsumerLicenseManagementServiceException(BAD_REQUEST, ex);
        }
        return Response.seeOther(licenseURI).build();
    }

    @GET
    public License view(final @QueryParam(VERIFY) @DefaultValue(FALSE) boolean verify)
    throws ConsumerLicenseManagementServiceException {
        final License license;
        try {
            license = manager().load();
        } catch (LicenseManagementException ex) {
            throw new ConsumerLicenseManagementServiceException(NOT_FOUND, ex);
        }
        if (verify) {
            try {
                manager().verify();
            } catch (LicenseManagementException ex) {
                throw new ConsumerLicenseManagementServiceException(PAYMENT_REQUIRED, ex);
            }
        }
        return license;
    }

    @DELETE
    public void uninstall() throws ConsumerLicenseManagementServiceException {
        try {
            manager().uninstall();
        } catch (LicenseManagementException ex) {
            throw new ConsumerLicenseManagementServiceException(NOT_FOUND, ex);
        }
    }
}
