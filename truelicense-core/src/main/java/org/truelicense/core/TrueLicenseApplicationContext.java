/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.api.*;
import org.truelicense.api.auth.*;
import org.truelicense.api.codec.Codec;
import org.truelicense.api.codec.CodecProvider;
import org.truelicense.api.codec.Decoder;
import org.truelicense.api.comp.CompressionProvider;
import org.truelicense.api.crypto.PbeParameters;
import org.truelicense.api.io.Sink;
import org.truelicense.api.io.Source;
import org.truelicense.api.io.Store;
import org.truelicense.api.io.Transformation;
import org.truelicense.api.misc.*;
import org.truelicense.api.passwd.*;
import org.truelicense.core.auth.Notary;
import org.truelicense.core.passwd.MinimumPasswordPolicy;
import org.truelicense.core.passwd.ObfuscatedPasswordProtection;
import org.truelicense.obfuscate.Obfuscate;
import org.truelicense.obfuscate.ObfuscatedString;
import org.truelicense.spi.codec.Codecs;
import org.truelicense.spi.io.BIOS;
import org.truelicense.spi.io.BiosProvider;
import org.truelicense.spi.io.StandardBIOS;
import org.truelicense.spi.misc.Option;

import javax.security.auth.x500.X500Principal;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import static java.util.Calendar.DATE;
import static java.util.Calendar.getInstance;
import static java.util.Objects.requireNonNull;
import static org.truelicense.core.Messages.*;

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
public abstract class TrueLicenseApplicationContext<Model>
implements BiosProvider,
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
    public Authentication authentication(KeyStoreParameters parameters) {
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
    public final List<ClassLoader> classLoader() {
        return Option.wrap(Thread.currentThread().getContextClassLoader());
    }

    @Override
    public final LicenseManagementContextBuilder context() {
        return new TrueLicenseManagementContextBuilder();
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
     * @see PbeInjection#algorithm
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

    static String requireNonEmpty(final String s) {
        if (s.isEmpty())
            throw new IllegalArgumentException();
        return s;
    }

    static boolean exists(final Store store) throws LicenseManagementException {
        return wrap(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return store.exists();
            }
        });
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
    static <V> V wrap(final Callable<V> task) throws LicenseManagementException {
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
    implements ContextProvider<TrueLicenseApplicationContext>,
               LicenseManagementContextBuilder {

        LicenseManagementAuthorization authorization = context().authorization();
        Clock clock = context();
        List<LicenseInitialization> initialization = Option.none();
        LicenseFunctionComposition initializationComposition = LicenseFunctionComposition.decorate;
        String subject = "";
        List<LicenseValidation> validation = Option.none();
        LicenseFunctionComposition validationComposition = LicenseFunctionComposition.decorate;

        @Override
        public LicenseManagementContextBuilder authorization(final LicenseManagementAuthorization authorization) {
            this.authorization = requireNonNull(authorization);
            return this;
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
            this.initialization = Option.wrap(initialization);
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
            this.validation = Option.wrap(validation);
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
    implements Clock,
            ContextProvider<TrueLicenseApplicationContext>,
            LicenseManagementAuthorizationProvider,
            LicenseInitializationProvider,
            LicenseManagementContext,
            LicenseManagementSubjectProvider,
            LicenseValidationProvider {

        final LicenseManagementAuthorization authorization;
        final Clock clock;
        final List<LicenseInitialization> initialization;
        final LicenseFunctionComposition initializationComposition;
        final String subject;
        final List<LicenseValidation> validation;
        final LicenseFunctionComposition validationComposition;

        TrueLicenseManagementContext(final TrueLicenseManagementContextBuilder b) {
            this.authorization = b.authorization;
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
        public List<ClassLoader> classLoader() { return context().classLoader(); }

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
            for (LicenseInitialization first : initialization)
                return initializationComposition.compose(first, second);
            return second;
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
            for (LicenseValidation first : validation)
                return validationComposition.compose(first, second);
            return second;
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
                        ? lp.new CachingTrueLicenseManager()
                        : lp.new ChainedTrueLicenseManager();
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

            List<Authentication> authentication = Option.none();
            List<Transformation> encryption = Option.none();
            int ftpDays;
            List<ConsumerLicenseManager> parent = Option.none();
            List<Store> store = Option.none();

            public final This authentication(final Authentication authentication) {
                this.authentication = Option.wrap(authentication);
                return (This) this;
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
                return storeIn(pathStore(path));
            }

            public final This storeInSystemPreferences(Class<?> classInPackage) {
                return storeIn(systemPreferencesStore(classInPackage));
            }

            public final This storeInUserPreferences(Class<?> classInPackage) {
                return storeIn(userPreferencesStore(classInPackage));
            }

            final class KsbaBuilder
            implements Builder<Authentication>, KsbaInjection<This> {

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
                    return context().authentication(new TrueKeyStoreParameters(this));
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
            implements Builder<Transformation>, PbeInjection<This> {

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
                    return context().encryption(new TruePbeParameters(this));
                }

                @Override
                public PbeBuilder protection(final PasswordProtection protection) {
                    this.protection = Option.wrap(protection);
                    return this;
                }
            }
        }

        final class TrueKeyStoreParameters implements KeyStoreParameters {

            final List<String> algorithm;
            final String alias;
            final List<PasswordProtection> keyProtection;
            final List<Source> source;
            final PasswordProtection storeProtection;
            final List<String> storeType;

            TrueKeyStoreParameters(final TrueLicenseManagerBuilder<?>.KsbaBuilder b) {
                this.algorithm = b.algorithm;
                this.alias = b.alias.get(0);
                this.keyProtection = b.keyProtection;
                this.source = b.source;
                this.storeProtection = b.storeProtection.get(0);
                this.storeType = b.storeType;
            }

            @Override
            public String alias() { return alias; }

            @Override
            public PasswordProtection keyProtection() {
                for (PasswordProtection kp : keyProtection)
                    return new CheckedPasswordProtection(kp);
                return new CheckedPasswordProtection(storeProtection);
            }

            @Override
            public List<String> algorithm() { return algorithm; }

            @Override
            public List<Source> source() { return source; }

            @Override
            public PasswordProtection storeProtection() {
                return new CheckedPasswordProtection(storeProtection);
            }

            @Override
            public String storeType() {
                for (String st : storeType)
                    return st;
                return context().storeType();
            }
        }

        final class TruePbeParameters implements PbeParameters {

            final List<String> algorithm;
            final PasswordProtection protection;

            TruePbeParameters(final TrueLicenseManagerBuilder<?>.PbeBuilder b) {
                this.algorithm = b.algorithm;
                this.protection = b.protection.get(0);
            }

            @Override
            public String algorithm() {
                for (String a : algorithm)
                    return a;
                return pbeAlgorithm();
            }

            @Override
            public PasswordProtection protection() {
                return new CheckedPasswordProtection(protection);
            }
        }

        final class TrueLicenseManagementParameters
        implements ContextProvider<TrueLicenseManagementContext>,
                LicenseInitializationProvider,
                LicenseManagementParameters {

            final List<Authentication> authentication;
            final List<Transformation> encryption;
            final int ftpDays;
            final List<ConsumerLicenseManager> parent;
            final List<Store> store;

            TrueLicenseManagementParameters(final TrueLicenseManagerBuilder<?> b) {
                this.authentication = b.authentication;
                this.encryption = b.encryption;
                this.ftpDays = b.ftpDays;
                this.parent = b.parent;
                this.store = b.store;
            }

            @Override
            public Authentication authentication() { return authentication.get(0); }

            @Override
            public TrueLicenseManagementContext context() {
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

            public ConsumerLicenseManager parent() { return parent.get(0); }

            public Store store() { return store.get(0);}

            final class ChainedTrueLicenseManager extends CachingTrueLicenseManager {

                volatile List<Boolean> canGenerateLicenseKeys = Option.none();

                @Override
                public void install(Source source) throws LicenseManagementException {
                    try {
                        parent().install(source);
                    } catch (final LicenseManagementException primary) {
                        if (canGenerateLicenseKeys())
                            throw primary;
                        super.install(source);
                    }
                }

                @Override
                public License view() throws LicenseManagementException {
                    try {
                        return parent().view();
                    } catch (final LicenseManagementException primary) {
                        try {
                            return super.view(); // uses store()
                        } catch (final LicenseManagementException secondary) {
                            synchronized (store()) {
                                try {
                                    return super.view(); // repeat
                                } catch (final LicenseManagementException ternary) {
                                    return generateIffNewFtp(ternary).license(); // uses store(), too
                                }
                            }
                        }
                    }
                }

                @Override
                public void verify() throws LicenseManagementException {
                    try {
                        parent().verify();
                    } catch (final LicenseManagementException primary) {
                        try {
                            super.verify(); // uses store()
                        } catch (final LicenseManagementException secondary) {
                            synchronized (store()) {
                                try {
                                    super.verify(); // repeat
                                } catch (final LicenseManagementException ternary) {
                                    generateIffNewFtp(ternary); // uses store(), too
                                }
                            }
                        }
                    }
                }

                @Override
                public void uninstall() throws LicenseManagementException {
                    try {
                        parent().uninstall();
                    } catch (final LicenseManagementException primary) {
                        if (canGenerateLicenseKeys()) throw primary;
                        super.uninstall();
                    }
                }

                boolean canGenerateLicenseKeys() {
                    if (canGenerateLicenseKeys.isEmpty()) {
                        synchronized (this) {
                            if (canGenerateLicenseKeys.isEmpty()) {
                                try {
                                    // Test encoding a new license key to /dev/null .
                                    super.generator(license()).writeTo(bios().memoryStore());
                                    canGenerateLicenseKeys = Option.wrap(Boolean.TRUE);
                                } catch (LicenseManagementException ignored) {
                                    canGenerateLicenseKeys = Option.wrap(Boolean.FALSE);
                                }
                            }
                        }
                    }
                    return canGenerateLicenseKeys.get(0);
                }

                LicenseKeyGenerator generateIffNewFtp(final LicenseManagementException e) throws LicenseManagementException {
                    if (!canGenerateLicenseKeys())
                        throw e;
                    final Store store = store();
                    if (exists(store))
                        throw e;
                    return super.generator(license()).writeTo(store);
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

                        final List<Source> optSource = Option.wrap(source);
                        final List<Source> optStore = Option.<Source>wrap(store);

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
                    final List<Source> optSource = Option.wrap(source);
                    List<License> optLicense = cachedLicense.map(optSource);
                    if (optLicense.isEmpty()) {
                        optLicense = Option.wrap(decodeLicense(source));
                        cachedLicense = new Cache<>(optSource, optLicense, cachePeriodMillis());
                    }
                    validation().validate(optLicense.get(0));
                }

                @Override
                Decoder authenticate(final Source source) throws Exception {
                    final List<Source> optSource = Option.wrap(source);
                    List<Decoder> optDecoder = cachedDecoder.map(optSource);
                    if (optDecoder.isEmpty()) {
                        optDecoder = Option.wrap(super.authenticate(source));
                        cachedDecoder = new Cache<>(optSource, optDecoder, cachePeriodMillis());
                    }
                    return optDecoder.get(0);
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
                public LicenseKeyGenerator generator(final License bean) throws LicenseManagementException {
                    return wrap(new Callable<LicenseKeyGenerator>() {
                        @Override
                        public LicenseKeyGenerator call() throws Exception {
                            authorization().clearGenerator(TrueLicenseManager.this);
                            return new LicenseKeyGenerator() {

                                final RepositoryContext<Model> context = repositoryContext();
                                final Model model = context.model();
                                final Decoder decoder = authentication().sign(
                                        context.controller(model, codec()),
                                        validatedBean());

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

                                @Override
                                public License license() throws LicenseManagementException {
                                    return wrap(new Callable<License>() {
                                        @Override
                                        public License call() throws Exception {
                                            return decoder.decode(License.class);
                                        }
                                    });
                                }

                                @Override
                                public LicenseKeyGenerator writeTo(final Sink sink) throws LicenseManagementException {
                                    wrap(new Callable<Void>() {

                                        @Override public Void call() throws Exception {
                                            codec().encoder(compressedAndEncryptedSink()).encode(model);
                                            return null;
                                        }

                                        Sink compressedAndEncryptedSink() {
                                            return compression().apply(encryptedSink());
                                        }

                                        Sink encryptedSink() {
                                            return encryption().apply(sink);
                                        }
                                    });
                                    return this;
                                }
                            };
                        }

                    });
                }

                @Override
                public void install(final Source source)
                        throws LicenseManagementException {
                    wrap(new Callable<Void>() {
                        @Override public Void call() throws Exception {
                            authorization().clearInstall(TrueLicenseManager.this);
                            decodeLicense(source); // checks digital signature
                            bios().copy(source, store());
                            return null;
                        }
                    });
                }

                @Override
                public License view() throws LicenseManagementException {
                    return wrap(new Callable<License>() {
                        @Override public License call() throws Exception {
                            authorization().clearView(TrueLicenseManager.this);
                            return decodeLicense(store());
                        }
                    });
                }

                @Override
                public void verify() throws LicenseManagementException {
                    wrap(new Callable<Void>() {
                        @Override public Void call() throws Exception {
                            authorization().clearVerify(TrueLicenseManager.this);
                            validate(store());
                            return null;
                        }
                    });
                }

                @Override
                public void uninstall() throws LicenseManagementException {
                    wrap(new Callable<Void>() {
                        @Override public Void call() throws Exception {
                            authorization().clearUninstall(TrueLicenseManager.this);
                            final Store store = store();
                            // #TRUELICENSE-81: A consumer license manager must
                            // authenticate the installed license key before uninstalling
                            // it.
                            authenticate(store);
                            store.delete();
                            return null;
                        }
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
                    return codec().decoder(decompress(source)).decode(repositoryContext().model().getClass());
                }

                Source decompress(Source source) {
                    return compression().unapply(decrypt(source));
                }

                Source decrypt(Source source) { return encryption().unapply(source); }

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
