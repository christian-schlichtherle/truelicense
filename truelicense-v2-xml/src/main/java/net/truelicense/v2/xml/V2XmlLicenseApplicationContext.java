/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.v2.xml;

import net.truelicense.api.License;
import net.truelicense.api.codec.Codec;
import net.truelicense.v2.commons.V2LicenseApplicationContext;
import net.truelicense.v2.commons.auth.V2RepositoryModel;
import net.truelicense.v2.xml.codec.JaxbCodec;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * The root context for the management of Version-2-with-XML (V2/XML) format
 * license keys.
 * Note that there is no compatibility between different format license keys.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
public class V2XmlLicenseApplicationContext
extends V2LicenseApplicationContext {

    private volatile JAXBContext context;

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link V2XmlLicenseApplicationContext}
     * returns a {@link JaxbCodec}.
     */
    @Override
    public Codec codec() { return new JaxbCodec(jaxbContext()); }

    private JAXBContext jaxbContext() {
        final JAXBContext c = context;
        return null != c ? c : (context = newJaxbContext());
    }

    /**
     * Returns a new JAXB context for use with {@linkplain #license licenses}
     * and {@linkplain #repositoryContext repositories}.
     * This method is normally only called once.
     * In a multi-threaded environment, it may get called more than once, but
     * then each invocation must return an object which behaves equivalent to
     * any previously returned object.
     * <p>
     * The implementation in the class {@link V2XmlLicenseApplicationContext}
     * constructs a new {@code JAXBContext} for the root element classes
     * {@link License} and {@link V2RepositoryModel}.
     */
    protected JAXBContext newJaxbContext() {
        try {
            return JAXBContext.newInstance(License.class, V2RepositoryModel.class);
        } catch (final JAXBException ex) {
            throw new AssertionError(ex);
        }
    }
}
