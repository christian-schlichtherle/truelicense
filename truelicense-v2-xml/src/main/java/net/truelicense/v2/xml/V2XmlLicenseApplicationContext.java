/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.v2.xml;

import net.truelicense.api.License;
import net.truelicense.api.LicenseManagementContextBuilder;
import net.truelicense.v2.commons.V2LicenseApplicationContext;
import net.truelicense.v2.commons.auth.V2RepositoryModel;
import net.truelicense.v2.xml.codec.XMLCodec;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * The root context for the management of Version-2-with-XML (V2/XML) format license keys.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
public final class V2XmlLicenseApplicationContext extends V2LicenseApplicationContext {

    @Override
    public LicenseManagementContextBuilder context() { return super.context().codec(new XMLCodec(jaxbContext())); }

    private JAXBContext jaxbContext() {
        try {
            return JAXBContext.newInstance(License.class, V2RepositoryModel.class);
        } catch (final JAXBException ex) {
            throw new AssertionError(ex);
        }
    }
}
