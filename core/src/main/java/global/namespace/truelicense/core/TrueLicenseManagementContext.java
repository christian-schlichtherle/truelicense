/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.core;

import global.namespace.fun.io.api.*;
import global.namespace.truelicense.api.*;
import global.namespace.truelicense.api.auth.*;
import global.namespace.truelicense.api.builder.GenBuilder;
import global.namespace.truelicense.api.codec.Codec;
import global.namespace.truelicense.api.crypto.Encryption;
import global.namespace.truelicense.api.crypto.EncryptionChildBuilder;
import global.namespace.truelicense.api.crypto.EncryptionFactory;
import global.namespace.truelicense.api.crypto.EncryptionParameters;
import global.namespace.truelicense.api.passwd.Password;
import global.namespace.truelicense.api.passwd.PasswordPolicy;
import global.namespace.truelicense.api.passwd.PasswordProtection;
import global.namespace.truelicense.api.passwd.PasswordUsage;
import global.namespace.truelicense.core.misc.Strings;
import global.namespace.truelicense.obfuscate.Obfuscate;

import javax.security.auth.x500.X500Principal;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.Callable;

import static global.namespace.fun.io.bios.BIOS.*;
import static global.namespace.truelicense.core.Messages.message;
import static java.util.Calendar.DATE;
import static java.util.Calendar.getInstance;

