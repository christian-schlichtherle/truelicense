/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.api.LicenseVendorContext;
import org.truelicense.api.LicenseVendorManager;
import org.truelicense.api.LicenseVendorManagerBuilder;

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

    @Override
    public LicenseVendorManagerBuilder<PasswordSpecification> manager() {
        return new TrueLicenseVendorManagerBuilder();
    }

    final class TrueLicenseVendorManagerBuilder
    extends TrueLicenseManagerBuilder<TrueLicenseVendorManagerBuilder>
    implements LicenseVendorManagerBuilder<PasswordSpecification> {

        @Override
        public LicenseVendorManager build() {
            return new TrueLicenseManager<>(new TrueLicenseParameters(this));
        }
    }
}
