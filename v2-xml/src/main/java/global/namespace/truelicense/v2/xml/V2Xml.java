/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v2.xml;

import global.namespace.truelicense.api.LicenseManagementContextBuilder;
import global.namespace.truelicense.v2.core.V2;
import global.namespace.truelicense.v2.core.auth.V2RepositoryModel;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * This facade provides a static factory method for license management context builders for Version 2-with-XML (V2/XML)
 * format license keys.
 *
 * @deprecated Since TrueLicense 4, this format is deprecated and should not be used for new applications.
 */
@Deprecated
public final class V2Xml {

    /**
     * Returns a new license management context builder for managing Version 2-with-XML (V2/XML) format license keys.
     */
    public static LicenseManagementContextBuilder builder() {
        return builder(jaxbContext());
    }

    /**
     * Returns a new license management context builder for managing Version 2-with-XML (V2/XML) format license keys
     * using the given JAXB context.
     */
    @SuppressWarnings("WeakerAccess")
    public static LicenseManagementContextBuilder builder(JAXBContext ctx) {
        return V2
                .builder()
                .codec(new XMLCodec(ctx))
                .licenseFactory(new V2XmlLicenseFactory());
    }

    private static JAXBContext jaxbContext() {
        try {
            return JAXBContext.newInstance(V2XmlLicense.class, V2RepositoryModel.class);
        } catch (final JAXBException ex) {
            throw new AssertionError(ex);
        }
    }

    private V2Xml() {
    }
}
