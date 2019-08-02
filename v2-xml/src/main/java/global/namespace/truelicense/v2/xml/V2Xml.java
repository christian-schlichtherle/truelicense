/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v2.xml;

import global.namespace.truelicense.api.LicenseManagementContextBuilder;
import global.namespace.truelicense.api.auth.RepositoryFactory;
import global.namespace.truelicense.v2.core.V2;

/**
 * This facade provides a static factory method for license management context builders for use with Version 2-with-XML
 * (V2/XML) format license keys.
 *
 * @deprecated Since TrueLicense 4, this format is deprecated and should not be used for new applications.
 */
@Deprecated
public final class V2Xml {

    private static final V2XmlContext context = new V2XmlContext();

    /**
     * Returns a new license management context builder for managing Version 2-with-XML (V2/XML) format license keys.
     */
    @SuppressWarnings("unused")
    public static LicenseManagementContextBuilder builder() {
        return builder(context);
    }

    /**
     * Returns a new license management context builder for managing Version 2-with-XML (V2/XML) format license keys
     * using the given context.
     */
    public static LicenseManagementContextBuilder builder(V2XmlContext context) {
        return V2
                .builder()
                .codec(context.codec())
                .licenseFactory(new V2XmlLicenseFactory())
                .repositoryFactory(repositoryFactory());
    }

    /**
     * For testing only: Returns the repository factory for use with V2/XML format license keys.
     */
    public static RepositoryFactory<?> repositoryFactory() {
        return new V2XmlRepositoryFactory();
    }

    private V2Xml() {
    }
}
