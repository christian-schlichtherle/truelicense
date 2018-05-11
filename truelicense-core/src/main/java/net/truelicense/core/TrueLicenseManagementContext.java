/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.truelicense.core;

import global.namespace.fun.io.api.*;
import net.truelicense.api.*;
import net.truelicense.api.auth.*;
import net.truelicense.api.codec.Codec;
import net.truelicense.api.crypto.EncryptionBuilder;
import net.truelicense.api.crypto.EncryptionFactory;
import net.truelicense.api.crypto.EncryptionParameters;
import net.truelicense.api.misc.Builder;
import net.truelicense.api.passwd.Password;
import net.truelicense.api.passwd.PasswordPolicy;
import net.truelicense.api.passwd.PasswordProtection;
import net.truelicense.api.passwd.PasswordUsage;
import net.truelicense.core.misc.Strings;
import net.truelicense.obfuscate.Obfuscate;

import javax.security.auth.x500.X500Principal;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.Callable;

import static global.namespace.fun.io.bios.BIOS.*;
import static java.util.Calendar.DATE;
import static java.util.Calendar.getInstance;
import static net.truelicense.core.Messages.*;

/** @author Christian Schlichtherle */
@SuppressWarnings({ "ConstantConditions", "OptionalUsedAsFieldOrParameterType", "unchecked" })
final class TrueLicenseManagementContext implements LicenseManagementContext, AuthenticationFactory, EncryptionFactory {

    private final AuthenticationFactory authenticationFactory;
    private final LicenseManagementAuthorization authorization;
    private final long cachePeriodMillis;
    private final Clock clock;
    private final Codec codec;
    private final Filter compression;
    private final String encryptionAlgorithm;
    private final EncryptionFactory encryptionFactory;
    private final LicenseFactory licenseFactory;
    private final Optional<LicenseInitialization> initialization;
    private final LicenseFunctionComposition initializationComposition;
    private final PasswordPolicy passwordPolicy;
    private final RepositoryContext<?> repositoryContext;
    private final String keystoreType;
    private final String subject;
    private final Optional<LicenseValidation> validation;
    private final LicenseFunctionComposition validationComposition;

    TrueLicenseManagementContext(final TrueLicenseManagementContextBuilder b) {
        this.authenticationFactory = b.authenticationFactory;
        this.authorization = b.authorization;
        this.cachePeriodMillis = b.cachePeriodMillis;
        this.clock = b.clock;
        this.codec = b.codec.get();
        this.compression = b .compression.get();
        this.encryptionAlgorithm = Strings.requireNonEmpty(b.encryptionAlgorithm);
        this.encryptionFactory = b.encryptionFactory.get();
        this.licenseFactory = b.licenseFactory.get();
        this.initialization = b.initialization;
        this.initializationComposition = b.initializationComposition;
        this.passwordPolicy = b.passwordPolicy;
        this.repositoryContext = b.repositoryContext.get();
        this.keystoreType = Strings.requireNonEmpty(b.keystoreType);
        this.subject = Strings.requireNonEmpty(b.subject);
        this.validation = b.validation;
        this.validationComposition = b.validationComposition;
    }

    private static <V> V callChecked(final Callable<V> task) throws LicenseManagementException {
        try {
            return task.call();
        } catch (RuntimeException | LicenseManagementException e) {
            throw e;
        } catch (Exception e) {
            throw new LicenseManagementException(e);
        }
    }

    @Override
    public Authentication authentication(AuthenticationParameters authenticationParameters) {
        return authenticationFactory.authentication(authenticationParameters);
    }

    private LicenseManagementAuthorization authorization() { return authorization; }

    private long cachePeriodMillis() { return cachePeriodMillis; }

    @Override
    public Codec codec() { return codec; }

    private Filter compression() { return compression; }

    @Override
    public ConsumerLicenseManagerBuilder consumer() { return new TrueConsumerLicenseManagerBuilder(); }

    private String encryptionAlgorithm() { return encryptionAlgorithm; }

