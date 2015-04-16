/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.api.*;
import org.truelicense.api.auth.Authentication;
import org.truelicense.api.auth.KeyStoreParameters;
import org.truelicense.api.auth.Repository;
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
implements BiosProvider,
           ClassLoaderProvider,
           Clock,
           ContextProvider<LicenseManagementContext<PasswordSpecification>>,
           LicenseApplicationContext,
           LicenseInitializationProvider,
           LicenseSubjectProvider,
           PasswordPolicyProvider,
           PasswordProtectionProvider<PasswordSpecification> {

    private final LicenseManagementContext<PasswordSpecification> context;

    BasicLicenseApplicationContext(final LicenseManagementContext<PasswordSpecification> context) {
        this.context = context;
    }

    @Override
    public final BIOS bios() { return context().bios(); }

    final LicenseParameters chainedParameters(
            Authentication authentication,
            List<Encryption> optionalEncryption,
            LicenseConsumerManager parent) {
        return parameters(authentication, initialization(), optionalEncryption,
                parent);
    }

    private PasswordProtection checkedProtection(
            final PasswordSpecification password) {
        return new PasswordProtection() {

            @Override
            public Password password(final PasswordUsage usage) throws Exception {
                final PasswordProtection protection = protection(password);
                if (usage.equals(PasswordUsage.WRITE)) // check null
                    policy().check(protection);
                return protection.password(usage);
            }
        };
    }

    @Override
    public final LicenseManagementContext<PasswordSpecification> context() {
        return context;
    }

    private static Encryption encryption(
            final List<Encryption> optionalEncryption,
            final LicenseConsumerManager parent) {
        for (Encryption encryption : optionalEncryption)
            return encryption;
        return parent.parameters().encryption();
    }

    final LicenseParameters ftpParameters(
            final Authentication authentication,
            final int days,
            final List<Encryption> optionalEncryption,
            final LicenseConsumerManager parent) {
        if (0 >= days) throw new IllegalArgumentException();
        final LicenseInitialization initialization = new LicenseInitialization() {

            @Override public void initialize(final License bean) {
                initialization().initialize(bean);
                final Calendar cal = getInstance();
                cal.setTime(bean.getIssued());
                bean.setNotBefore(cal.getTime()); // not before issued
                cal.add(DATE, days); // FTP countdown starts NOW
                bean.setNotAfter(cal.getTime());
            }
        };
        return parameters(authentication, initialization, optionalEncryption,
                parent);
    }

    @Override
    public LicenseInitialization initialization() {
        return context().initialization();
    }

    final Authentication keyStoreAuthentication(
            String alias,
            List<String> optionalAlgorithm,
            List<PasswordSpecification> optionalKeyPassword,
            List<Source> optionalSource,
            List<String> optionalStoreType,
            PasswordSpecification storePassword) {
        return context().authentication(keyStoreParameters(
                alias, optionalAlgorithm, optionalKeyPassword,
                optionalSource, optionalStoreType, storePassword));
    }

    private KeyStoreParameters keyStoreParameters(
            final String alias,
            final List<String> optionalAlgorithm,
            final List<PasswordSpecification> optionalKeyPassword,
            final List<Source> optionalSource,
            final List<String> optionalStoreType,
            final PasswordSpecification storePassword) {
        return new KeyStoreParameters() {

            @Override
            public String alias() { return alias; }

            @Override
            public PasswordProtection keyProtection() {
                for (PasswordSpecification keyPassword : optionalKeyPassword)
                    return checkedProtection(keyPassword);
                return checkedProtection(storePassword);
            }

            @Override
            public List<String> optionalAlgorithm() {
                return optionalAlgorithm;
            }

            @Override
            public List<Source> optionalSource() { return optionalSource; }

            @Override
            public PasswordProtection storeProtection() {
                return checkedProtection(storePassword);
            }

            @Override
            public String storeType() {
                for (String storeType : optionalStoreType)
                    return storeType;
                return context().storeType();
            }
        };
    }

    @Override
    public final Store memoryStore() { return bios().memoryStore(); }

    @Override
    public final Date now() { return context().now(); }

    @Override
    public final List<ClassLoader> optionalClassLoader() {
        return context().optionalClassLoader();
    }

    final LicenseParameters parameters(
            Authentication authentication,
            Encryption encryption) {
        return parameters(authentication, encryption, initialization());
    }

    private LicenseParameters parameters(
            final Authentication authentication,
            final Encryption encryption,
            final LicenseInitialization initialization) {
        return new LicenseParameters() {

            @Override
            public Authentication authentication() { return authentication; }

            @Override
            public LicenseAuthorization authorization() { return context().authorization(); }

            @Override
            public Codec codec() { return context().codec(); }

            @Override
            public Transformation compression() { return context().compression(); }

            @Override
            public Encryption encryption() { return encryption; }

            @Override
            public LicenseInitialization initialization() { return initialization; }

            @Override
            public Repository repository() { return context().repository(); }

            @Override
            public LicenseValidation validation() { return context().validation(); }
        };
    }

    private LicenseParameters parameters(
            Authentication authentication,
            LicenseInitialization initialization,
            List<Encryption> optionalEncryption,
            LicenseConsumerManager parent) {
        return parameters(authentication,
                encryption(optionalEncryption, parent), initialization);
    }

    final Encryption passwordBasedEncryption(
            List<String> optionalAlgorithm,
            PasswordSpecification password) {
        return context().encryption(pbeParameters(optionalAlgorithm, password));
    }

    @Override
    public final Store pathStore(Path path) { return bios().pathStore(path); }

    private PbeParameters pbeParameters(
            final List<String> optionalAlgorithm,
            final PasswordSpecification password) {
        return new PbeParameters() {

            @Override
            public String algorithm() {
                for (String algorithm : optionalAlgorithm)
                    return algorithm;
                return context().pbeAlgorithm();
            }

            @Override
            public PasswordProtection protection() {
                return checkedProtection(password);
            }
        };
    }

    @Override
    public final PasswordPolicy policy() { return context().policy(); }

    @Override
    public final PasswordProtection protection(
            PasswordSpecification specification) {
        return context().protection(specification);
    }

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
    public final String subject() { return context().subject(); }

    @Override
    public final Store systemPreferencesStore(Class<?> classInPackage) {
        return bios().systemPreferencesStore(classInPackage, subject());
    }

    @Override
    public final Store userPreferencesStore(Class<?> classInPackage) {
        return bios().userPreferencesStore(classInPackage, subject());
    }
}
