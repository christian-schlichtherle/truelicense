/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.api.License;
import org.truelicense.api.LicenseParameters;
import org.truelicense.api.LicenseVendorContext;
import org.truelicense.api.LicenseVendorManager;
import org.truelicense.api.auth.Authentication;
import org.truelicense.api.codec.Codec;
import org.truelicense.api.crypto.Encryption;
import org.truelicense.api.io.BIOS;
import org.truelicense.api.io.Source;
import org.truelicense.api.io.Store;
import org.truelicense.spi.misc.Option;

import javax.annotation.ParametersAreNullableByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;

/**
 * A basic context for license vendor applications alias license key tools.
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
@Immutable
final class BasicLicenseVendorContext<PasswordSpecification>
extends BasicLicenseApplicationContext<PasswordSpecification>
implements LicenseVendorContext<PasswordSpecification> {

    BasicLicenseVendorContext(BasicLicenseManagementContext<PasswordSpecification> context) {
        super(context);
    }

    @Override public License license() { return context().license(); }

    @Override public final Codec codec() { return context().codec(); }

    LicenseVendorManager manager(
            Authentication authentication,
            Encryption encryption) {
        return manager(parameters(authentication, encryption));
    }

    private LicenseVendorManager manager(final LicenseParameters lp) {

        class Manager extends BasicLicenseManager implements LicenseVendorManager {

            final BasicLicenseVendorContext<PasswordSpecification> vc = BasicLicenseVendorContext.this;

            @Override public BIOS bios() { return vc.bios(); }
            @Override public LicenseVendorContext<PasswordSpecification> context() { return vc; }
            @Override public LicenseParameters parameters() { return lp; }
            @Override public Store store() { throw new UnsupportedOperationException(); }
            @Override public String subject() { return vc.subject(); }
        }
        return new Manager();
    }

    @SuppressWarnings("PackageVisibleField")
    @Override public ManagerBuilder<PasswordSpecification> manager() {

        @NotThreadSafe
        @ParametersAreNullableByDefault
        class ManagerConfiguration implements ManagerBuilder<PasswordSpecification> {

            final BasicLicenseVendorContext<PasswordSpecification> vc = BasicLicenseVendorContext.this;

            List<Authentication> optionalAuthentication = Option.none();
            List<Encryption> optionalEncryption = Option.none();

            @Override
            public LicenseVendorManager build() {
                return vc.manager(optionalAuthentication.get(0), optionalEncryption.get(0));
            }

            @Override
            public ManagerBuilder<PasswordSpecification> authentication(final Authentication authentication) {
                this.optionalAuthentication = Option.of(authentication);
                return this;
            }

            @Override
            public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> keyStore() {

                @ParametersAreNullableByDefault
                class KeyStoreConfiguration implements KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> {
                    List<String> optionalStoreType = Option.none(), optionalAlias = Option.none();
                    List<Source> optionalSource = Option.none();
                    List<PasswordSpecification> optionalStorePassword = Option.none(), optionalKeyPassword = Option.none();

                    @Override
                    public ManagerBuilder<PasswordSpecification> inject() {
                        return authentication(
                                vc.keyStore(optionalSource, optionalStoreType, optionalStorePassword.get(0), optionalAlias.get(0), optionalKeyPassword));
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> storeType(final String storeType) {
                        this.optionalStoreType = Option.of(storeType);
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> loadFrom(final Source source) {
                        this.optionalSource = Option.of(source);
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> loadFromResource(String name) {
                        return loadFrom(vc.resource(name));
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> storePassword(final PasswordSpecification storePassword) {
                        this.optionalStorePassword = Option.of(storePassword);
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> alias(final String alias) {
                        this.optionalAlias = Option.of(alias);
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> keyPassword(final PasswordSpecification keyPassword) {
                        this.optionalKeyPassword = Option.of(keyPassword);
                        return this;
                    }
                }
                return new KeyStoreConfiguration();
            }

            @Override
            public ManagerBuilder<PasswordSpecification> encryption(final Encryption encryption) {
                this.optionalEncryption = Option.of(encryption);
                return this;
            }

            @Override
            public PbeInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> encryption() {

                @ParametersAreNullableByDefault
                class EncryptionConfiguration implements PbeInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> {
                    List<String> optionalAlgorithm = Option.none();
                    List<PasswordSpecification> optionalPassword = Option.none();

                    @Override
                    public ManagerBuilder<PasswordSpecification> inject() {
                        return encryption(vc.pbe(optionalAlgorithm, optionalPassword.get(0)));
                    }

                    @Override
                    public PbeInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> algorithm(final String algorithm) {
                        this.optionalAlgorithm = Option.of(algorithm);
                        return this;
                    }

                    @Override
                    public PbeInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> password(final PasswordSpecification password) {
                        this.optionalPassword = Option.of(password);
                        return this;
                    }
                }
                return new EncryptionConfiguration();
            }
        }
        return new ManagerConfiguration();
    }
}
