/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.api.LicenseVendorContext;
import org.truelicense.api.LicenseVendorManager;
import org.truelicense.api.LicenseVendorManagerBuilder;
import org.truelicense.api.auth.Authentication;
import org.truelicense.api.io.Store;
import org.truelicense.api.io.Transformation;

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
final class TrueLicenseVendorContext<Model, PasswordSpecification>
extends TrueLicenseApplicationContext<Model, PasswordSpecification>
implements LicenseVendorContext<PasswordSpecification> {

    TrueLicenseVendorContext(TrueLicenseManagementContext<Model, PasswordSpecification> context) {
        super(context);
    }

    @Override public TrueLicenseVendorManagerBuilder manager() {
        return new TrueLicenseVendorManagerBuilder();
    }

    private LicenseVendorManager manager(final TrueLicenseParameters parameters) {

        class Manager extends TrueLicenseManager
        implements LicenseVendorManager {

            final TrueLicenseVendorContext<Model, PasswordSpecification> vc = TrueLicenseVendorContext.this;

            @Override
            public LicenseVendorContext<PasswordSpecification> context() { return vc; }

            @Override
            public TrueLicenseParameters parameters() { return parameters; }

            @Override
            public Store store() { throw new UnsupportedOperationException(); }
        }

        return new Manager();
    }

    LicenseVendorManager manager(
            Authentication authentication,
            Transformation encryption) {
        return manager(parameters(authentication, encryption));
    }

    final class TrueLicenseVendorManagerBuilder
    extends TrueLicenseManagerBuilder<TrueLicenseVendorManagerBuilder>
    implements LicenseVendorManagerBuilder<PasswordSpecification> {

        @Override
        public LicenseVendorManager build() {
            return manager(authentication.get(0), encryption.get(0));
        }
    }
}
