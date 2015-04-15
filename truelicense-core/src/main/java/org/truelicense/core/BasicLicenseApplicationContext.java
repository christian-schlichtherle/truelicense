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
import org.truelicense.api.io.*;
import org.truelicense.api.misc.ClassLoaderProvider;
import org.truelicense.api.misc.Clock;
import org.truelicense.api.misc.ContextProvider;
import org.truelicense.api.passwd.*;

import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.util.Calendar.DATE;
import static java.util.Calendar.getInstance;

/**
 * A basic context for license applications.
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
@SuppressWarnings("LoopStatementThatDoesntLoop")
abstract class BasicLicenseApplicationContext<PasswordSpecification>
implements ClassLoaderProvider,
           Clock,
           ContextProvider<LicenseManagementContext<PasswordSpecification>>,
        BiosProvider,
           LicenseApplicationContext,
           LicenseSubjectProvider,
           PasswordPolicyProvider,
           PasswordProtectionProvider<PasswordSpecification> {

    private final LicenseManagementContext<PasswordSpecification> context;

    BasicLicenseApplicationContext(final LicenseManagementContext<PasswordSpecification> context) {
        this.context = context;
    }

    @Override
    public final String subject() { return context().subject(); }

    @Override
    public final LicenseManagementContext<PasswordSpecification> context() { return context; }

    @Override
    public final PasswordPolicy policy() { return context().policy(); }

    @Override
    public final PasswordProtection protection(PasswordSpecification specification) {
        return context().protection(specification);
    }

    @Override
    public final Date now() { return context().now(); }

    @Override
    public final List<ClassLoader> optionalClassLoader() {
        return context().optionalClassLoader();
    }

    final LicenseParameters parameters(
            Authentication authentication,
            Encryption encryption) {
        return parameters(context().initialization(), authentication, encryption);
    }

    final LicenseParameters ftpParameters(
            final LicenseConsumerManager parent,
            final Authentication authentication,
            final List<Encryption> optionalEncryption,
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
        return chainedParameters(parent, initialization, authentication, optionalEncryption);
    }

    final LicenseParameters chainedParameters(
            final LicenseConsumerManager parent,
            final Authentication authentication,
            final List<Encryption> optionalEncryption) {
        return chainedParameters(parent, context().initialization(),
                authentication, optionalEncryption);
    }

    private LicenseParameters chainedParameters(
            LicenseConsumerManager parent,
            LicenseInitialization initialization,
            Authentication authentication,
            List<Encryption> optionalEncryption) {
        return parameters(initialization, authentication,
                resolveEncryption(parent, optionalEncryption));
    }

    private static Encryption resolveEncryption(
            LicenseConsumerManager parent,
            List<Encryption> optionalEncryption) {
        for (Encryption encryption : optionalEncryption)
            return encryption;
        return parent.parameters().encryption();
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

    final Authentication keyStore(
            List<Source> optionalSource,
            List<String> optionalStoreType,
            PasswordSpecification storePassword,
            String alias,
            List<PasswordSpecification> optionalKeyPassword) {
        return context().authentication(keyStoreParameters(
                optionalSource, optionalStoreType, storePassword, alias, optionalKeyPassword));
    }

    final AuthenticationParameters keyStoreParameters(
            final List<Source> optionalSource,
            final List<String> optionalStoreType,
            final PasswordSpecification storePassword,
            final String alias,
            final List<PasswordSpecification> optionalKeyPassword) {
        return new AuthenticationParameters() {

            final PasswordProtection checkedStoreProtection = checkedProtection(storePassword);
            final PasswordProtection checkedKeyProtection;

            {
                PasswordProtection pp = checkedStoreProtection;
                for (PasswordSpecification ps : optionalKeyPassword) {
                    pp = checkedProtection(ps);
                    break;
                }
                checkedKeyProtection = pp;
            }

            @Override
            public List<Source> optionalSource() { return optionalSource; }

            @Override
            public String storeType() {
                for (String storeType : optionalStoreType)
                    return storeType;
                return context().storeType();
            }

            @Override
            public PasswordProtection storeProtection() {
                return checkedStoreProtection;
            }

            @Override
            public String alias() { return alias; }

            @Override
            public PasswordProtection keyProtection() {
                return checkedKeyProtection;
            }
        };
    }

    final Encryption pbe(
            List<String> optionalAlgorithm,
            PasswordSpecification password) {
        return context().encryption(pbeParameters(optionalAlgorithm, password));
    }

    final PbeParameters pbeParameters(
            final List<String> optionalAlgorithm,
            final PasswordSpecification password) {
        return new PbeParameters() {

            final String pbeAlgorithm = pbeAlgorithm(optionalAlgorithm);
            final PasswordProtection checkedProtection = checkedProtection(password);

            @Override
            public String algorithm() { return pbeAlgorithm; }

            @Override
            public PasswordProtection protection() { return checkedProtection; }
        };
    }

    private String pbeAlgorithm(List<String> optionalAlgorithm) {
        for (String algorithm : optionalAlgorithm)
            return algorithm;
        return context().pbeAlgorithm();
    }

    private PasswordProtection checkedProtection(final PasswordSpecification password) {
        return new PasswordProtection() {

            final PasswordProtection protection = protection(password);

            @Override
            public Password password(final PasswordUsage usage) throws Exception {
                if (usage.equals(PasswordUsage.WRITE)) // check null
                    policy().check(protection);
                return protection.password(usage);
            }
        };
    }

    @Override
    public final Store memoryStore() { return bios().memoryStore(); }

    @Override
    public final Store pathStore(Path path) { return bios().pathStore(path); }

    @Override
    public final Source resource(String name) {
        return bios().resource(name, optionalClassLoader());
    }

    @Override
    public final Source stdin() {
        return bios().stdin();
    }

    @Override
    public final Sink stdout() {
        return bios().stdout();
    }

    @Override
    public final Store systemPreferencesStore(Class<?> classInPackage) {
        return bios().systemPreferencesStore(classInPackage, subject());
    }

    @Override
    public final Store userPreferencesStore(Class<?> classInPackage) {
        return bios().userPreferencesStore(classInPackage, subject());
    }

    @Override
    public final BIOS bios() { return context().bios(); }
}
