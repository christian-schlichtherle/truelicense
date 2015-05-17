/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.api.*;
import org.truelicense.api.auth.Authentication;
import org.truelicense.api.auth.KeyStoreParameters;
import org.truelicense.api.auth.RepositoryContext;
import org.truelicense.api.auth.RepositoryContextProvider;
import org.truelicense.api.codec.Codec;
import org.truelicense.api.codec.CodecProvider;
import org.truelicense.api.comp.CompressionProvider;
import org.truelicense.api.crypto.PbeParameters;
import org.truelicense.api.io.*;
import org.truelicense.api.misc.*;
import org.truelicense.api.passwd.*;
import org.truelicense.core.auth.Notary;
import org.truelicense.core.passwd.Passwords;
import org.truelicense.obfuscate.ObfuscatedString;
import org.truelicense.spi.io.StandardBIOS;
import org.truelicense.spi.misc.Option;

import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static java.util.Calendar.DATE;
import static java.util.Calendar.getInstance;

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
 * @param <Model> the type of the repository model.
 * @author Christian Schlichtherle
 */
@SuppressWarnings("LoopStatementThatDoesntLoop")
public abstract class TrueLicenseManagementContext<Model>
implements BiosProvider,
        CachePeriodProvider,
        Clock,
        CodecProvider,
        CompressionProvider,
        LicenseManagementAuthorizationProvider,
        LicenseFactory,
        LicenseInitializationProvider,
        LicenseManagementContext,
        LicenseSubjectProvider,
        LicenseValidationProvider,
        PasswordPolicyProvider,
        PasswordProtectionProvider<ObfuscatedString>,
        RepositoryContextProvider<Model> {

    private final String subject;

    protected TrueLicenseManagementContext(final String subject) {
        this.subject = Objects.requireNonNull(subject);
    }

    /**
     * Returns an authentication for the given key store parameters.
     * <p>
     * The implementation in the class {@link TrueLicenseManagementContext}
     * returns a new {@link Notary} for the given key store parameters.
     *
     * @param parameters the key store parameters.
     */
    public Authentication authentication(KeyStoreParameters parameters) {
        return new Notary(parameters);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link TrueLicenseManagementContext}
     * returns an authorization which clears all operation requests.
     */
    @Override
    public LicenseManagementAuthorization authorization() {
        return new TrueLicenseManagementAuthorization();
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link TrueLicenseManagementContext}
     * returns a {@link StandardBIOS}.
     */
    @Override
    public BIOS bios() { return new StandardBIOS(); }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link TrueLicenseManagementContext}
     * returns half an hour (in milliseconds) to account for external changes
     * to the configured store for the license key.
     */
    @Override
    public long cachePeriodMillis() { return 30 * 60 * 1000; }

    final PasswordProtection checkedProtection(
            final PasswordProtection password) {
        return new PasswordProtection() {

            @Override
            public Password password(final PasswordUsage usage) throws Exception {
                if (usage.equals(PasswordUsage.WRITE)) // check null
                    policy().check(password);
                return password.password(usage);
            }
        };
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link TrueLicenseManagementContext}
     * lists the current thread's context class loader, if not {@code null}.
     */
    @Override
    public List<ClassLoader> classLoader() {
        return Option.wrap(Thread.currentThread().getContextClassLoader());
    }

    @Override
    public ConsumerLicenseManagerBuilder consumer() {
        return new ConsumerTrueLicenseManagerBuilder();
    }

    /**
     * Returns an encryption for the given PBE parameters.
     *
     * @param parameters the PBE parameters.
     */
    public abstract Transformation encryption(PbeParameters parameters);

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link TrueLicenseManagementContext}
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
        return new TrueLicenseInitialization(this);
    }

    final Authentication ksba(
            List<String> algorithm,
            String alias,
            List<PasswordProtection> keyPassword,
            List<Source> source,
            List<String> storeType,
            PasswordProtection storePassword) {
        return authentication(ksbaParameters(
                algorithm, alias, keyPassword,
                source, storePassword, storeType));
    }

    private KeyStoreParameters ksbaParameters(
            final List<String> algorithm,
            final String alias,
            final List<PasswordProtection> keyPassword,
            final List<Source> source,
            final PasswordProtection storePassword,
            final List<String> storeType) {
        return new KeyStoreParameters() {

            @Override
            public String alias() { return alias; }

            final TrueLicenseManagementContext<Model> context() {
                return TrueLicenseManagementContext.this;
            }

            @Override
            public PasswordProtection keyProtection() {
                for (PasswordProtection kp : keyPassword)
                    return context().checkedProtection(kp);
                return context().checkedProtection(storePassword);
            }

            @Override
            public List<String> algorithm() { return algorithm; }

            @Override
            public List<Source> source() { return source; }

            @Override
            public PasswordProtection storeProtection() {
                return context().checkedProtection(storePassword);
            }

            @Override
            public String storeType() {
                for (String st : storeType)
                    return st;
                return context().storeType();
            }
        };
    }

    @Override
    public final Store memoryStore() { return bios().memoryStore(); }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link TrueLicenseManagementContext}
     * returns a new {@link Date}.
     */
    @Override
    public Date now() { return new Date(); }

    @Override
    public final Store pathStore(Path path) { return bios().pathStore(path); }

    final Transformation pbe(
            List<String> algorithm,
            PasswordProtection password) {
        return encryption(pbeParameters(algorithm, password));
    }

    /**
     * Returns the name of the default Password Based Encryption (PBE)
     * algorithm for the license key format.
     * You can override this default value when configuring the PBE.
     *
     * @see PbeInjection#algorithm
     */
    public abstract String pbeAlgorithm();

    private PbeParameters pbeParameters(
            final List<String> algorithm,
            final PasswordProtection password) {
        return new PbeParameters() {

            @Override
            public String algorithm() {
                for (String a : algorithm)
                    return a;
                return context().pbeAlgorithm();
            }

            final TrueLicenseManagementContext<Model> context() {
                return TrueLicenseManagementContext.this;
            }

            @Override
            public PasswordProtection protection() {
                return context().checkedProtection(password);
            }
        };
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link TrueLicenseManagementContext}
     * returns {@link Passwords#newPasswordPolicy()}.
     */
    @Override
    public PasswordPolicy policy() { return Passwords.newPasswordPolicy(); }

    /**
     * Returns a password protection for the given representation of an
     * obfuscated string.
     * Calling this method creates a new {@link ObfuscatedString} from the given
     * array and forwards the call to {@link #protection(ObfuscatedString)}.
     */
    public final PasswordProtection protection(long[] obfuscated) {
        return protection(new ObfuscatedString(obfuscated));
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link TrueLicenseManagementContext}
     * returns
     * {@link Passwords#newPasswordProtection(ObfuscatedString) Passwords.newPasswordProtecetion(os)}.
     */
    @Override
    public final PasswordProtection protection(ObfuscatedString os) {
        return Passwords.newPasswordProtection(os);
    }

    @Override
    public final Source resource(String name) {
        return bios().resource(name, classLoader());
    }

    @Override
    public final Source stdin() { return bios().stdin(); }

    @Override
    public final Sink stdout() { return bios().stdout(); }

    /**
     * Returns the name of the default key store type,
     * for example {@code "JCEKS"} or {@code "JKS"}.
     * You can override this default value when configuring the key store based
     * authentication.
     */
    public abstract String storeType();

    /** Returns the licensing subject. */
    @Override
    public final String subject() { return subject; }

    @Override
    public final Store systemPreferencesStore(Class<?> classInPackage) {
        return bios().systemPreferencesStore(classInPackage, subject());
    }

    @Override
    public final Store userPreferencesStore(Class<?> classInPackage) {
        return bios().userPreferencesStore(classInPackage, subject());
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link TrueLicenseManagementContext}
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
        return new TrueLicenseValidation(this);
    }

    @Override
    public VendorLicenseManagerBuilder vendor() {
        return new VendorTrueLicenseManagerBuilder();
    }

    class ConsumerTrueLicenseManagerBuilder
    extends TrueLicenseManagerBuilder<ConsumerTrueLicenseManagerBuilder>
    implements ConsumerLicenseManagerBuilder {

        @Override
        public ConsumerLicenseManager build() {
            final TrueLicenseManagementParameters
                    lp = new TrueLicenseManagementParameters(this);
            return parent.isEmpty()
                    ? new CachingTrueLicenseManager<>(lp)
                    : new ChainedTrueLicenseManager<>(lp);
        }

        @Override
        public ConsumerTrueLicenseManagerBuilder inject() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ParentConsumerTrueLicenseManagerBuilder parent() {
            return new ParentConsumerTrueLicenseManagerBuilder();
        }

        final class ParentConsumerTrueLicenseManagerBuilder
        extends ConsumerTrueLicenseManagerBuilder {

            @Override
            public ConsumerTrueLicenseManagerBuilder inject() {
                return ConsumerTrueLicenseManagerBuilder.this.parent(build());
            }
        }
    }

    final class VendorTrueLicenseManagerBuilder
    extends TrueLicenseManagerBuilder<VendorTrueLicenseManagerBuilder>
    implements VendorLicenseManagerBuilder {

        @Override
        public VendorLicenseManager build() {
            return new TrueLicenseManager<>(
                    new TrueLicenseManagementParameters(this));
        }
    }

    @SuppressWarnings("unchecked")
    abstract class TrueLicenseManagerBuilder<This extends TrueLicenseManagerBuilder<This>>
    implements ContextProvider<TrueLicenseManagementContext<Model>> {

        List<Authentication> authentication = Option.none();
        List<Transformation> encryption = Option.none();
        int ftpDays;
        List<ConsumerLicenseManager> parent = Option.none();
        List<Store> store = Option.none();

        public final This authentication(final Authentication authentication) {
            this.authentication = Option.wrap(authentication);
            return (This) this;
        }

        @Override
        public final TrueLicenseManagementContext<Model> context() {
            return TrueLicenseManagementContext.this;
        }

        public final PbeBuilder encryption() { return new PbeBuilder(); }

        public final This encryption(final Transformation encryption) {
            this.encryption = Option.wrap(encryption);
            return (This) this;
        }

        public final This ftpDays(final int ftpDays) {
            this.ftpDays = ftpDays;
            return (This) this;
        }

        public final KsbaBuilder keyStore() { return new KsbaBuilder(); }

        public final This parent(final ConsumerLicenseManager parent) {
            this.parent = Option.wrap(parent);
            return (This) this;
        }

        public final This storeIn(final Store store) {
            this.store = Option.wrap(store);
            return (This) this;
        }

        public final This storeInPath(Path path) {
            return storeIn(context().pathStore(path));
        }

        public final This storeInSystemPreferences(Class<?> classInPackage) {
            return storeIn(context().systemPreferencesStore(classInPackage));
        }

        public final This storeInUserPreferences(Class<?> classInPackage) {
            return storeIn(context().userPreferencesStore(classInPackage));
        }

        final class KsbaBuilder
        implements Builder<Authentication>,
                KsbaInjection<This> {

            List<String> algorithm = Option.none();
            List<String> alias = Option.none();
            List<PasswordProtection> keyProtection = Option.none();
            List<Source> source = Option.none();
            List<PasswordProtection> storeProtection = Option.none();
            List<String> storeType = Option.none();

            @Override
            public This inject() { return authentication(build()); }

            @Override
            public KsbaBuilder algorithm(final String algorithm) {
                this.algorithm = Option.wrap(algorithm);
                return this;
            }

            @Override
            public KsbaBuilder alias(final String alias) {
                this.alias = Option.wrap(alias);
                return this;
            }

            @Override
            public Authentication build() {
                return ksba(algorithm, alias.get(0), keyProtection, source, storeType, storeProtection.get(0));
            }

            @Override
            public KsbaBuilder keyProtection(final PasswordProtection keyProtection) {
                this.keyProtection = Option.wrap(keyProtection);
                return this;
            }

            @Override
            public KsbaBuilder loadFrom(final Source source) {
                this.source = Option.wrap(source);
                return this;
            }

            @Override
            public KsbaBuilder loadFromResource(String name) {
                return loadFrom(resource(name));
            }

            @Override
            public KsbaBuilder storeProtection(final PasswordProtection storeProtection) {
                this.storeProtection = Option.wrap(storeProtection);
                return this;
            }

            @Override
            public KsbaBuilder storeType(final String storeType) {
                this.storeType = Option.wrap(storeType);
                return this;
            }
        }

        final class PbeBuilder
        implements Builder<Transformation>,
                PbeInjection<This> {

            List<String> algorithm = Option.none();
            List<PasswordProtection> protection = Option.none();

            @Override
            public This inject() { return encryption(build()); }

            @Override
            public PbeBuilder algorithm(final String algorithm) {
                this.algorithm = Option.wrap(algorithm);
                return this;
            }

            @Override
            public Transformation build() {
                return pbe(algorithm, protection.get(0));
            }

            @Override
            public PbeBuilder protection(final PasswordProtection protection) {
                this.protection = Option.wrap(protection);
                return this;
            }
        }
    }

    final class TrueLicenseManagementParameters
            implements
            BiosProvider,
            CachePeriodProvider,
            CodecProvider,
            CompressionProvider,
            ContextProvider<TrueLicenseManagementContext<Model>>,
            LicenseManagementAuthorizationProvider,
            LicenseFactory,
            LicenseInitializationProvider,
            LicenseManagementParameters,
            LicenseValidationProvider,
            RepositoryContextProvider<Model> {

        final List<Authentication> authentication;
        final List<Transformation> encryption;
        final int ftpDays;
        final List<ConsumerLicenseManager> parent;
        final List<Store> store;

        TrueLicenseManagementParameters(final TrueLicenseManagerBuilder<?> bldr) {
            this.authentication = bldr.authentication;
            this.encryption = bldr.encryption;
            this.ftpDays = bldr.ftpDays;
            this.parent = bldr.parent;
            this.store = bldr.store;
        }

        @Override
        public Authentication authentication() { return authentication.get(0); }

        @Override
        public final LicenseManagementAuthorization authorization() { return context().authorization(); }

        @Override
        public final BIOS bios() { return context().bios(); }

        @Override
        public long cachePeriodMillis() { return context().cachePeriodMillis(); }

        @Override
        public final Codec codec() { return context().codec(); }

        @Override
        public final Transformation compression() { return context().compression(); }

        @Override
        public TrueLicenseManagementContext<Model> context() {
            return TrueLicenseManagementContext.this;
        }

        @Override
        public Transformation encryption() {
            for (Transformation e : encryption)
                return e;
            return parent().parameters().encryption();
        }

        @Override
        public LicenseInitialization initialization() {
            final LicenseInitialization initialization = context().initialization();
            if (0 == ftpDays)
                return initialization;

            return new LicenseInitialization() {

                @Override public void initialize(final License bean) {
                    initialization.initialize(bean);
                    final Calendar cal = getInstance();
                    cal.setTime(bean.getIssued());
                    bean.setNotBefore(cal.getTime()); // not before issued
                    cal.add(DATE, ftpDays); // FTP countdown starts NOW
                    bean.setNotAfter(cal.getTime());
                }
            };
        }

        @Override
        public License license() { return context().license(); }

        public ConsumerLicenseManager parent() { return parent.get(0); }

        @Override
        public final RepositoryContext<Model> repositoryContext() {
            return context().repositoryContext();
        }

        public Store store() { return store.get(0);}

        @Override
        public final LicenseValidation validation() { return context().validation(); }
    }
}
