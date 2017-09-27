/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.core;

import net.truelicense.api.*;
import net.truelicense.api.auth.*;
import net.truelicense.api.codec.Codec;
import net.truelicense.api.codec.CodecProvider;
import net.truelicense.api.codec.Decoder;
import net.truelicense.api.comp.CompressionProvider;
import net.truelicense.api.crypto.EncryptionParameters;
import net.truelicense.api.io.Sink;
import net.truelicense.api.io.Source;
import net.truelicense.api.io.Store;
import net.truelicense.api.io.Transformation;
import net.truelicense.api.misc.*;
import net.truelicense.api.passwd.*;
import net.truelicense.core.auth.Notary;
import net.truelicense.core.passwd.MinimumPasswordPolicy;
import net.truelicense.core.passwd.ObfuscatedPasswordProtection;
import net.truelicense.obfuscate.Obfuscate;
import net.truelicense.obfuscate.ObfuscatedString;
import net.truelicense.spi.codec.Codecs;
import net.truelicense.spi.io.BIOS;
import net.truelicense.spi.io.BiosProvider;
import net.truelicense.spi.io.StandardBIOS;
import net.truelicense.spi.io.Transformer;

import javax.security.auth.x500.X500Principal;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.Callable;

import static java.util.Calendar.DATE;
import static java.util.Calendar.getInstance;
import static java.util.Objects.requireNonNull;
import static net.truelicense.core.Messages.*;

