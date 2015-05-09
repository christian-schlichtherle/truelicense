/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.v2.xml;

import org.truelicense.api.License;
import org.truelicense.api.codec.Codec;
import org.truelicense.obfuscate.Obfuscate;
import org.truelicense.v2.commons.V2LicenseManagementContext;
import org.truelicense.v2.commons.auth.V2RepositoryController;
import org.truelicense.v2.commons.auth.V2RepositoryModel;
import org.truelicense.v2.xml.codec.JaxbCodec;

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
public class V2XmlLicenseManagementContext
extends V2LicenseManagementContext {

    private volatile JAXBContext context;

    /**
     * Constructs a V2/XML license management context.
     * The provided string should get computed on demand from an obfuscated
     * form, e.g. by annotating a constant string value with the
     * {@literal @}{@link Obfuscate} annotation and processing it with the
     * TrueLicense Maven Plugin.
     *
     * @param subject the licensing subject, i.e. a product name with an
     *        optional version range, e.g. {@code MyApp 1}.
     */
    public V2XmlLicenseManagementContext(String subject) { super(subject); }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link V2XmlLicenseManagementContext}
     * returns a {@link JaxbCodec}.
     */
    @Override
    public Codec codec() { return new JaxbCodec(context()); }

    /**
     * Returns the JAXB context to use for {@link #codec}.
     * <p>
     * The implementation in the class {@link V2XmlLicenseManagementContext}
     * lazily resolves this property by calling {@link #newContext}.
     */
    public JAXBContext context() {
        final JAXBContext c = context;
        return null != c ? c : (context = newContext());
    }

    /**
     * Returns a new JAXB context for use with {@linkplain #license licenses}
     * and {@linkplain #repositoryContext repositories}.
     * This method is normally only called once.
     * In a multi-threaded environment, it may get called more than once, but
     * then each invocation must return an object which behaves equivalent to
     * any previously returned object.
     * <p>
     * The implementation in the class {@link V2XmlLicenseManagementContext}
     * constructs a new {@code JAXBContext} for the root element classes
     * {@link License} and {@link V2RepositoryController}.
     */
    protected JAXBContext newContext() {
        try {
            return JAXBContext.newInstance(License.class, V2RepositoryModel.class);
        } catch (final JAXBException ex) {
            throw new AssertionError(ex);
        }
    }
}
