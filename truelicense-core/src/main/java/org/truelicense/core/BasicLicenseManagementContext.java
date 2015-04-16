/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.api.*;
import org.truelicense.api.auth.Authentication;
import org.truelicense.api.auth.KeyStoreParameters;
import org.truelicense.api.auth.Repository;
import org.truelicense.api.io.BIOS;
import org.truelicense.core.auth.Notary;
import org.truelicense.spi.io.StandardBIOS;
import org.truelicense.spi.misc.Option;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * A basic license management context.
 * This class is immutable.
 * <p>
 * Unless stated otherwise, all no-argument methods need to return consistent
 * objects so that caching them is not required.
 * A returned object is considered to be consistent if it compares
 * {@linkplain Object#equals(Object) equal} or at least behaves identical to
 * any previously returned object.
 *
 * @param <PasswordSpecification> the generic password specification type.
 * @author Christian Schlichtherle
 */
public abstract class BasicLicenseManagementContext<PasswordSpecification>
implements LicenseManagementContext<PasswordSpecification> {

    private final String subject;

    protected BasicLicenseManagementContext(final String subject) {
        this.subject = Objects.requireNonNull(subject);
    }

    /**
     * Returns an authentication for the given key store parameters.
     * <p>
     * The implementation in the class {@link BasicLicenseManagementContext}
     * returns a new {@link Notary} for the given parameters.
     *
     * @param parameters the key store parameters.
     */
    @Override
    public Authentication authentication(KeyStoreParameters parameters) {
        return new Notary(parameters);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link BasicLicenseManagementContext}
     * returns an authorization which clears all operation requests.
     */
    @Override
    public LicenseAuthorization authorization() {
        return new BasicLicenseAuthorization();
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link BasicLicenseManagementContext}
     * returns a {@link StandardBIOS}.
     */
    @Override
    public BIOS bios() { return new StandardBIOS(); } // TODO: Consider caching this object.

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link BasicLicenseManagementContext}
     * returns half an hour (in milliseconds) to account for external changes
     * to the configured store for the license key.
     */
    @Override
    public long cachePeriodMillis() { return 30 * 60 * 1000; }

    @Override
    public final LicenseConsumerContext<PasswordSpecification> consumer() {
        return new BasicLicenseConsumerContext<>(this);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link BasicLicenseManagementContext}
     * returns an initialization which initializes the license
     * {@linkplain License#getConsumerType consumer type},
     * {@linkplain License#getHolder holder},
     * {@linkplain License#getIssued issue date/time},
     * {@linkplain License#getIssuer issuer} and
     * {@linkplain License#getSubject subject}
     * unless these properties are already set respectively.
     */
    @Override
    public LicenseInitialization initialization() {
        return new BasicLicenseInitialization(this);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link BasicLicenseManagementContext}
     * returns a new {@link Date}.
     */
    @Override
    public Date now() { return new Date(); }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link BasicLicenseManagementContext}
     * lists the current thread's context class loader, if not {@code null}.
     */
    @Override
    public List<ClassLoader> optionalClassLoader() {
        return Option.wrap(Thread.currentThread().getContextClassLoader());
    }

    /**
     * Returns a <em>new</em> repository to use for
     * {@linkplain #license licenses}.
     */
    @Override
    public abstract Repository repository();

    /** Returns the licensing subject. */
    @Override
    public final String subject() { return subject; }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link BasicLicenseManagementContext}
     * returns a validation which validates the license
     * {@linkplain License#getConsumerAmount consumer amount},
     * {@linkplain License#getConsumerType consumer type},
     * {@linkplain License#getHolder holder},
     * {@linkplain License#getIssued issue date/time},
     * {@linkplain License#getIssuer issuer},
     * {@linkplain License#getSubject subject},
     * {@linkplain License#getNotAfter not after date/time} (if set) and
     * {@linkplain License#getNotBefore not before date/time} (if set).
     */
    @Override
    public LicenseValidation validation() {
        return new BasicLicenseValidation(this);
    }

    @Override
    public final LicenseVendorContext<PasswordSpecification> vendor() {
        return new BasicLicenseVendorContext<>(this);
    }
}
