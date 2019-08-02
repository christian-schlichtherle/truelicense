/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v2.xml;

import global.namespace.truelicense.api.License;
import global.namespace.truelicense.api.LicenseFactory;

/**
 * A license factory for use with V2/XML format license keys.
 */
final class V2XmlLicenseFactory implements LicenseFactory {

    @Override
    public License license() {
        return new V2XmlLicense();
    }

    @Override
    public Class<? extends License> licenseClass() {
        return V2XmlLicense.class;
    }
}