/**
 * A basic license application context.
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
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public abstract class TrueLicenseApplicationContext<Model>
implements
        BiosProvider,
        CachePeriodProvider,
        ClassLoaderProvider,
        Clock,
        CodecProvider,
        CompressionProvider,
        LicenseApplicationContext,
        LicenseManagementAuthorizationProvider,
        LicenseFactory,
        PasswordPolicyProvider,
        PasswordProtectionProvider<ObfuscatedString>,
        RepositoryContextProvider<Model> {

    /**
     * Returns an authentication for the given key store parameters.
     * <p>
     * The implementation in the class {@link TrueLicenseApplicationContext}
     * returns a new {@link Notary} for the given key store parameters.
     *
     * @param parameters the key store parameters.
     */
    public Authentication authentication(AuthenticationParameters parameters) {
        return new Notary(parameters);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link TrueLicenseApplicationContext}
     * returns an authorization which clears all operation requests.
     */
    @Override
    public final LicenseManagementAuthorization authorization() {
        return new TrueLicenseManagementAuthorization();
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link TrueLicenseApplicationContext}
     * returns a {@link StandardBIOS}.
     */
    @Override
    public BIOS bios() { return new StandardBIOS(); }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link TrueLicenseApplicationContext}
     * returns half an hour (in milliseconds) to account for external changes
     * to the configured store for the license key.
     */
    @Override
    public long cachePeriodMillis() { return 30 * 60 * 1000; }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link TrueLicenseApplicationContext}
     * lists the current thread's context class loader, if not {@code null}.
     */
    @Override
    public final Optional<ClassLoader> classLoader() {
        return Optional.ofNullable(Thread.currentThread().getContextClassLoader());
    }

    @Override
    public final LicenseManagementContextBuilder context() {
        return new TrueLicenseManagementContextBuilder();
    }

    /**
     * Returns a password based encryption using the given parameters.
     *
     * @param parameters the password based encryption parameters.
     */
    public abstract Transformation encryption(EncryptionParameters parameters);

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link TrueLicenseApplicationContext}
     * returns a new {@link Date}.
     */
    @Override
    public final Date now() {
        return new Date();
    }

    /**
     * Returns the name of the default Password Based Encryption (PBE)
     * algorithm for the license key format.
     * You can override this default value when configuring the PBE.
     *
     * @see EncryptionInjection#algorithm
     */
    public abstract String pbeAlgorithm();

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link TrueLicenseApplicationContext}
     * returns a new {@link MinimumPasswordPolicy}.
     */
    @Override
    public PasswordPolicy policy() { return new MinimumPasswordPolicy(); }

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
     * The implementation in the class {@link TrueLicenseApplicationContext}
     * returns a new {@link ObfuscatedPasswordProtection}.
     */
    @Override
    public final PasswordProtection protection(ObfuscatedString os) {
        return new ObfuscatedPasswordProtection(os);
    }

    /**
     * Returns the name of the default key store type,
     * for example {@code "JCEKS"} or {@code "JKS"}.
     * You can override this default value when configuring the key store based
     * authentication.
     */
    public abstract String storeType();

    //
    // Utility functions:
    //

    private static String requireNonEmpty(final String s) {
        if (s.isEmpty())
            throw new IllegalArgumentException();
        return s;
    }

    private static boolean exists(final Store store) throws LicenseManagementException {
        return wrap(store::exists);
    }

    /**
     * Executes the given {@code task} and wraps any
     * non-{@link RuntimeException} and non-{@link LicenseManagementException}
     * in a new {@code LicenseManagementException}.
     *
     * @param  task the task to {@link Callable#call}.
     * @return the result of calling the task.
     * @throws RuntimeException at the discretion of the task.
     * @throws LicenseManagementException on any other {@link Exception} thrown
     *         by the task.
     */
    private static <V> V wrap(final Callable<V> task) throws LicenseManagementException {
        try { return task.call(); }
        catch (RuntimeException | LicenseManagementException e) { throw e; }
        catch (Exception e) { throw new LicenseManagementException(e); }
    }

    //
    // Inner classes:
    //

    final class CheckedPasswordProtection implements PasswordProtection {

        final PasswordProtection protection;

        CheckedPasswordProtection(final PasswordProtection protection) {
            this.protection = protection;
        }

        @Override
        public Password password(final PasswordUsage usage) throws Exception {
            if (usage.equals(PasswordUsage.WRITE)) // check null
                policy().check(protection);
            return protection.password(usage);
        }
    }

    final class TrueLicenseManagementContextBuilder
    implements
            ContextProvider<TrueLicenseApplicationContext>,
            LicenseManagementContextBuilder {

        LicenseManagementAuthorization authorization = context().authorization();
        ClassLoaderProvider classLoaderProvider = context();
        Clock clock = context();
        Optional<LicenseInitialization> initialization = Optional.empty();
        LicenseFunctionComposition initializationComposition = LicenseFunctionComposition.decorate;
        String subject = "";
        Optional<LicenseValidation> validation = Optional.empty();
        LicenseFunctionComposition validationComposition = LicenseFunctionComposition.decorate;

        @Override
        public LicenseManagementContextBuilder authorization(final LicenseManagementAuthorization authorization) {
            this.authorization = requireNonNull(authorization);
            return this;
        }

        @Override
        public LicenseManagementContextBuilder classLoaderProvider(final ClassLoaderProvider classLoaderProvider) {
            this.classLoaderProvider = requireNonNull(classLoaderProvider);
            return null;
        }

        @Override
        public LicenseManagementContextBuilder clock(final Clock clock) {
            this.clock = requireNonNull(clock);
            return this;
        }

        @Override
        public TrueLicenseApplicationContext context() {
            return TrueLicenseApplicationContext.this;
        }

        @Override
        public LicenseManagementContextBuilder initialization(final LicenseInitialization initialization) {
            this.initialization = Optional.ofNullable(initialization);
            return this;
        }

        @Override
        public LicenseManagementContextBuilder initializationComposition(final LicenseFunctionComposition composition) {
            this.initializationComposition = requireNonNull(composition);
            return this;
        }

        @Override
        public LicenseManagementContextBuilder subject(final String subject) {
            this.subject = requireNonEmpty(subject);
            return this;
        }

        @Override
        public LicenseManagementContextBuilder validation(final LicenseValidation validation) {
            this.validation = Optional.ofNullable(validation);
            return this;
        }

        @Override
        public LicenseManagementContextBuilder validationComposition(final LicenseFunctionComposition composition) {
            this.validationComposition = requireNonNull(composition);
            return this;
        }

        @Override
        public LicenseManagementContext build() {
            return new TrueLicenseManagementContext(this);
        }
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    final class TrueLicenseManagementContext
    implements
            Clock,
            ContextProvider<TrueLicenseApplicationContext>,
            LicenseManagementAuthorizationProvider,
            LicenseInitializationProvider,
            LicenseManagementContext,
            LicenseManagementSubjectProvider,
            LicenseValidationProvider {

        final LicenseManagementAuthorization authorization;
        final ClassLoaderProvider classLoaderProvider;
        final Clock clock;
        final Optional<LicenseInitialization> initialization;
        final LicenseFunctionComposition initializationComposition;
        final String subject;
        final Optional<LicenseValidation> validation;
        final LicenseFunctionComposition validationComposition;

        TrueLicenseManagementContext(final TrueLicenseManagementContextBuilder b) {
            this.authorization = b.authorization;
            this.classLoaderProvider = b.classLoaderProvider;
            this.clock = b.clock;
            this.initialization = b.initialization;
            this.initializationComposition = b.initializationComposition;
            this.subject = requireNonEmpty(b.subject);
            this.validation = b.validation;
            this.validationComposition = b.validationComposition;
        }

        @Override
        public LicenseManagementAuthorization authorization() {
            return authorization;
        }

        @Override
        public Optional<ClassLoader> classLoader() {
            return classLoaderProvider.classLoader();
        }

        @Override
        public Codec codec() { return context().codec(); }

        @Override
        public ConsumerLicenseManagerBuilder consumer() {
            return new ConsumerTrueLicenseManagerBuilder();
        }

        @Override
        public TrueLicenseApplicationContext<Model> context() {
            return TrueLicenseApplicationContext.this;
        }

        @Override
        public LicenseInitialization initialization() {
            final LicenseInitialization second = new TrueLicenseInitialization();
            return initialization
                    .map(first -> initializationComposition.compose(first, second))
                    .orElse(second);
        }

        @Override
        public License license() { return context().license(); }

        @Override
        public Store memoryStore() { return bios().memoryStore(); }

        @Override
        public Date now() { return clock.now(); }

        @Override
        public Store pathStore(Path path) { return bios().pathStore(path); }

        @Override
        public Source resource(String name) {
            return bios().resource(name, classLoader());
        }

        @Override
        public Source stdin() { return bios().stdin(); }

        @Override
        public Sink stdout() { return bios().stdout(); }

        @Override
        public String subject() { return subject; }

        @Override
        public Store systemPreferencesStore(Class<?> classInPackage) {
            return bios().systemPreferencesStore(classInPackage, subject());
        }

        @Override
        public Store userPreferencesStore(Class<?> classInPackage) {
            return bios().userPreferencesStore(classInPackage, subject());
        }

        @Override
        public LicenseValidation validation() {
            final LicenseValidation second = new TrueLicenseValidation();
            return validation
                    .map(first -> validationComposition.compose(first, second))
                    .orElse(second);
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
                        p = new TrueLicenseManagementParameters(this);
                return parent.isPresent()
                        ? p.new ChainedTrueLicenseManager()
                        : p.new CachingTrueLicenseManager();
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
                return new TrueLicenseManagementParameters(this)
                        .new TrueLicenseManager();
            }
        }

        @SuppressWarnings("unchecked")
        abstract class TrueLicenseManagerBuilder<This extends TrueLicenseManagerBuilder<This>> {

            Optional<Authentication> authentication = Optional.empty();
            Optional<Transformation> encryption = Optional.empty();
            int ftpDays;
            Optional<ConsumerLicenseManager> parent = Optional.empty();
            Optional<Store> store = Optional.empty();

            public final This authentication(final Authentication authentication) {
                this.authentication = Optional.ofNullable(authentication);
                return (This) this;
            }

            public final TrueEncryptionBuilder encryption() { return new TrueEncryptionBuilder(); }

            public final This encryption(final Transformation encryption) {
                this.encryption = Optional.ofNullable(encryption);
                return (This) this;
            }

            public final This ftpDays(final int ftpDays) {
                this.ftpDays = ftpDays;
                return (This) this;
            }

            public final TrueAuthenticationBuilder authentication() { return new TrueAuthenticationBuilder(); }

            public final This parent(final ConsumerLicenseManager parent) {
                this.parent = Optional.ofNullable(parent);
                return (This) this;
            }

            public final This storeIn(final Store store) {
                this.store = Optional.ofNullable(store);
                return (This) this;
            }

            public final This storeInPath(Path path) {
                return storeIn(pathStore(path));
            }

            public final This storeInSystemPreferences(Class<?> classInPackage) {
                return storeIn(systemPreferencesStore(classInPackage));
            }

            public final This storeInUserPreferences(Class<?> classInPackage) {
                return storeIn(userPreferencesStore(classInPackage));
            }

            final class TrueAuthenticationBuilder
            implements Builder<Authentication>, AuthenticationInjection<This> {

                Optional<String> algorithm = Optional.empty();
                Optional<String> alias = Optional.empty();
                Optional<PasswordProtection> keyProtection = Optional.empty();
                Optional<Source> source = Optional.empty();
                Optional<PasswordProtection> storeProtection = Optional.empty();
                Optional<String> storeType = Optional.empty();

                @Override
                public This inject() { return authentication(build()); }

                @Override
                public TrueAuthenticationBuilder algorithm(final String algorithm) {
                    this.algorithm = Optional.ofNullable(algorithm);
                    return this;
                }

                @Override
                public TrueAuthenticationBuilder alias(final String alias) {
                    this.alias = Optional.ofNullable(alias);
                    return this;
                }

                @Override
                public Authentication build() {
                    return context().authentication(new TrueAuthenticationParameters(this));
                }

                @Override
                public TrueAuthenticationBuilder keyProtection(final PasswordProtection keyProtection) {
                    this.keyProtection = Optional.ofNullable(keyProtection);
                    return this;
                }

                @Override
                public TrueAuthenticationBuilder loadFrom(final Source source) {
                    this.source = Optional.ofNullable(source);
                    return this;
                }

                @Override
                public TrueAuthenticationBuilder loadFromResource(String name) {
                    return loadFrom(resource(name));
                }

                @Override
                public TrueAuthenticationBuilder storeProtection(final PasswordProtection storeProtection) {
                    this.storeProtection = Optional.ofNullable(storeProtection);
                    return this;
                }

                @Override
                public TrueAuthenticationBuilder storeType(final String storeType) {
                    this.storeType = Optional.ofNullable(storeType);
                    return this;
                }
            }

            final class TrueEncryptionBuilder
            implements Builder<Transformation>, EncryptionInjection<This> {

                Optional<String> algorithm = Optional.empty();
                Optional<PasswordProtection> protection = Optional.empty();

                @Override
                public This inject() { return encryption(build()); }

                @Override
                public TrueEncryptionBuilder algorithm(final String algorithm) {
                    this.algorithm = Optional.ofNullable(algorithm);
                    return this;
                }

                @Override
                public Transformation build() {
                    return context().encryption(new TrueEncryptionParameters(this));
                }

                @Override
                public TrueEncryptionBuilder protection(final PasswordProtection protection) {
                    this.protection = Optional.ofNullable(protection);
                    return this;
                }
            }
        }

        final class TrueAuthenticationParameters implements AuthenticationParameters {

            final Optional<String> algorithm;
            final String alias;
            final Optional<PasswordProtection> keyProtection;
            final Optional<Source> source;
            final PasswordProtection storeProtection;
            final Optional<String> storeType;

            TrueAuthenticationParameters(final TrueLicenseManagerBuilder<?>.TrueAuthenticationBuilder b) {
                this.algorithm = b.algorithm;
                this.alias = b.alias.get();
                this.keyProtection = b.keyProtection;
                this.source = b.source;
                this.storeProtection = b.storeProtection.get();
                this.storeType = b.storeType;
            }

            @Override
            public String alias() { return alias; }

            @Override
            public PasswordProtection keyProtection() {
                return keyProtection
                        .map(CheckedPasswordProtection::new)
                        .orElseGet(() -> new CheckedPasswordProtection(storeProtection));
            }

            @Override
            public Optional<String> algorithm() { return algorithm; }

            @Override
            public Optional<Source> source() { return source; }

            @Override
            public PasswordProtection storeProtection() {
                return new CheckedPasswordProtection(storeProtection);
            }

            @Override
            public String storeType() {
                return storeType.orElseGet(() -> context().storeType());
            }
        }

        final class TrueEncryptionParameters implements EncryptionParameters {

            final Optional<String> algorithm;
            final PasswordProtection protection;

            TrueEncryptionParameters(final TrueLicenseManagerBuilder<?>.TrueEncryptionBuilder b) {
                this.algorithm = b.algorithm;
                this.protection = b.protection.get();
            }

            @Override
            public String algorithm() {
                return algorithm.orElseGet(TrueLicenseApplicationContext.this::pbeAlgorithm);
            }

            @Override
            public PasswordProtection protection() {
                return new CheckedPasswordProtection(protection);
            }
        }

        final class TrueLicenseManagementParameters
        implements
                ContextProvider<TrueLicenseManagementContext>,
                LicenseInitializationProvider,
                LicenseManagementParameters {

            final Authentication authentication;
            final Optional<Transformation> encryption;
            final int ftpDays;
            final Optional<ConsumerLicenseManager> parent;
            final Optional<Store> store;

            TrueLicenseManagementParameters(final TrueLicenseManagerBuilder<?> b) {
                this.authentication = b.authentication.get();
                this.encryption = b.encryption;
                this.ftpDays = b.ftpDays;
                this.parent = b.parent;
                this.store = b.store;
            }

            @Override
            public Authentication authentication() { return authentication; }

            @Override
            public TrueLicenseManagementContext context() {
                return TrueLicenseManagementContext.this;
            }

            @Override
            public Transformation encryption() {
                return encryption
                        .orElseGet(() -> parent().parameters().encryption());
            }

            @Override
            public LicenseInitialization initialization() {
                final LicenseInitialization initialization = context().initialization();
                if (0 == ftpDays)
                    return initialization;

                return bean -> {
                    initialization.initialize(bean);
                    final Calendar cal = getInstance();
                    cal.setTime(bean.getIssued());
                    bean.setNotBefore(cal.getTime()); // not before issued
                    cal.add(DATE, ftpDays); // FTP countdown starts NOW
                    bean.setNotAfter(cal.getTime());
                };
            }

            ConsumerLicenseManager parent() { return parent.get(); }

            Store store() { return store.get();}

            Transformation compressionThenEncryption() {
                return Transformer.apply(compression()).then(encryption()).get();
            }

            final class ChainedTrueLicenseManager extends CachingTrueLicenseManager {

                volatile Optional<Boolean> canGenerateLicenseKeys = Optional.empty();

                @Override
                public void install(Source source) throws LicenseManagementException {
                    try {
                        parent().install(source);
                    } catch (final LicenseManagementException first) {
                        if (canGenerateLicenseKeys())
                            throw first;
                        super.install(source);
                    }
                }

                @Override
                public License load() throws LicenseManagementException {
                    try {
                        return parent().load();
                    } catch (final LicenseManagementException first) {
                        try {
                            return super.load(); // uses store()
                        } catch (final LicenseManagementException second) {
                            synchronized (store()) {
                                try {
                                    return super.load(); // repeat
                                } catch (final LicenseManagementException third) {
                                    return generateIffNewFtp(third).license(); // uses store(), too
                                }
                            }
                        }
                    }
                }

                @Override
                public void verify() throws LicenseManagementException {
                    try {
                        parent().verify();
                    } catch (final LicenseManagementException first) {
                        try {
                            super.verify(); // uses store()
                        } catch (final LicenseManagementException second) {
                            synchronized (store()) {
                                try {
                                    super.verify(); // repeat
                                } catch (final LicenseManagementException third) {
                                    generateIffNewFtp(third); // uses store(), too
                                }
                            }
                        }
                    }
                }

                @Override
                public void uninstall() throws LicenseManagementException {
                    try {
                        parent().uninstall();
                    } catch (final LicenseManagementException first) {
                        if (canGenerateLicenseKeys())
                            throw first;
                        super.uninstall();
                    }
                }

                boolean canGenerateLicenseKeys() {
                    if (!canGenerateLicenseKeys.isPresent()) {
                        synchronized (this) {
                            if (!canGenerateLicenseKeys.isPresent()) {
                                try {
                                    // Test encoding a new license key to /dev/null .
                                    super.generateKeyFrom(license()).saveTo(memoryStore());
                                    canGenerateLicenseKeys = Optional.of(Boolean.TRUE);
                                } catch (LicenseManagementException ignored) {
                                    canGenerateLicenseKeys = Optional.of(Boolean.FALSE);
                                }
                            }
                        }
                    }
                    return canGenerateLicenseKeys.get();
                }

                LicenseKeyGenerator generateIffNewFtp(final LicenseManagementException e) throws LicenseManagementException {
                    if (!canGenerateLicenseKeys())
                        throw e;
                    final Store store = store();
                    if (exists(store))
                        throw e;
                    return super.generateKeyFrom(license()).saveTo(store);
                }
            }

            class CachingTrueLicenseManager extends TrueLicenseManager {

                // These volatile fields get initialized by applying a pure function which
                // takes the immutable value of the store() property as its single argument.
                // So some concurrent threads may safely interleave when initializing these
                // fields without creating a racing condition and thus it's not generally
                // required to synchronize access to them.
                volatile Cache<Source, Decoder> cachedDecoder = new Cache<>();
                volatile Cache<Source, License> cachedLicense = new Cache<>();

                @Override
                public void install(final Source source) throws LicenseManagementException {
                    final Store store = store();
                    synchronized (store) {
                        super.install(source);

                        final Optional<Source> optSource = Optional.ofNullable(source);
                        final Optional<Source> optStore = Optional.ofNullable(store);

                        // As a side effect of the license key installation, the cached
                        // artifactory and license get associated to the source unless this
                        // is a re-installation from an equal source or the cached objects
                        // have already been obsoleted by a time-out, that is, if the cache
                        // period is equal or close to zero.
                        assert cachedDecoder.hasKey(optSource) ||
                                cachedDecoder.hasKey(optStore) ||
                                cachedDecoder.obsolete();
                        assert cachedLicense.hasKey(optSource) ||
                                cachedLicense.hasKey(optStore) ||
                                cachedLicense.obsolete();

                        // Update the association of the cached artifactory and license to
                        // the store.
                        cachedDecoder = cachedDecoder.key(optStore);
                        cachedLicense = cachedLicense.key(optStore);
                    }
                }

                @Override
                public void uninstall() throws LicenseManagementException {
                    final Cache<Source, Decoder> cachedDecoder = new Cache<>();
                    final Cache<Source, License> cachedLicense = new Cache<>();
                    synchronized (store()) {
                        super.uninstall();
                        this.cachedDecoder = cachedDecoder;
                        this.cachedLicense = cachedLicense;
                    }
                }

                @Override
                void validate(final Source source) throws Exception {
                    final Optional<Source> optSource = Optional.ofNullable(source);
                    Optional<License> optLicense = cachedLicense.map(optSource);
                    if (!optLicense.isPresent()) {
                        optLicense = Optional.ofNullable(decodeLicense(source));
                        cachedLicense = new Cache<>(optSource, optLicense, cachePeriodMillis());
                    }
                    validation().validate(optLicense.get());
                }

                @Override
                Decoder authenticate(final Source source) throws Exception {
                    final Optional<Source> optSource = Optional.ofNullable(source);
                    Optional<Decoder> optDecoder = cachedDecoder.map(optSource);
                    if (!optDecoder.isPresent()) {
                        optDecoder = Optional.ofNullable(super.authenticate(source));
                        cachedDecoder = new Cache<>(optSource, optDecoder, cachePeriodMillis());
                    }
                    return optDecoder.get();
                }
            }

            /**
             * A basic license manager.
             * This class is immutable.
             * <p>
             * Unless stated otherwise, all no-argument methods need to return consistent
             * objects so that caching them is not required.
             * A returned object is considered to be consistent if it compares
             * {@linkplain Object#equals(Object) equal} or at least behaves identical to
             * any previously returned object.
             *
             * @author Christian Schlichtherle
             */
            class TrueLicenseManager
            implements ConsumerLicenseManager, VendorLicenseManager {

                @Override
                public LicenseKeyGenerator generateKeyFrom(final License bean) {
                    return new LicenseKeyGenerator() {

                        final RepositoryContext<Model> context = repositoryContext();
                        final Model model = context.model();
                        Decoder decoder;

                        @Override
                        public License license() throws LicenseManagementException {
                            return wrap(() -> decoder().decode(License.class));
                        }

                        @Override
                        public LicenseKeyGenerator saveTo(final Sink sink) throws LicenseManagementException {
                            wrap(new Callable<Void>() {

                                @Override public Void call() throws Exception {
                                    authorization().clearGenerate(TrueLicenseManager.this);
                                    codec().encoder(compressedAndEncryptedSink()).encode(model());
                                    return null;
                                }

                                Sink compressedAndEncryptedSink() {
                                    return compressionThenEncryption().apply(sink);
                                }
                            });
                            return this;
                        }

                        Decoder decoder() throws Exception {
                            init();
                            return decoder;
                        }

                        Model model() throws Exception {
                            init();
                            return model;
                        }

                        synchronized void init() throws Exception {
                            if (null == decoder) {
                                decoder = authentication().sign(
                                        context.controller(model, codec()),
                                        validatedBean());
                            }
                        }

                        License validatedBean() throws Exception {
                            final License duplicate = initializedBean();
                            validation().validate(duplicate);
                            return duplicate;
                        }

                        License initializedBean() throws Exception {
                            final License duplicate = duplicatedBean();
                            initialization().initialize(duplicate);
                            return duplicate;
                        }

                        License duplicatedBean() throws Exception {
                            return Codecs.clone(bean, codec());
                        }
                    };
                }

                @Override
                public void install(final Source source) throws LicenseManagementException {
                    wrap(() -> {
                        authorization().clearInstall(TrueLicenseManager.this);
                        decodeLicense(source); // checks digital signature
                        bios().copy(source, store());
                        return null;
                    });
                }

                @Override
                public License load() throws LicenseManagementException {
                    return wrap(() -> {
                        authorization().clearLoad(TrueLicenseManager.this);
                        return decodeLicense(store());
                    });
                }

                @Override
                public void verify() throws LicenseManagementException {
                    wrap(() -> {
                        authorization().clearVerify(TrueLicenseManager.this);
                        validate(store());
                        return null;
                    });
                }

                @Override
                public void uninstall() throws LicenseManagementException {
                    wrap(() -> {
                        authorization().clearUninstall(TrueLicenseManager.this);
                        final Store store1 = store();
                        // #TRUELICENSE-81: A consumer license manager must
                        // authenticate the installed license key before uninstalling
                        // it.
                        authenticate(store1);
                        store1.delete();
                        return null;
                    });
                }

                //
                // License consumer functions:
                //

                void validate(Source source) throws Exception {
                    validation().validate(decodeLicense(source));
                }

                License decodeLicense(Source source) throws Exception {
                    return authenticate(source).decode(License.class);
                }

                Decoder authenticate(Source source) throws Exception {
                    return authentication().verify(repositoryController(source));
                }

                RepositoryController repositoryController(Source source) throws Exception {
                    return repositoryContext().controller(repositoryModel(source), codec());
                }

                Model repositoryModel(Source source) throws Exception {
                    return codec().decoder(decryptedAndDecompressedSource(source)).decode(repositoryContext().model().getClass());
                }

                Source decryptedAndDecompressedSource(Source source) {
                    return compressionThenEncryption().unapply(source);
                }

                //
                // Property/factory functions:
                //

                @Override
                public final LicenseManagementContext context() {
                    return TrueLicenseManagementContext.this;
                }

                @Override
                public final TrueLicenseManagementParameters parameters() {
                    return TrueLicenseManagementParameters.this;
                }
            }
        }

        /**
         * A basic license initialization.
         * This class is immutable.
         * <p>
         * This implementation of the {@link LicenseInitialization} interface
         * initializes the license
         * {@linkplain License#getConsumerType consumer type},
         * {@linkplain License#getHolder holder},
         * {@linkplain License#getIssued issue date/time},
         * {@linkplain License#getIssuer issuer} and
         * {@linkplain License#getSubject subject}
         * unless these properties are respectively set already.
         * <p>
         * Unless stated otherwise, all no-argument methods need to return consistent
         * objects so that caching them is not required.
         * A returned object is considered to be consistent if it compares
         * {@linkplain Object#equals(Object) equal} or at least behaves identical to
         * any previously returned object.
         */
        final class TrueLicenseInitialization implements LicenseInitialization {

            @Obfuscate
            static final String DEFAULT_CONSUMER_TYPE = "User";

            /** The canonical name prefix for X.500 principals. */
            @Obfuscate
            static final String CN_PREFIX = "CN=";

            @Override
            public void initialize(final License bean) {
                if (null == bean.getConsumerType())
                    bean.setConsumerType(DEFAULT_CONSUMER_TYPE);
                if (null == bean.getHolder())
                    bean.setHolder(new X500Principal(CN_PREFIX + message(UNKNOWN)));
                if (null == bean.getIssued())
                    bean.setIssued(now()); // don't trust the system clock!
                if (null == bean.getIssuer())
                    bean.setIssuer(new X500Principal(CN_PREFIX + subject()));
                if (null == bean.getSubject())
                    bean.setSubject(subject());
            }
        }

        /**
         * A basic license validation.
         * This class is immutable.
         * <p>
         * This implementation of the {@link LicenseValidation} interface validates the
         * license
         * {@linkplain License#getConsumerAmount consumer amount},
         * {@linkplain License#getConsumerType consumer type},
         * {@linkplain License#getHolder holder},
         * {@linkplain License#getIssued issue date/time},
         * {@linkplain License#getIssuer issuer},
         * {@linkplain License#getNotAfter not after date/time} (if set),
         * {@linkplain License#getNotBefore not before date/time} (if set) and
         * {@linkplain License#getSubject subject}.
         * <p>
         * Unless stated otherwise, all no-argument methods need to return consistent
         * objects so that caching them is not required.
         * A returned object is considered to be consistent if it compares
         * {@linkplain Object#equals(Object) equal} or at least behaves identical to
         * any previously returned object.
         */
        final class TrueLicenseValidation implements LicenseValidation {

            @Override
            public void validate(final License bean) throws LicenseValidationException {
                if (0 >= bean.getConsumerAmount())
                    throw new LicenseValidationException(message(CONSUMER_AMOUNT_IS_NOT_POSITIVE, bean.getConsumerAmount()));
                if (null == bean.getConsumerType())
                    throw new LicenseValidationException(message(CONSUMER_TYPE_IS_NULL));
                if (null == bean.getHolder())
                    throw new LicenseValidationException(message(HOLDER_IS_NULL));
                if (null == bean.getIssued())
                    throw new LicenseValidationException(message(ISSUED_IS_NULL));
                if (null == bean.getIssuer())
                    throw new LicenseValidationException(message(ISSUER_IS_NULL));
                final Date now = now(); // don't trust the system clock!
                final Date notAfter = bean.getNotAfter();
                if (null != notAfter && now.after(notAfter))
                    throw new LicenseValidationException(message(LICENSE_HAS_EXPIRED, notAfter));
                final Date notBefore = bean.getNotBefore();
                if (null != notBefore && now.before(notBefore))
                    throw new LicenseValidationException(message(LICENSE_IS_NOT_YET_VALID, notBefore));
                if (!subject().equals(bean.getSubject()))
                    throw new LicenseValidationException(message(INVALID_SUBJECT, bean.getSubject(), subject()));
            }
        }
    }
}
