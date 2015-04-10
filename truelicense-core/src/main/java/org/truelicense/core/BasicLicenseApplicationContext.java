/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.api.*;
import org.truelicense.api.auth.Authentication;
import org.truelicense.api.auth.AuthenticationParameters;
import org.truelicense.api.auth.Repository;
import org.truelicense.api.auth.RepositoryProvider;
import org.truelicense.api.codec.Codec;
import org.truelicense.api.crypto.Encryption;
import org.truelicense.api.crypto.PbeParameters;
import org.truelicense.api.io.Sink;
import org.truelicense.api.io.Source;
import org.truelicense.api.io.Store;
import org.truelicense.api.io.Transformation;
import org.truelicense.api.misc.ClassLoaderProvider;
import org.truelicense.api.misc.Clock;
import org.truelicense.api.passwd.Password;
import org.truelicense.api.passwd.PasswordPolicy;
import org.truelicense.api.passwd.PasswordPolicyProvider;
import org.truelicense.api.passwd.PasswordProtection;
import org.truelicense.core.io.*;

import javax.annotation.CheckForNull;
import javax.annotation.concurrent.Immutable;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.prefs.Preferences;

import static java.util.Calendar.DATE;
import static java.util.Calendar.getInstance;
import static java.util.Objects.requireNonNull;

/**
 * A basic context for license applications.
 * <p>
 * Unless stated otherwise, all no-argument methods need to return consistent
 * objects so that caching them is not required.
 * A returned object is considered to be consistent if it compares
 * {@linkplain Object#equals(Object) equal} or at least behaves identical to
 * any previously returned object.
 *
 * @author Christian Schlichtherle
 */
