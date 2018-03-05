/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.jax.rs;

import global.namespace.fun.io.api.Store;
import net.truelicense.api.ConsumerLicenseManager;
import net.truelicense.api.License;
import net.truelicense.api.LicenseManagementException;
import net.truelicense.dto.LicenseDTO;
import net.truelicense.dto.SubjectDTO;
import net.truelicense.obfuscate.Obfuscate;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.net.URI;
import java.util.Objects;

import static global.namespace.fun.io.bios.BIOS.memoryStore;
import static javax.ws.rs.core.MediaType.*;
import static javax.ws.rs.core.Response.Status.*;

/**
 * A RESTful web service for license management in consumer applications.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 * @since TrueLicense 2.3
 */
@Path(ConsumerLicenseManagementService.LICENSE)
public final class ConsumerLicenseManagementService {

    @Obfuscate
    private static final String FALSE = "false";

    @Obfuscate
    static final String LICENSE = "license";

    @Obfuscate
    private static final String SUBJECT = "subject";

    @Obfuscate
    private static final String VERIFY = "verify";

    private static final QName subject = new QName(SUBJECT);

    private static final URI licenseURI = URI.create(LICENSE);

    private final ConsumerLicenseManager manager;

    public ConsumerLicenseManagementService(final @Context ConsumerLicenseManager manager) {
        this.manager = Objects.requireNonNull(manager);
    }

    @GET
    @Path(SUBJECT)
    @Produces(TEXT_PLAIN)
    public String subject() {
        return manager.context().subject();
    }

    @GET
    @Path(SUBJECT)
    @Produces(APPLICATION_JSON)
    public SubjectDTO subjectAsJson() {
        return new SubjectDTO(subject());
    }

    @GET
    @Path(SUBJECT)
    @Produces({APPLICATION_XML, TEXT_XML})
    public JAXBElement<String> subjectAsXml() {
        return new JAXBElement<>(subject, String.class, subject());
    }

    @POST
    @Consumes(APPLICATION_OCTET_STREAM)
    public Response install(final byte[] key) throws ConsumerLicenseManagementServiceException {
        try {
            final Store store = memoryStore();
            store.content(key);
            manager.install(store.input());
        } catch (Exception e) {
            throw new ConsumerLicenseManagementServiceException(BAD_REQUEST, e);
        }
        return Response.seeOther(licenseURI).build();
    }

    @GET
    @Produces(APPLICATION_JSON)
    public LicenseDTO loadAsJson(@QueryParam(VERIFY) @DefaultValue(FALSE) boolean verify)
            throws ConsumerLicenseManagementServiceException {
        return new LicenseDTO(loadAsXml(verify));
    }

    @GET
    @Produces({APPLICATION_XML, TEXT_XML})
    public License loadAsXml(final @QueryParam(VERIFY) @DefaultValue(FALSE) boolean verify)
            throws ConsumerLicenseManagementServiceException {
        final License license;
        try {
            license = manager.load();
        } catch (LicenseManagementException e) {
            throw new ConsumerLicenseManagementServiceException(NOT_FOUND, e);
        }
        if (verify) {
            try {
                manager.verify();
            } catch (LicenseManagementException e) {
                throw new ConsumerLicenseManagementServiceException(PAYMENT_REQUIRED, e);
            }
        }
        return license;
    }

    @DELETE
    public void uninstall() throws ConsumerLicenseManagementServiceException {
        try {
            manager.uninstall();
        } catch (LicenseManagementException e) {
            throw new ConsumerLicenseManagementServiceException(NOT_FOUND, e);
        }
    }
}
