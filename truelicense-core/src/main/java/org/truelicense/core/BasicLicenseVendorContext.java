/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.api.*;
import org.truelicense.api.auth.Authentication;
import org.truelicense.api.codec.Codec;
import org.truelicense.api.crypto.Encryption;
import org.truelicense.api.io.BIOS;
import org.truelicense.api.io.Store;

/**
 * A basic context for license vendor applications alias license key tools.
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
final class BasicLicenseVendorContext<PasswordSpecification>
extends BasicLicenseApplicationContext<PasswordSpecification>
implements LicenseVendorContext<PasswordSpecification> {

    BasicLicenseVendorContext(BasicLicenseManagementContext<PasswordSpecification> context) {
        super(context);
    }

    @Override public final Codec codec() { return context().codec(); }

    @Override public License license() { return context().license(); }

    @Override public BasicLicenseVendorManagerBuilder manager() {
        return new BasicLicenseVendorManagerBuilder();
    }

    private LicenseVendorManager manager(final LicenseParameters parameters) {

        class Manager extends BasicLicenseManager
        implements LicenseVendorManager {

            final BasicLicenseVendorContext<PasswordSpecification> vc = BasicLicenseVendorContext.this;

            @Override public BIOS bios() { return vc.bios(); }
            @Override public LicenseVendorContext<PasswordSpecification> context() { return vc; }
            @Override public LicenseParameters parameters() { return parameters; }
            @Override public Store store() { throw new UnsupportedOperationException(); }
            @Override public String subject() { return vc.subject(); }
        }
        return new Manager();
    }

    LicenseVendorManager manager(
            Authentication authentication,
            Encryption encryption) {
        return manager(parameters(authentication, encryption));
    }

    final class BasicLicenseVendorManagerBuilder
    extends BasicLicenseManagerBuilder<BasicLicenseVendorManagerBuilder>
    implements LicenseVendorManagerBuilder<PasswordSpecification> {

        @Override
        public LicenseVendorManager build() {
            return manager(authentication.get(0), encryption.get(0));
        }
    }
}
