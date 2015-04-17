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
import org.truelicense.api.misc.Builder;
import org.truelicense.api.misc.ClassLoaderProvider;
import org.truelicense.api.misc.Clock;
import org.truelicense.api.misc.ContextProvider;
import org.truelicense.api.passwd.*;
import org.truelicense.spi.misc.Option;

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
            List<Encryption> encryption,
            LicenseConsumerManager parent) {
        return parameters(authentication, initialization(), encryption,
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
            final List<Encryption> encryption,
            final LicenseConsumerManager parent) {
        for (Encryption e : encryption)
            return e;
        return parent.parameters().encryption();
    }

    final LicenseParameters ftpParameters(
            final Authentication authentication,
            final int days,
            final List<Encryption> encryption,
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
        return parameters(authentication, initialization, encryption, parent);
    }

    @Override
    public LicenseInitialization initialization() {
        return context().initialization();
    }

    private KeyStoreParameters keyStoreParameters(
            final List<String> algorithm,
            final String alias,
            final List<PasswordSpecification> keyPassword,
            final List<Source> source,
            final PasswordSpecification storePassword,
            final List<String> storeType) {
        return new KeyStoreParameters() {

            @Override
            public String alias() { return alias; }

            @Override
            public PasswordProtection keyProtection() {
                for (PasswordSpecification kp : keyPassword)
                    return checkedProtection(kp);
                return checkedProtection(storePassword);
            }

            @Override
            public List<String> algorithm() { return algorithm; }

            @Override
            public List<Source> source() { return source; }

            @Override
            public PasswordProtection storeProtection() {
                return checkedProtection(storePassword);
            }

            @Override
            public String storeType() {
                for (String st : storeType)
                    return st;
                return context().storeType();
            }
        };
    }

    final Authentication ksba(
            List<String> algorithm,
            String alias,
            List<PasswordSpecification> keyPassword,
            List<Source> source,
            List<String> storeType,
            PasswordSpecification storePassword) {
        return context().authentication(keyStoreParameters(
                algorithm, alias, keyPassword,
                source, storePassword, storeType));
    }

    @Override
    public final Store memoryStore() { return bios().memoryStore(); }

    @Override
    public final Date now() { return context().now(); }

    @Override
    public final List<ClassLoader> classLoader() {
        return context().classLoader();
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
            List<Encryption> encryption,
            LicenseConsumerManager parent) {
        return parameters(authentication,
                encryption(encryption, parent), initialization);
    }

    @Override
    public final Store pathStore(Path path) { return bios().pathStore(path); }

    final Encryption pbe(
            List<String> algorithm,
            PasswordSpecification password) {
        return context().encryption(pbeParameters(algorithm, password));
    }

    private PbeParameters pbeParameters(
            final List<String> algorithm,
            final PasswordSpecification password) {
        return new PbeParameters() {

            @Override
            public String algorithm() {
                for (String a : algorithm)
                    return a;
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
        return bios().resource(name, classLoader());
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

    @SuppressWarnings("unchecked")
    abstract class BasicLicenseManagerBuilder<This extends BasicLicenseManagerBuilder<This>> {

        List<Authentication> authentication = Option.none();
        List<Encryption> encryption = Option.none();

        public This authentication(final Authentication authentication) {
            this.authentication = Option.wrap(authentication);
            return (This) this;
        }

        public PbeBuilder encryption() { return new PbeBuilder(); }

        public This encryption(final Encryption encryption) {
            this.encryption = Option.wrap(encryption);
            return (This) this;
        }

        public KsbaBuilder keyStore() { return new KsbaBuilder(); }

        final class KsbaBuilder
                implements Builder<Authentication>,
                           KsbaInjection<This,PasswordSpecification> {

            List<String> algorithm = Option.none();
            List<String> alias = Option.none();
            List<PasswordSpecification> keyPassword = Option.none();
            List<Source> source = Option.none();
            List<PasswordSpecification> storePassword = Option.none();
            List<String> storeType = Option.none();

            @Override
            public This inject() { return authentication(build()); }

            @Override
            public final KsbaBuilder algorithm(final String algorithm) {
                this.algorithm = Option.wrap(algorithm);
                return this;
            }

            @Override
            public final KsbaBuilder alias(final String alias) {
                this.alias = Option.wrap(alias);
                return this;
            }

            @Override
            public final Authentication build() {
                return ksba(algorithm, alias.get(0), keyPassword, source, storeType, storePassword.get(0));
            }

            @Override
            public final KsbaBuilder keyPassword(final PasswordSpecification keyPassword) {
                this.keyPassword = Option.wrap(keyPassword);
                return this;
            }

            @Override
            public final KsbaBuilder loadFrom(final Source source) {
                this.source = Option.wrap(source);
                return this;
            }

            @Override
            public final KsbaBuilder loadFromResource(String name) {
                return loadFrom(resource(name));
            }

            @Override
            public final KsbaBuilder storePassword(final PasswordSpecification storePassword) {
                this.storePassword = Option.wrap(storePassword);
                return this;
            }

            @Override
            public final KsbaBuilder storeType(final String storeType) {
                this.storeType = Option.wrap(storeType);
                return this;
            }
        }

        final class PbeBuilder
                implements Builder<Encryption>,
                           PbeInjection<This,PasswordSpecification> {

            List<String> algorithm = Option.none();
            List<PasswordSpecification> password = Option.none();

            @Override
            public This inject() { return encryption(build()); }

            @Override
            public final PbeBuilder algorithm(final String algorithm) {
                this.algorithm = Option.wrap(algorithm);
                return this;
            }

            @Override
            public final Encryption build() {
                return pbe(algorithm, password.get(0));
            }

            @Override
            public final PbeBuilder password(final PasswordSpecification password) {
                this.password = Option.wrap(password);
                return this;
            }
        }
    }
}
