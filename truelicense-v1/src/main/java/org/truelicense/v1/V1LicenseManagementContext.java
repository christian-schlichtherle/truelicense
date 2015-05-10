/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.v1;

import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.xml.GenericCertificate;
import org.truelicense.api.License;
import org.truelicense.api.auth.RepositoryContext;
import org.truelicense.api.codec.Codec;
import org.truelicense.api.crypto.PbeParameters;
import org.truelicense.api.io.Transformation;
import org.truelicense.core.ObfuscatedLicenseManagementContext;
import org.truelicense.obfuscate.Obfuscate;
import org.truelicense.v1.auth.V1RepositoryContext;
import org.truelicense.v1.codec.X500PrincipalXmlCodec;
import org.truelicense.v1.comp.V1Compression;
import org.truelicense.v1.crypto.V1Encryption;

/**
 * The root context for the management of Version 1 (V1) format license keys.
 * This class is provided to enable applications to generate, install, verify
 * and uninstall license keys in the format for TrueLicense 1.X applications.
 * Since TrueLicense 2.0, this format is obsolete and should not be used for
 * new applications!
 * Note that there is no compatibility between different format license keys.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
public class V1LicenseManagementContext
extends ObfuscatedLicenseManagementContext<GenericCertificate> {

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

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link V1LicenseManagementContext}
     * returns an {@link X500PrincipalXmlCodec}.
     */
    @Override
    public Codec codec() { return codec; }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link V1LicenseManagementContext}
     * returns a compression for V1 format license keys.
     */
    @Override
    public final Transformation compression() {
        return new V1Compression();
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link V1LicenseManagementContext}
     * returns an encryption for V1 format license keys with the given PBE
     * parameters.
     */
    @Override
    public final Transformation encryption(PbeParameters parameters) {
        return new V1Encryption(parameters);
    }

    /** Returns a <em>new</em> license content. */
    // This introduces a cyclic dependency between the packages
    // de.schlichtherle.license and this package.
    // However, this is tolerable.
    @Override
    public License license() { return new LicenseContent(); }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link V1LicenseManagementContext}
     * returns {@code "PBEWithMD5AndDES"}.
     * This was the only supported PBE algorithm in TrueLicense 1 and its
     * not possible to use another one.
     */
    @Override
    public final String pbeAlgorithm() { return PBE_ALGORITHM; }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link V1LicenseManagementContext}
     * returns a new {@link V1RepositoryContext}.
     */
    @Override
    public final RepositoryContext<GenericCertificate> repositoryContext() {
        return new V1RepositoryContext();
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link V1LicenseManagementContext}
     * returns {@code "JKS"}.
     */
    @Override
    public final String storeType() { return STORE_TYPE; }
}