@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "unchecked", "OptionalGetWithoutIsPresent"})
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
    private final RepositoryFactory<?> repositoryFactory;
    private final String keystoreType;
    private final String subject;
    private final Optional<LicenseValidation> validation;
    private final LicenseFunctionComposition validationComposition;

    TrueLicenseManagementContext(final TrueLicenseManagementContextBuilder b) {
        this.authenticationFactory = b.authenticationFactory;
        this.authorization = b.authorization;
        this.cachePeriodMillis = b.cachePeriodMillis;
        this.clock = b.clock;
        this.codec = b.codecFactory.get().codec();
        this.compression = b.compression.get();
        this.encryptionAlgorithm = Strings.requireNonEmpty(b.encryptionAlgorithm);
        this.encryptionFactory = b.encryptionFactory.get();
        this.licenseFactory = b.licenseFactory.get();
        this.initialization = b.initialization;
        this.initializationComposition = b.initializationComposition;
        this.passwordPolicy = b.passwordPolicy;
        this.repositoryFactory = b.repositoryFactory.get();
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

    private LicenseManagementAuthorization authorization() {
        return authorization;
    }

    private long cachePeriodMillis() {
        return cachePeriodMillis;
    }

    @Override
    public Codec codec() {
        return codec;
    }

    private Filter compression() {
        return compression;
    }

    @Override
    public ConsumerLicenseManagerBuilder consumer() {
        return new TrueConsumerLicenseManagerBuilder();
    }

    private String encryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    @Override
    public Encryption encryption(EncryptionParameters encryptionParameters) {
        return encryptionFactory.encryption(encryptionParameters);
    }

    private LicenseInitialization initialization() {
        final LicenseInitialization second = new TrueLicenseInitialization();
        return initialization
                .map(first -> initializationComposition.compose(first, second))
                .orElse(second);
    }

    @Override
    public LicenseFactory licenseFactory() {
        return licenseFactory;
    }

    private Date now() {
        return Date.from(clock.instant());
    }

    private PasswordPolicy passwordPolicy() {
        return passwordPolicy;
    }

    @Override
    public RepositoryFactory<?> repositoryFactory() {
        return repositoryFactory;
    }

    private String keystoreType() {
        return keystoreType;
    }

    @Override
    public String subject() {
        return subject;
    }

    private LicenseValidation validation() {
        final LicenseValidation second = new TrueLicenseValidation();
        return validation.map(first -> validationComposition.compose(first, second)).orElse(second);
    }

    @Override
    public VendorLicenseManagerBuilder vendor() {
        return new TrueVendorLicenseManagerBuilder();
    }

    class TrueConsumerLicenseManagerBuilder
            extends TrueLicenseManagerBuilder<TrueConsumerLicenseManagerBuilder>
            implements ConsumerLicenseManagerBuilder {

        @Override
        public ConsumerLicenseManager build() {
            final TrueLicenseManagerParameters parameters = new TrueLicenseManagerParameters(this);
            return parent.isPresent()
                    ? parameters.new ChainedLicenseManager()
                    : parameters.new CachingLicenseManager();
        }

        @Override
        public TrueConsumerLicenseManagerBuilder up() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ParentConsumerLicenseManagerBuilder parent() {
            return new ParentConsumerLicenseManagerBuilder();
        }

        final class ParentConsumerLicenseManagerBuilder extends TrueConsumerLicenseManagerBuilder {

            @Override
            public TrueConsumerLicenseManagerBuilder up() {
                return parent(build());
            }
        }
    }

    final class TrueVendorLicenseManagerBuilder
            extends TrueLicenseManagerBuilder<TrueVendorLicenseManagerBuilder>
            implements VendorLicenseManagerBuilder {

        @Override
        public VendorLicenseManager build() {
            return new TrueLicenseManagerParameters(this).new TrueLicenseManager();
        }
    }

    abstract class TrueLicenseManagerBuilder<This extends TrueLicenseManagerBuilder<This> & GenBuilder<?>> {

        Optional<Authentication> authentication = Optional.empty();
        Optional<Filter> encryption = Optional.empty();
        int ftpDays;
        Optional<ConsumerLicenseManager> parent = Optional.empty();
        Optional<Store> store = Optional.empty();

        @SuppressWarnings("WeakerAccess")
        public final This authentication(final Authentication authentication) {
            this.authentication = Optional.ofNullable(authentication);
            return (This) this;
        }

        public final TrueEncryptionChildBuilder encryption() {
            return new TrueEncryptionChildBuilder();
        }

        public final This encryption(final Filter encryption) {
            this.encryption = Optional.ofNullable(encryption);
            return (This) this;
        }

        public final This ftpDays(final int ftpDays) {
            this.ftpDays = ftpDays;
            return (This) this;
        }

        public final TrueAuthenticationChildBuilder authentication() {
            return new TrueAuthenticationChildBuilder();
        }

        public final This parent(final ConsumerLicenseManager parent) {
            this.parent = Optional.ofNullable(parent);
            return (This) this;
        }

        @SuppressWarnings("WeakerAccess")
        public final This storeIn(final Store store) {
            this.store = Optional.ofNullable(store);
            return (This) this;
        }

        public final This storeInPath(Path path) {
            return storeIn(path(path));
        }

        public final This storeInSystemPreferences(Class<?> classInPackage) {
            return storeIn(systemPreferences(classInPackage, subject()));
        }

        public final This storeInUserPreferences(Class<?> classInPackage) {
            return storeIn(userPreferences(classInPackage, subject()));
        }

        final class TrueAuthenticationChildBuilder implements GenBuilder<Authentication>, AuthenticationChildBuilder<This> {

            Optional<String> algorithm = Optional.empty();
            Optional<String> alias = Optional.empty();
            Optional<PasswordProtection> keyProtection = Optional.empty();
            Optional<Source> source = Optional.empty();
            Optional<PasswordProtection> storeProtection = Optional.empty();
            Optional<String> storeType = Optional.empty();

            @Override
            public This up() {
                return authentication(build());
            }

            @Override
            public TrueAuthenticationChildBuilder algorithm(final String algorithm) {
                this.algorithm = Optional.ofNullable(algorithm);
                return this;
            }

            @Override
            public TrueAuthenticationChildBuilder alias(final String alias) {
                this.alias = Optional.ofNullable(alias);
                return this;
            }

            @Override
            public Authentication build() {
                return TrueLicenseManagementContext.this.authentication(new TrueAuthenticationParameters(this));
            }

            @Override
            public TrueAuthenticationChildBuilder keyProtection(final PasswordProtection keyProtection) {
                this.keyProtection = Optional.ofNullable(keyProtection);
                return this;
            }

            @Override
            public TrueAuthenticationChildBuilder loadFrom(final Source source) {
                this.source = Optional.ofNullable(source);
                return this;
            }

            @Override
            public TrueAuthenticationChildBuilder loadFromResource(String name) {
                return loadFrom(resource(name));
            }

            @Override
            public TrueAuthenticationChildBuilder storeProtection(final PasswordProtection storeProtection) {
                this.storeProtection = Optional.ofNullable(storeProtection);
                return this;
            }

            @Override
            public TrueAuthenticationChildBuilder storeType(final String storeType) {
                this.storeType = Optional.ofNullable(storeType);
                return this;
            }
        }

        final class TrueEncryptionChildBuilder implements GenBuilder<Filter>, EncryptionChildBuilder<This> {

            Optional<String> algorithm = Optional.empty();
            Optional<PasswordProtection> protection = Optional.empty();

            @Override
            public This up() {
                return encryption(build());
            }

            @Override
            public TrueEncryptionChildBuilder algorithm(final String algorithm) {
                this.algorithm = Optional.ofNullable(algorithm);
                return this;
            }

            @Override
            public Filter build() {
                return TrueLicenseManagementContext.this.encryption(new TrueEncryptionParameters(this));
            }

            @Override
            public TrueEncryptionChildBuilder protection(final PasswordProtection protection) {
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

        TrueAuthenticationParameters(final TrueLicenseManagerBuilder<?>.TrueAuthenticationChildBuilder b) {
            this.algorithm = b.algorithm;
            this.alias = b.alias.get();
            this.keyProtection = b.keyProtection;
            this.source = b.source;
            this.storeProtection = b.storeProtection.get();
            this.storeType = b.storeType;
        }

        @Override
        public String alias() {
            return alias;
        }

        @Override
        public PasswordProtection keyProtection() {
            return keyProtection
                    .map(CheckedPasswordProtection::new)
                    .orElseGet(() -> new CheckedPasswordProtection(storeProtection));
        }

        @Override
        public Optional<String> algorithm() {
            return algorithm;
        }

        @Override
        public Optional<Source> source() {
            return source;
        }

        @Override
        public PasswordProtection storeProtection() {
            return new CheckedPasswordProtection(storeProtection);
        }

        @Override
        public String storeType() {
            return storeType.orElseGet(TrueLicenseManagementContext.this::keystoreType);
        }
    }

    final class TrueEncryptionParameters implements EncryptionParameters {

        final Optional<String> algorithm;
        final PasswordProtection protection;

        TrueEncryptionParameters(final TrueLicenseManagerBuilder<?>.TrueEncryptionChildBuilder b) {
            this.algorithm = b.algorithm;
            this.protection = b.protection.get();
        }

        @Override
        public String algorithm() {
            return algorithm.orElseGet(TrueLicenseManagementContext.this::encryptionAlgorithm);
        }

        @Override
        public PasswordProtection protection() {
            return new CheckedPasswordProtection(protection);
        }
    }

    final class TrueLicenseManagerParameters implements LicenseManagerParameters {

        final Authentication authentication;
        final Optional<Filter> encryption;
        final int ftpDays;
        final Optional<ConsumerLicenseManager> parent;
        final Optional<Store> store;

        TrueLicenseManagerParameters(final TrueLicenseManagerBuilder<?> b) {
            this.authentication = b.authentication.get();
            this.encryption = b.encryption;
            this.ftpDays = b.ftpDays;
            this.parent = b.parent;
            this.store = b.store;
        }

        @Override
        public Authentication authentication() {
            return authentication;
        }

        @Override
        public Codec codec() {
            return codec;
        }

        Filter compressionAndEncryption() {
            return Filters.compressionAndEncryption(compression(), encryption());
        }

        @Override
        public TrueLicenseManagementContext context() {
            return TrueLicenseManagementContext.this;
        }

        @Override
        public Filter encryption() {
            return encryption.orElseGet(() -> parent().parameters().encryption());
        }

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

        License license() {
            return licenseFactory.license();
        }

        Class<? extends License> licenseClass() {
            return licenseFactory.licenseClass();
        }

        @Override
        public LicenseFactory licenseFactory() {
            return licenseFactory;
        }

        ConsumerLicenseManager parent() {
            return parent.get();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public RepositoryFactory repositoryFactory() {
            return repositoryFactory;
        }

        Store store() {
            return store.get();
        }

        @Override
        public String subject() {
            return subject;
        }

        final class ChainedLicenseManager extends CachingLicenseManager {

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

        class CachingLicenseManager extends TrueLicenseManager {

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
                extends TrueLicenseManagerBase
                implements ConsumerLicenseManager, VendorLicenseManager {

            @Override
            public LicenseKeyGenerator generateKeyFrom(final License bean) throws LicenseManagementException {

                class TrueLicenseKeyGenerator implements LicenseKeyGenerator {

                    private final Object model = repositoryFactory().model();
                    private Decoder decoder;

                    @Override
                    public License license() throws LicenseManagementException {
                        return callChecked(() -> decoder().decode(licenseClass()));
                    }

                    @Override
                    public LicenseKeyGenerator saveTo(final Sink sink) throws LicenseManagementException {
                        callChecked(() -> {
                            codec().encoder(sink.map(compressionAndEncryption())).encode(model());
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
                                    .sign(repositoryFactory().controller(codec(), model), validatedBean());
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

                    private License duplicatedBean() throws Exception {
                        return memory().connect(codec()).clone(bean);
                    }
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

            void validate(Source source) throws Exception {
                validation().validate(decodeLicense(source));
            }

            License decodeLicense(Source source) throws Exception {
                return authenticate(source).decode(licenseClass());
            }

            Decoder authenticate(Source source) throws Exception {
                return authentication().verify(repositoryController(source));
            }

            RepositoryController repositoryController(Source source) throws Exception {
                return repositoryFactory().controller(codec(), repositoryModel(source));
            }

            Object repositoryModel(Source source) throws Exception {
                return codec()
                        .decoder(decryptedAndDecompressedSource(source))
                        .decode(repositoryFactory().modelClass());
            }

            Source decryptedAndDecompressedSource(Source source) {
                return source.map(compressionAndEncryption());
            }

            @Override
            public TrueUncheckedLicenseManager unchecked() {
                return new TrueUncheckedLicenseManager();
            }

            final class TrueUncheckedLicenseManager
                    extends TrueLicenseManagerBase
                    implements UncheckedVendorLicenseManager, UncheckedConsumerLicenseManager {

                @Override
                public TrueLicenseManager checked() {
                    return TrueLicenseManager.this;
                }

                @Override
                public TrueUncheckedLicenseManager unchecked() {
                    return this;
                }
            }
        }

        abstract class TrueLicenseManagerBase implements LicenseManagerMixin {

            @Override
            public LicenseManagerParameters parameters() {
                return TrueLicenseManagerParameters.this;
            }

            @Override
            public LicenseManagementContext context() {
                return TrueLicenseManagementContext.this;
            }
        }
    }

    final class TrueLicenseInitialization implements LicenseInitialization {

        @Obfuscate
        static final String DEFAULT_CONSUMER_TYPE = "User";

        /**
         * The canonical name prefix for X.500 principals.
         */
        @Obfuscate
        static final String CN_PREFIX = "CN=";

        @Override
        public void initialize(final License bean) {
            if (0 == bean.getConsumerAmount()) {
                bean.setConsumerAmount(1);
            }
            if (null == bean.getConsumerType()) {
                bean.setConsumerType(DEFAULT_CONSUMER_TYPE);
            }
            if (null == bean.getHolder()) {
                bean.setHolder(new X500Principal(CN_PREFIX + Messages.message(Messages.UNKNOWN)));
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
                throw new LicenseValidationException(message(Messages.CONSUMER_AMOUNT_IS_NOT_POSITIVE, bean.getConsumerAmount()));
            }
            if (null == bean.getConsumerType()) {
                throw new LicenseValidationException(Messages.message(Messages.CONSUMER_TYPE_IS_NULL));
            }
            if (null == bean.getHolder()) {
                throw new LicenseValidationException(Messages.message(Messages.HOLDER_IS_NULL));
            }
            if (null == bean.getIssued()) {
                throw new LicenseValidationException(Messages.message(Messages.ISSUED_IS_NULL));
            }
            if (null == bean.getIssuer()) {
                throw new LicenseValidationException(Messages.message(Messages.ISSUER_IS_NULL));
            }
            final Date now = now(); // don't trust the system clock!
            final Date notAfter = bean.getNotAfter();
            if (null != notAfter && now.after(notAfter)) {
                throw new LicenseValidationException(Messages.message(Messages.LICENSE_HAS_EXPIRED, notAfter));
            }
            final Date notBefore = bean.getNotBefore();
            if (null != notBefore && now.before(notBefore)) {
                throw new LicenseValidationException(Messages.message(Messages.LICENSE_IS_NOT_YET_VALID, notBefore));
            }
            if (!subject().equals(bean.getSubject())) {
                throw new LicenseValidationException(Messages.message(Messages.INVALID_SUBJECT, bean.getSubject(), subject()));
            }
        }
    }

    final class CheckedPasswordProtection implements PasswordProtection {

        final PasswordProtection protection;

        CheckedPasswordProtection(final PasswordProtection protection) {
            this.protection = protection;
        }

        @Override
        public Password password(final PasswordUsage usage) throws Exception {
            if (usage.equals(PasswordUsage.ENCRYPTION)) { // checks null
                passwordPolicy().check(protection);
            }
            return protection.password(usage);
        }
    }
}
