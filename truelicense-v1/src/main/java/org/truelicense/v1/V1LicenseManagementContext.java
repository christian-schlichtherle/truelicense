/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.v1;

import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.xml.GenericCertificate;
import org.truelicense.api.LicenseConsumerContext;
import org.truelicense.api.LicenseVendorContext;
import org.truelicense.api.crypto.Encryption;
import org.truelicense.api.crypto.PbeParameters;
import org.truelicense.api.io.Transformation;
import org.truelicense.core.CommonLicenseManagementContext;
import org.truelicense.v1.codec.X500PrincipalXmlCodec;
import org.truelicense.obfuscate.Obfuscate;
import org.truelicense.v1.commons.V1Compression;
import org.truelicense.v1.commons.V1Encryption;

import javax.annotation.concurrent.Immutable;

/**
 * The root context for the management of Version 1 (V1) format license keys.
 * This class is provided to enable applications to create, install, verify
 * and uninstall license keys in the format for TrueLicense 1.X applications.
 * Since TrueLicense 2.0, this format is obsolete and should not be used in
 * new applications!
 * Note that there is no compatibility between different format license keys.
 * <p>
 * Use this context to create a {@link LicenseVendorContext} or a
 * {@link LicenseConsumerContext}.
 * Here's an example for verifying the installed license key in a consumer
 * application:
 * <pre><code>
 * LicenseConsumerManager manager = new V1LicenseManagementContext("MyApp 1")
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
public class V1LicenseManagementContext extends CommonLicenseManagementContext {

    @Obfuscate
    private static final String STORE_TYPE = "JKS";

    @Obfuscate
    private static final String PBE_ALGORITHM = "PBEWithMD5AndDES";

    private final X500PrincipalXmlCodec codec = new X500PrincipalXmlCodec();

    /**
     * Constructs a V1 license management context.
     * The provided string should get computed on demand from an obfuscated
     * form, e.g. by annotating a constant string value with the
     * {@literal @}{@link Obfuscate} annotation and processing it with the
     * TrueLicense Maven Plugin.
     *
     * @param subject the licensing subject, i.e. a product name with an
     *        optional version range, e.g. {@code MyApp 1}.
     */
    public V1LicenseManagementContext(String subject) { super(subject); }

    /** Returns a <em>new</em> license content. */
    // This introduces a cyclic dependency between the packages
    // de.schlichtherle.license and this package.
    // However, this is tolerable.
    @Override public LicenseContent license() { return new LicenseContent(); }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link V1LicenseManagementContext}
     * returns {@code "JKS"}.
     */
    @Override public final String storeType() { return STORE_TYPE; }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link V1LicenseManagementContext}
     * returns a new {@link GenericCertificate}.
     */
    @Override public final GenericCertificate repository() {
        return new GenericCertificate();
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link V1LicenseManagementContext}
     * returns an {@link X500PrincipalXmlCodec}.
     */
    @Override public X500PrincipalXmlCodec codec() { return codec; }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link V1LicenseManagementContext}
     * returns a compression for V1 format license keys.
     */
    @Override public final Transformation compression() {
        return new V1Compression();
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link V1LicenseManagementContext}
     * returns {@code "PBEWithMD5AndDES"}.
     * This was the only supported PBE algorithm in TrueLicense 1 and its
     * not possible to use another one.
     */
    @Override public final String pbeAlgorithm() { return PBE_ALGORITHM; }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link V1LicenseManagementContext}
     * returns an encryption for V1 format license keys with the given
     * parameters.
     */
    @Override public final Encryption encryption(PbeParameters pbe) {
        return new V1Encryption(pbe);
    }
}
