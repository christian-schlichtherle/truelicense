/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.v2.commons;

import net.truelicense.api.License;
import net.truelicense.api.auth.RepositoryContext;
import net.truelicense.api.crypto.EncryptionParameters;
import net.truelicense.api.io.Transformation;
import net.truelicense.core.TrueLicenseApplicationContext;
import net.truelicense.obfuscate.Obfuscate;
import net.truelicense.v2.commons.auth.V2RepositoryContext;
import net.truelicense.v2.commons.auth.V2RepositoryModel;
import net.truelicense.v2.commons.comp.V2Compression;
import net.truelicense.v2.commons.crypto.V2Encryption;

/**
 * The root context for applications which need to manage V2/* format license
 * keys.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
public abstract class V2LicenseApplicationContext
extends TrueLicenseApplicationContext<V2RepositoryModel> {

    @Obfuscate
    private static final String STORE_TYPE = "JCEKS";

    @Obfuscate
    private static final String PBE_ALGORITHM = "PBEWithSHA1AndDESede";

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link V2LicenseApplicationContext}
     * returns a compression for V2 format license keys.
     */
    @Override
    public final Transformation compression() {
        return new V2Compression();
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link V2LicenseApplicationContext}
     * returns an encryption for V2 format license keys with the given PBE
     * parameters.
     */
    @Override
    public final Transformation encryption(EncryptionParameters parameters) {
        return new V2Encryption(parameters);
    }

    @Override
    public License license() { return new License(); }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link V2LicenseApplicationContext}
     * returns {@code "PBEWithSHA1AndDESede"}.
     */
    @Override
    public final String pbeAlgorithm() { return PBE_ALGORITHM; }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link V2LicenseApplicationContext}
     * returns a new {@link V2RepositoryContext}.
     */
    @Override
    public final RepositoryContext<V2RepositoryModel> repositoryContext() {
        return new V2RepositoryContext();
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link V2LicenseApplicationContext}
     * returns {@code "JCEKS"}.
     */
    @Override
    public final String storeType() { return STORE_TYPE; }
}
