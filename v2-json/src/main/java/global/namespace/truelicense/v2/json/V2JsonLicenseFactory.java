/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v2.json;

import global.namespace.truelicense.api.License;
import global.namespace.truelicense.api.LicenseFactory;

/**
 * A license factory for V2/JSON format license keys.
 */
final class V2JsonLicenseFactory implements LicenseFactory {

    @Override
    public License license() {
        return new V2JsonLicense();
    }

    @Override
    public Class<? extends License> licenseClass() {
        return V2JsonLicense.class;
    }
}
