/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.v2.xml;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.*;

import org.truelicense.api.License;
import org.truelicense.api.LicenseConsumerContext;
import org.truelicense.api.LicenseVendorContext;
import org.truelicense.core.auth.BasicRepository;
import org.truelicense.core.codec.JaxbCodec;
import org.truelicense.obfuscate.Obfuscate;
import org.truelicense.v2.base.BasicV2LicenseManagementContext;

/**
 * The root context for the management of Version-2-with-XML (V2/XML) format
 * license keys.
 * Note that there is no compatibility between different format license keys.
 * <p>
 * Use this context to create a {@link LicenseVendorContext} or a
 * {@link LicenseConsumerContext}.
 * Here's an example for verifying the installed license key in a consumer
 * application:
 * <pre><code>
 * LicenseConsumerManager manager = new V2XmlLicenseManagementContext("MyApp 1")
 *         .consumer()
 *         .manager()
 *         ...
 *         .build();
 * manager.verify();
 * </code></pre>
 * <p>
 * DO NOT COPY-PASTE THIS CODE!
 * Instead, use the TrueLicense Maven Archetype to generate a sample project
 * for you.
 * <p>
 * Where required, you should subclass this class to customize its properties,
 * e.g. its {@linkplain #codec encoding}, {@linkplain #now clock},
 * {@linkplain #classLoader class loader} etc.
 * <p>
 * Note that this class is immutable.
 * Unless stated otherwise, all no-argument methods need to return consistent
 * objects so that caching them is not required.
 * A returned object is considered to be consistent if it compares
 * {@linkplain Object#equals(Object) equal} or at least behaves identical to
 * any previously returned object.
 *
 * @author Christian Schlichtherle
 */
@Immutable
public class V2XmlLicenseManagementContext
extends BasicV2LicenseManagementContext {

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
    @Override public JaxbCodec codec() { return new JaxbCodec(context()); }

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
     * and {@linkplain #repository repositories}.
     * This method is normally only called once.
     * In a multi-threaded environment, it may get called more than once, but
     * then each invocation must return an object which behaves equivalent to
     * any previously returned object.
     * <p>
     * The implementation in the class {@link V2XmlLicenseManagementContext}
     * constructs a new {@code JAXBContext} for the root element classes
     * {@link License} and {@link BasicRepository}.
     */
    protected JAXBContext newContext() {
        try {
            return JAXBContext.newInstance(License.class, BasicRepository.class);
        } catch (final JAXBException ex) {
            throw new AssertionError(ex);
        }
    }
}