    @Override
    public Filter encryption(EncryptionParameters encryptionParameters) {
        return encryptionFactory.encryption(encryptionParameters);
    }

    private LicenseInitialization initialization() {
        final LicenseInitialization second = new TrueLicenseInitialization();
        return initialization
                .map(first -> initializationComposition.compose(first, second))
                .orElse(second);
    }

    @Override
    public License license() { return licenseFactory.license(); }

    private Date now() { return Date.from(clock.instant()); }

    private PasswordPolicy passwordPolicy() { return passwordPolicy; }

    private <Model> RepositoryContext<Model> repositoryContext() {
        return (RepositoryContext<Model>) repositoryContext;
    }

    private String keystoreType() { return keystoreType; }

    @Override
    public String subject() { return subject; }

    private LicenseValidation validation() {
        final LicenseValidation second = new TrueLicenseValidation();
        return validation.map(first -> validationComposition.compose(first, second)).orElse(second);
    }

    @Override
    public VendorLicenseManagerBuilder vendor() { return new TrueVendorLicenseManagerBuilder(); }

    class TrueConsumerLicenseManagerBuilder
            extends TrueLicenseManagerBuilder<TrueConsumerLicenseManagerBuilder>
            implements ConsumerLicenseManagerBuilder {

        @Override
        public ConsumerLicenseManager build() {
            final TrueLicenseManagementSchema schema = new TrueLicenseManagementSchema(this);
            return parent.isPresent()
                    ? schema.new ChainedLicenseManager()
                    : schema.new CachingLicenseManager();
        }

        @Override
        public TrueConsumerLicenseManagerBuilder up() { throw new UnsupportedOperationException(); }

        @Override
        public ParentConsumerLicenseManagerBuilder parent() { return new ParentConsumerLicenseManagerBuilder(); }

        final class ParentConsumerLicenseManagerBuilder extends TrueConsumerLicenseManagerBuilder {

            @Override
            public TrueConsumerLicenseManagerBuilder up() { return parent(build()); }
        }
    }

    final class TrueVendorLicenseManagerBuilder
            extends TrueLicenseManagerBuilder<TrueVendorLicenseManagerBuilder>
            implements VendorLicenseManagerBuilder {

        @Override
        public VendorLicenseManager build() {
            return new TrueLicenseManagementSchema(this).new TrueLicenseManager();
        }
    }

    abstract class TrueLicenseManagerBuilder<This extends TrueLicenseManagerBuilder<This> & Builder<?>> {

        Optional<Authentication> authentication = Optional.empty();
        Optional<Filter> encryption = Optional.empty();
        int ftpDays;
        Optional<ConsumerLicenseManager> parent = Optional.empty();
        Optional<Store> store = Optional.empty();

        public final This authentication(final Authentication authentication) {
            this.authentication = Optional.ofNullable(authentication);
            return (This) this;
        }

        public final TrueEncryptionBuilder encryption() { return new TrueEncryptionBuilder(); }