@Immutable
abstract class BasicLicenseApplicationContext
implements ClassLoaderProvider,
           Clock,
           LicenseApplicationContext,
           LicenseSubjectProvider,
           PasswordPolicyProvider {

    private final LicenseManagementContext context;

    BasicLicenseApplicationContext(final LicenseManagementContext context) {
        assert null != context;
        this.context = context;
    }

    @Override
    public final String subject() { return context().subject(); }

    @Override
    public final LicenseManagementContext context() { return context; }

    @Override
    public final PasswordPolicy policy() { return context().policy(); }

    @Override
    public final Date now() { return context().now(); }

    @Override
    public final @CheckForNull ClassLoader classLoader() {
        return context().classLoader();
    }

    final LicenseParameters parameters(
            Authentication authentication,
            Encryption encryption) {
        return parameters(context().initialization(), authentication, encryption);
    }

    final LicenseParameters ftpParameters(
            final LicenseConsumerManager parent,
            final Authentication authentication,
            final @CheckForNull Encryption encryption,
            final int days) {
        if (0 >= days) throw new IllegalArgumentException();
        final LicenseInitialization initialization = new LicenseInitialization() {
            final LicenseInitialization initialization = context().initialization();

            @Override public void initialize(final License bean) {
                initialization.initialize(bean);
                final Calendar cal = getInstance();
                cal.setTime(bean.getIssued());
                bean.setNotBefore(cal.getTime()); // not before issued
                cal.add(DATE, days); // FTP countdown starts NOW
                bean.setNotAfter(cal.getTime());
            }
        };
        return chainedParameters(parent, initialization, authentication, encryption);
    }

    final LicenseParameters chainedParameters(
            final LicenseConsumerManager parent,
            final Authentication authentication,
            final @CheckForNull Encryption encryption) {
        return chainedParameters(parent, context().initialization(),
                authentication, encryption);
    }

    private LicenseParameters chainedParameters(
            LicenseConsumerManager parent,
            LicenseInitialization initialization,
            Authentication authentication,
            @CheckForNull Encryption encryption) {
        return parameters(initialization, authentication,
                resolveEncryption(parent, encryption));
    }

    private static Encryption resolveEncryption(
            LicenseConsumerManager parent,
            @CheckForNull Encryption encryption) {
        return null != encryption ? encryption : parent.parameters().encryption();
    }

    private LicenseParameters parameters(
            final LicenseInitialization initialization,
            final Authentication authentication,
            final Encryption encryption) {
        final LicenseManagementContext c = context();
        return parameters(c.authorization(), initialization, c.validation(),
                c, authentication, c.codec(), c.compression(), encryption);
    }

    private static LicenseParameters parameters(
            final LicenseAuthorization authorization,
            final LicenseInitialization initialization,
            final LicenseValidation validation,
            final RepositoryProvider rp,
            final Authentication authentication,
            final Codec codec,
            final Transformation compression,
            final Encryption encryption) {
        requireNonNull(authorization);
        requireNonNull(initialization);
        requireNonNull(validation);
        requireNonNull(authentication);
        requireNonNull(codec);
        requireNonNull(compression);
        requireNonNull(encryption);
        return new LicenseParameters() {
            @Override
            public LicenseAuthorization authorization() { return authorization; }

            @Override
            public LicenseInitialization initialization() { return initialization; }

            @Override
            public LicenseValidation validation() { return validation; }

            @Override
            public Repository repository() { return rp.repository(); }

            @Override
            public Authentication authentication() { return authentication; }

            @Override
            public Codec codec() { return codec; }

            @Override
            public Transformation compression() { return compression; }

            @Override
            public Encryption encryption() { return encryption; }
        };
    }

    final AuthenticationParameters apChecked(
            final @CheckForNull Source source,
            final @CheckForNull String storeType,
            final PasswordProtection storeProtection,
            final String alias,
            final @CheckForNull PasswordProtection keyProtection) {
        final AuthenticationParameters unchecked =
                apUnchecked(source, storeType, storeProtection, alias, keyProtection);
        return new AuthenticationParameters() {
            @Override
            public Source source() {
                return unchecked.source();
            }

            @Override
            public String storeType() {
                return unchecked.storeType();
            }

            @Override
            public PasswordProtection storeProtection() {
                return checkedProtection(unchecked.storeProtection());
            }

            @Override
            public String alias() {
                return unchecked.alias();
            }

            @Override
            public PasswordProtection keyProtection() {
                return checkedProtection(unchecked.keyProtection());
            }
        };
    }

    final AuthenticationParameters apUnchecked(
            final @CheckForNull Source source,
            final @CheckForNull String storeType,
            final PasswordProtection storeProtection,
            final String alias,
            final @CheckForNull PasswordProtection keyProtection) {
        requireNonNull(storeProtection);
        requireNonNull(alias);
        return new AuthenticationParameters() {

            @Override
            public @CheckForNull Source source() { return source; }

            @Override
            public String storeType() {
                return null != storeType ? storeType : context().storeType();
            }

            @Override
            public PasswordProtection storeProtection() {
                return storeProtection;
            }

            @Override
            public String alias() { return alias; }

            @Override
            public PasswordProtection keyProtection() {
                return null != keyProtection ? keyProtection : storeProtection();
            }
        };
    }

    final PbeParameters pbeChecked(
            final @CheckForNull String algorithm,
            final PasswordProtection protection) {
        return new PbeParameters() {
            final PbeParameters unchecked = pbeUnchecked(algorithm, protection);

            @Override
            public String algorithm() { return unchecked.algorithm(); }

            @Override
            public PasswordProtection protection() throws Exception {
                return checkedProtection(unchecked.protection());
            }
        };
    }

    final PbeParameters pbeUnchecked(
            final @CheckForNull String algorithm,
            final PasswordProtection protection) {
        requireNonNull(protection);
        return new PbeParameters() {
            final String resolvedAlgorithm = resolvePbeAlgorithm(algorithm);

            @Override
            public String algorithm() { return resolvedAlgorithm; }

            @Override
            public PasswordProtection protection() { return protection; }
        };
    }

    private String resolvePbeAlgorithm(@CheckForNull String algorithm) {
        return null != algorithm ? algorithm : context().pbeAlgorithm();
    }

    private PasswordProtection checkedProtection(final PasswordProtection protection) {
        return new PasswordProtection() {
            @Override public Password password() throws Exception {
                policy().check(protection);
                return protection.password();
            }
        };
    }

    @Override
    public Source resource(String name) {
        return Sources.forResource(name, classLoader());
    }

    @Override
    public Store systemNodeStore(Class<?> classInPackage) {
        return new PreferencesStore(
                Preferences.systemNodeForPackage(classInPackage), subject());
    }

    @Override
    public Store userNodeStore(Class<?> classInPackage) {
        return new PreferencesStore(
                Preferences.userNodeForPackage(classInPackage), subject());
    }

    @Override
    public Store pathStore(Path path) { return new PathStore(path); }

    @Override
    public Store memoryStore() { return new MemoryStore(); }

    @Override
    public Source input() { return Sources.input(); }

    @Override
    public Sink output() { return Sinks.output(); }
}
