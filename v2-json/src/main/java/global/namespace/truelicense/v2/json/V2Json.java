/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v2.json;

import global.namespace.truelicense.api.LicenseManagementContextBuilder;
import global.namespace.truelicense.api.auth.RepositoryFactory;
import global.namespace.truelicense.v2.core.V2;

/**
 * This facade provides a static factory method for license management context builders for use with Version 2-with-JSON
 * (V2/JSON) format license keys.
 *
 * @deprecated Since TrueLicense 4, this format is deprecated and should not be used for new applications.
 */
@Deprecated
public final class V2Json {

    private static final V2JsonContext context = new V2JsonContext();

    /**
     * Returns a new license management context builder for managing Version 2-with-JSON (V2/JSON) format license keys.
     */
    public static LicenseManagementContextBuilder builder() {
        return builder(context);
    }

    /**
     * Returns a new license management context builder for managing Version 2-with-JSON (V2/JSON) format license keys
     * using the given context.
     */
    @SuppressWarnings("WeakerAccess")
    public static LicenseManagementContextBuilder builder(V2JsonContext context) {
        return V2
                .builder()
                .codec(context.codec())
                .licenseFactory(new V2JsonLicenseFactory())
                .repositoryFactory(repositoryFactory());
    }

    /**
     * For testing only: Returns the repository factory for use with V2/JSON format license keys.
     */
    public static RepositoryFactory<?> repositoryFactory() {
        return new V2JsonRepositoryFactory();
    }

    private V2Json() {
    }
}