        public final This encryption(final Filter encryption) {
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

        @SuppressWarnings("WeakerAccess")
        public final This storeIn(final Store store) {
            this.store = Optional.ofNullable(store);
            return (This) this;
        }

        public final This storeInPath(Path path) { return storeIn(path(path)); }

        public final This storeInSystemPreferences(Class<?> classInPackage) {
            return storeIn(systemPreferences(classInPackage, subject()));
        }

        public final This storeInUserPreferences(Class<?> classInPackage) {
            return storeIn(userPreferences(classInPackage, subject()));
        }

        final class TrueAuthenticationBuilder implements Builder<Authentication>, AuthenticationBuilder<This> {

            Optional<String> algorithm = Optional.empty();
            Optional<String> alias = Optional.empty();
            Optional<PasswordProtection> keyProtection = Optional.empty();
            Optional<Source> source = Optional.empty();
            Optional<PasswordProtection> storeProtection = Optional.empty();
            Optional<String> storeType = Optional.empty();

            @Override
            public This up() { return authentication(build()); }

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
                return TrueLicenseManagementContext.this.authentication(new TrueAuthenticationParameters(this));
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
            public TrueAuthenticationBuilder loadFromResource(String name) { return loadFrom(resource(name)); }

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

        final class TrueEncryptionBuilder implements Builder<Filter>, EncryptionBuilder<This> {

            Optional<String> algorithm = Optional.empty();
            Optional<PasswordProtection> protection = Optional.empty();

            @Override
            public This up() { return encryption(build()); }

            @Override
            public TrueEncryptionBuilder algorithm(final String algorithm) {
                this.algorithm = Optional.ofNullable(algorithm);
                return this;
            }

            @Override
            public Filter build() {
                return TrueLicenseManagementContext.this.encryption(new TrueEncryptionParameters(this));
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
        public PasswordProtection storeProtection() { return new CheckedPasswordProtection(storeProtection); }

        @Override
        public String storeType() { return storeType.orElseGet(TrueLicenseManagementContext.this::keystoreType); }
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
            return algorithm.orElseGet(TrueLicenseManagementContext.this::encryptionAlgorithm);
        }

        @Override
        public PasswordProtection protection() { return new CheckedPasswordProtection(protection); }
    }

    final class TrueLicenseManagementSchema implements LicenseManagementSchema {

        final Authentication authentication;
        final Optional<Filter> encryption;
        final int ftpDays;
        final Optional<ConsumerLicenseManager> parent;
        final Optional<Store> store;

        TrueLicenseManagementSchema(final TrueLicenseManagerBuilder<?> b) {
            this.authentication = b.authentication.get();
            this.encryption = b.encryption;
            this.ftpDays = b.ftpDays;
            this.parent = b.parent;
            this.store = b.store;
        }

        @Override
        public Authentication authentication() { return authentication; }

        Filter compressionThenEncryption() { return compression().andThen(encryption()); }

        @Override
        public TrueLicenseManagementContext context() { return TrueLicenseManagementContext.this; }

        @Override
        public Filter encryption() { return encryption.orElseGet(() -> parent().schema().encryption()); }

        LicenseInitialization initialization() {
            final LicenseInitialization initialization = context().initialization();
            if (0 != ftpDays) {
                return bean -> {
                    initialization.initialize(bean);
                    final Calendar cal = getInstance();
                    cal.setTime(bean.getIssued());
                    bean.setNotBefore(cal.getTime()); // not before issued
                    cal.add(DATE, ftpDays); // FTP countdown starts NOW
                    bean.setNotAfter(cal.getTime());
                };
            } else {
                return initialization;
            }
        }

        ConsumerLicenseManager parent() { return parent.get(); }

        Store store() { return store.get();}

        final class ChainedLicenseManager extends TrueLicenseManagementSchema.CachingLicenseManager {

            volatile Optional<Boolean> canGenerateLicenseKeys = Optional.empty();

            @Override
            public void install(Source source) throws LicenseManagementException {
                try {
                    parent().install(source);
                } catch (final LicenseManagementException first) {
                    if (canGenerateLicenseKeys()) {
                        throw first;
                    }
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
                    if (canGenerateLicenseKeys()) {
                        throw first;
                    }
                    super.uninstall();
                }
            }

            boolean canGenerateLicenseKeys() {
                if (!canGenerateLicenseKeys.isPresent()) {
                    synchronized (this) {
                        if (!canGenerateLicenseKeys.isPresent()) {
                            try {
                                // Test encoding a new license key to /dev/null .
                                super.generateKeyFrom(license()).saveTo(memory());
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
                if (!canGenerateLicenseKeys()) {
                    throw e;
                }
                final Store store = store();
                if (callChecked(store::exists)) {
                    throw e;
                }
                return super.generateKeyFrom(license()).saveTo(store);
            }
        }

        class CachingLicenseManager extends TrueLicenseManagementSchema.TrueLicenseManager {

            // These volatile fields get initialized by applying a pure function which
            // takes the immutable value of the store() property as its single argument.
            // So some concurrent threads may safely interleave when initializing these
            // fields without creating a racing condition and thus it's not generally
            // required to synchronize access to them.
            volatile Cache<Source, Decoder> cachedDecoder = new Cache<>();
            volatile Cache<Source, License> cachedLicense = new Cache<>();

            @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
            @Override
            public void install(final Source source) throws LicenseManagementException {
                final Optional<Source> optSource = Optional.of(source);
                final Store store = store();
                final Optional<Source> optStore = Optional.of(store);
                synchronized (store) {
                    super.install(source);

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
                final Optional<Source> optSource = Optional.of(source);
                Optional<License> optLicense = cachedLicense.map(optSource);
                if (!optLicense.isPresent()) {
                    optLicense = Optional.of(decodeLicense(source));
                    cachedLicense = new Cache<>(optSource, optLicense, cachePeriodMillis());
                }
                validation().validate(optLicense.get());
            }

            @Override
            Decoder authenticate(final Source source) throws Exception {
                final Optional<Source> optSource = Optional.of(source);
                Optional<Decoder> optDecoder = cachedDecoder.map(optSource);
                if (!optDecoder.isPresent()) {
                    optDecoder = Optional.of(super.authenticate(source));
                    cachedDecoder = new Cache<>(optSource, optDecoder, cachePeriodMillis());
                }
                return optDecoder.get();
            }
        }

        class TrueLicenseManager
                extends TrueHasLicenseManagementSchema
                implements ConsumerLicenseManager, VendorLicenseManager {

            @Override
            public TrueUncheckedLicenseManager unchecked() { return new TrueUncheckedLicenseManager(); }

            @Override
            public LicenseKeyGenerator generateKeyFrom(final License bean) throws LicenseManagementException {

                class TrueLicenseKeyGenerator implements LicenseKeyGenerator {

                    private final Object model = repositoryContext().model();
                    private Decoder decoder;

                    @Override
                    public License license() throws LicenseManagementException {
                        return callChecked(() -> decoder().decode(License.class));
                    }

                    @Override
                    public LicenseKeyGenerator saveTo(final Sink sink) throws LicenseManagementException {
                        callChecked(() -> {
                            codec().encoder(sink.map(compressionThenEncryption())).encode(model());
                            return null;
                        });
                        return this;
                    }

                    private Decoder decoder() throws Exception {
                        init();
                        return decoder;
                    }

                    private Object model() throws Exception {
                        init();
                        return model;
                    }

                    synchronized private void init() throws Exception {
                        if (null == decoder) {
                            decoder = authentication()
                                    .sign(repositoryContext().controller(model, codec()), validatedBean());
                        }
                    }

                    private License validatedBean() throws Exception {
                        final License duplicate = initializedBean();
                        validation().validate(duplicate);
                        return duplicate;
                    }

                    private License initializedBean() throws Exception {
                        final License duplicate = duplicatedBean();
                        initialization().initialize(duplicate);
                        return duplicate;
                    }

                    private License duplicatedBean() throws Exception { return memory().connect(codec()).clone(bean); }
                }

                return callChecked(() -> {
                    authorization().clearGenerate(this);
                    return new TrueLicenseKeyGenerator();
                });
            }

            @Override
            public void install(final Source source) throws LicenseManagementException {
                callChecked(() -> {
                    authorization().clearInstall(this);
                    decodeLicense(source); // checks digital signature
                    copy(source, store());
                    return null;
                });
            }

            @Override
            public License load() throws LicenseManagementException {
                return callChecked(() -> {
                    authorization().clearLoad(this);
                    return decodeLicense(store());
                });
            }

            @Override
            public void verify() throws LicenseManagementException {
                callChecked(() -> {
                    authorization().clearVerify(this);
                    validate(store());
                    return null;
                });
            }

            @Override
            public void uninstall() throws LicenseManagementException {
                callChecked(() -> {
                    authorization().clearUninstall(this);
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

            void validate(Source source) throws Exception { validation().validate(decodeLicense(source)); }

            License decodeLicense(Source source) throws Exception {
                return authenticate(source).decode(License.class);
            }

            Decoder authenticate(Source source) throws Exception {
                return authentication().verify(repositoryController(source));
            }

            RepositoryController repositoryController(Source source) throws Exception {
                return repositoryContext().controller(repositoryModel(source), codec());
            }

            Object repositoryModel(Source source) throws Exception {
                return codec()
                        .decoder(decryptedAndDecompressedSource(source))
                        .decode(repositoryContext().model().getClass());
            }

            Source decryptedAndDecompressedSource(Source source) { return source.map(compressionThenEncryption()); }

            final class TrueUncheckedLicenseManager
                    extends TrueHasLicenseManagementSchema
                    implements UncheckedVendorLicenseManager, UncheckedConsumerLicenseManager {

                @Override
                public TrueLicenseManager checked() { return TrueLicenseManager.this; }
            }
        }

        abstract class TrueHasLicenseManagementSchema implements HasLicenseManagementSchema {

            @Override
            public LicenseManagementSchema schema() { return TrueLicenseManagementSchema.this; }

            @Override
            public LicenseManagementContext context() { return TrueLicenseManagementContext.this; }
        }
    }

    final class TrueLicenseInitialization implements LicenseInitialization {

        @Obfuscate
        static final String DEFAULT_CONSUMER_TYPE = "User";

        /** The canonical name prefix for X.500 principals. */
        @Obfuscate
        static final String CN_PREFIX = "CN=";

        @Override
        public void initialize(final License bean) {
            if (null == bean.getConsumerType()) {
                bean.setConsumerType(DEFAULT_CONSUMER_TYPE);
            }
            if (null == bean.getHolder()) {
                bean.setHolder(new X500Principal(CN_PREFIX + message(UNKNOWN)));
            }
            if (null == bean.getIssued()) {
                bean.setIssued(now()); // don't trust the system clock!
            }
            if (null == bean.getIssuer()) {
                bean.setIssuer(new X500Principal(CN_PREFIX + subject()));
            }
            if (null == bean.getSubject()) {
                bean.setSubject(subject());
            }
        }
    }

    final class TrueLicenseValidation implements LicenseValidation {

        @Override
        public void validate(final License bean) throws LicenseValidationException {
            if (0 >= bean.getConsumerAmount()) {
                throw new LicenseValidationException(message(CONSUMER_AMOUNT_IS_NOT_POSITIVE, bean.getConsumerAmount()));
            }
            if (null == bean.getConsumerType()) {
                throw new LicenseValidationException(message(CONSUMER_TYPE_IS_NULL));
            }
            if (null == bean.getHolder()) {
                throw new LicenseValidationException(message(HOLDER_IS_NULL));
            }
            if (null == bean.getIssued()) {
                throw new LicenseValidationException(message(ISSUED_IS_NULL));
            }
            if (null == bean.getIssuer()) {
                throw new LicenseValidationException(message(ISSUER_IS_NULL));
            }
            final Date now = now(); // don't trust the system clock!
            final Date notAfter = bean.getNotAfter();
            if (null != notAfter && now.after(notAfter)) {
                throw new LicenseValidationException(message(LICENSE_HAS_EXPIRED, notAfter));
            }
            final Date notBefore = bean.getNotBefore();
            if (null != notBefore && now.before(notBefore)) {
                throw new LicenseValidationException(message(LICENSE_IS_NOT_YET_VALID, notBefore));
            }
            if (!subject().equals(bean.getSubject())) {
                throw new LicenseValidationException(message(INVALID_SUBJECT, bean.getSubject(), subject()));
            }
        }
    }

    final class CheckedPasswordProtection implements PasswordProtection {

        final PasswordProtection protection;

        CheckedPasswordProtection(final PasswordProtection protection) { this.protection = protection; }

        @Override
        public Password password(final PasswordUsage usage) throws Exception {
            if (usage.equals(PasswordUsage.WRITE)) { // checks null
                passwordPolicy().check(protection);
            }
            return protection.password(usage);
        }
    }
}
