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
import org.truelicense.core.passwd.Passwords;
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
     * The implementation in the class {@link TrueLicenseApplicationContext}
     * returns
     * {@link Passwords#newPasswordProtection(ObfuscatedString) Passwords.newPasswordProtecetion(os)}.
     */
    @Override
    public final PasswordProtection protection(ObfuscatedString os) {
        return Passwords.newPasswordProtection(os);
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

    static boolean exists(final Store store) throws LicenseManagementException {
        return wrap(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return store.exists();
            }
        });
    }

    static long requireNonNegative(final long l) {
        if (0 > l)
            throw new IllegalArgumentException();
        return l;
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

    final class TrueLicenseManagementContextBuilder
    implements ContextProvider<TrueLicenseApplicationContext>,
               LicenseManagementContextBuilder {

        LicenseManagementAuthorization authorization = context().authorization();
        Clock clock = context();
        List<LicenseInitialization> initialization = Option.none();
        LicenseFunctionComposition initializationComposition = LicenseFunctionComposition.decorate;
        String subject;
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
            this.subject = requireNonNull(subject);
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
    implements BiosProvider,
            CachePeriodProvider,
            Clock,
            CodecProvider,
            CompressionProvider,
            ContextProvider<TrueLicenseApplicationContext>,
            LicenseManagementAuthorizationProvider,
            LicenseFactory,
            LicenseInitializationProvider,
            LicenseManagementContext,
            LicenseSubjectProvider,
            LicenseValidationProvider,
            PasswordPolicyProvider,
            RepositoryContextProvider<Model> {

        private final LicenseManagementAuthorization authorization;
        private final Clock clock;
        private final List<LicenseInitialization> initialization;
        private final LicenseFunctionComposition initializationComposition;
        private final String subject;
        private final List<LicenseValidation> validation;
        private final LicenseFunctionComposition validationComposition;

        TrueLicenseManagementContext(final TrueLicenseManagementContextBuilder b) {
            this.authorization = b.authorization;
            this.clock = b.clock;
            this.initialization = b.initialization;
            this.initializationComposition = b.initializationComposition;
            this.subject = b.subject;
            this.validation = b.validation;
            this.validationComposition = b.validationComposition;
        }

        Authentication authentication(KeyStoreParameters parameters) {
            return context().authentication(parameters);
        }

        @Override
        public LicenseManagementAuthorization authorization() {
            return authorization;
        }

        @Override
        public BIOS bios() { return context().bios(); }

        @Override
        public long cachePeriodMillis() { return context().cachePeriodMillis(); }

        PasswordProtection checkedProtection(final PasswordProtection password) {
            return new PasswordProtection() {

                @Override
                public Password password(final PasswordUsage usage) throws Exception {
                    if (usage.equals(PasswordUsage.WRITE)) // check null
                        policy().check(password);
                    return password.password(usage);
                }
            };
        }

        @Override
        public List<ClassLoader> classLoader() { return context().classLoader(); }

        @Override
        public Codec codec() { return context().codec(); }

        @Override
        public Transformation compression() { return context().compression(); }

        @Override
        public ConsumerLicenseManagerBuilder consumer() {
            return new ConsumerTrueLicenseManagerBuilder();
        }

        @Override
        public TrueLicenseApplicationContext<Model> context() {
            return TrueLicenseApplicationContext.this;
        }

        Transformation encryption(PbeParameters parameters) {
            return context().encryption(parameters);
        }

        @Override
        public LicenseInitialization initialization() {
            final LicenseInitialization secondary = new TrueLicenseInitialization();
            for (LicenseInitialization primary : initialization)
                return initializationComposition.compose(primary, secondary);
            return secondary;
        }

        Authentication ksba(
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

                TrueLicenseManagementContext context() {
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
        public License license() { return context().license(); }

        @Override
        public Store memoryStore() { return bios().memoryStore(); }

        @Override
        public Date now() { return clock.now(); }

        @Override
        public Store pathStore(Path path) { return bios().pathStore(path); }

        Transformation pbe(
                List<String> algorithm,
                PasswordProtection password) {
            return encryption(pbeParameters(algorithm, password));
        }

        String pbeAlgorithm() { return context().pbeAlgorithm(); }

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

                TrueLicenseManagementContext context() {
                    return TrueLicenseManagementContext.this;
                }

                @Override
                public PasswordProtection protection() {
                    return context().checkedProtection(password);
                }
            };
        }

        @Override
        public PasswordPolicy policy() { return context().policy(); }

        @Override
        public RepositoryContext<Model> repositoryContext() {
            return context().repositoryContext();
        }

        @Override
        public Source resource(String name) {
            return bios().resource(name, classLoader());
        }

        @Override
        public Source stdin() { return bios().stdin(); }

        @Override
        public Sink stdout() { return bios().stdout(); }

        String storeType() { return context().storeType(); }

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
            final LicenseValidation secondary = new TrueLicenseValidation();
            for (LicenseValidation primary : validation)
                return validationComposition.compose(primary, secondary);
            return secondary;
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
        abstract class TrueLicenseManagerBuilder<This extends TrueLicenseManagerBuilder<This>>
        implements ContextProvider<TrueLicenseManagementContext> {

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
            public final TrueLicenseManagementContext context() {
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
        implements BiosProvider,
                CachePeriodProvider,
                CodecProvider,
                CompressionProvider,
                ContextProvider<TrueLicenseManagementContext>,
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
            public LicenseManagementAuthorization authorization() { return context().authorization(); }

            @Override
            public BIOS bios() { return context().bios(); }

            @Override
            public long cachePeriodMillis() { return context().cachePeriodMillis(); }

            @Override
            public Codec codec() { return context().codec(); }

            @Override
            public Transformation compression() { return context().compression(); }

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

            @Override
            public License license() { return context().license(); }

            public ConsumerLicenseManager parent() { return parent.get(0); }

            @Override
            public RepositoryContext<Model> repositoryContext() {
                return context().repositoryContext();
            }

            public Store store() { return store.get(0);}

            @Override
            public LicenseValidation validation() { return context().validation(); }

            /**
             * A caching consumer license manager which establishes a Chain Of
             * Responsibility with its parent consumer license manager.
             * On each operation, the parent consumer license manager is tried first.
             * This class is thread-safe.
             *
             * @author Christian Schlichtherle
             */
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

                License license() { return parameters().license(); }

                ConsumerLicenseManager parent() { return parameters().parent(); }
            }

            /**
             * A basic consumer license manager which caches some computed objects to speed
             * up subsequent requests.
             * This class is thread-safe.
             *
             * @author Christian Schlichtherle
             */
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

                final long cachePeriodMillis() {
                    return requireNonNegative(parameters().cachePeriodMillis());
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
                                final Decoder artifactory = authentication()
                                        .sign(context.controller(model, codec()), validatedBean());

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
                                            return artifactory.decode(License.class);
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

                final Authentication authentication() {
                    return parameters().authentication();
                }

                final LicenseManagementAuthorization authorization() {
                    return parameters().authorization();
                }

                final BIOS bios() { return parameters().bios(); }

                final Codec codec() { return parameters().codec(); }

                final Transformation compression() { return parameters().compression(); }

                @Override
                public final LicenseManagementContext context() {
                    return parameters().context();
                }

                final Transformation encryption() { return parameters().encryption(); }

                final LicenseInitialization initialization() {
                    return parameters().initialization();
                }

                @Override
                public final TrueLicenseManagementParameters parameters() {
                    return TrueLicenseManagementParameters.this;
                }

                final RepositoryContext<Model> repositoryContext() {
                    return parameters().repositoryContext();
                }

                final Store store() { return parameters().store(); }

                final LicenseValidation validation() { return parameters().validation(); }
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
         *
         * @author Christian Schlichtherle
         */
        final class TrueLicenseInitialization
        implements Clock,
                ContextProvider<TrueLicenseManagementContext>,
                LicenseInitialization,
                LicenseSubjectProvider {

            @Obfuscate
            static final String DEFAULT_CONSUMER_TYPE = "User";

            /** The canonical name prefix for X.500 principals. */
            @Obfuscate
            static final String CN_PREFIX = "CN=";

            @Override
            public TrueLicenseManagementContext context() {
                return TrueLicenseManagementContext.this;
            }

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

            @Override
            public Date now() { return context().now(); }

            @Override
            public String subject() { return context().subject(); }
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
         * {@linkplain License#getSubject subject},
         * {@linkplain License#getNotAfter not after date/time} (if set) and
         * {@linkplain License#getNotBefore not before date/time} (if set).
         * <p>
         * Unless stated otherwise, all no-argument methods need to return consistent
         * objects so that caching them is not required.
         * A returned object is considered to be consistent if it compares
         * {@linkplain Object#equals(Object) equal} or at least behaves identical to
         * any previously returned object.
         *
         * @author Christian Schlichtherle
         */
        final class TrueLicenseValidation
        implements Clock,
                ContextProvider<TrueLicenseManagementContext>,
                LicenseSubjectProvider,
                LicenseValidation {

            @Override
            public TrueLicenseManagementContext context() {
                return TrueLicenseManagementContext.this;
            }

            @Override
            public Date now() { return context().now(); }

            @Override
            public String subject() { return context().subject(); }

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
