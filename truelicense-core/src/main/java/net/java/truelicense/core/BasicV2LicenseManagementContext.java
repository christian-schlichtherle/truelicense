/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core;

import javax.annotation.concurrent.Immutable;
import net.java.truelicense.core.auth.BasicRepository;
import net.java.truelicense.core.crypto.*;
import net.java.truelicense.core.io.Transformation;
import net.java.truelicense.obfuscate.Obfuscate;

/**
 * The root context for the management of V2 format license keys.
 *
 * @author Christian Schlichtherle
 */
@Immutable
public abstract class BasicV2LicenseManagementContext
extends BasicLicenseManagementContext {

    @Obfuscate
    private static final String STORE_TYPE = "JCEKS";

    @Obfuscate
    private static final String PBE_ALGORITHM = "PBEWithSHA1AndDESede";

    protected BasicV2LicenseManagementContext(String subject) { super(subject); }

    @Override public License license() { return new License(); }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link BasicV2LicenseManagementContext}
     * returns {@code "JCEKS"}.
     */
    @Override public final String storeType() { return STORE_TYPE; }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link BasicV2LicenseManagementContext}
     * returns a new {@link BasicRepository}.
     */
    @Override public final BasicRepository repository() {
        return new BasicRepository();
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link BasicV2LicenseManagementContext}
     * returns a compression for V2 format license keys.
     */
    @Override public final Transformation compression() {
        return new V2Compression();
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link BasicV2LicenseManagementContext}
     * returns {@code "PBEWithSHA1AndDESede"}.
     */
    @Override public final String pbeAlgorithm() { return PBE_ALGORITHM; }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link BasicV2LicenseManagementContext}
     * returns an encryption for V2 format license keys with the given
     * parameters.
     */
    @Override public final Encryption encryption(PbeParameters pbe) {
        return new V2Encryption(pbe);
    }
}
