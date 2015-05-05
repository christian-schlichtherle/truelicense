/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.api.LicenseConsumerContext;
import org.truelicense.api.LicenseConsumerManager;
import org.truelicense.api.LicenseConsumerManagerBuilder;

/**
 * A basic context for license consumer applications.
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
final class TrueLicenseConsumerContext<Model, PasswordSpecification>
extends TrueLicenseApplicationContext<Model, PasswordSpecification>
implements LicenseConsumerContext<PasswordSpecification> {

    TrueLicenseConsumerContext(TrueLicenseManagementContext<Model, PasswordSpecification> context) {
        super(context);
    }

    @Override
    public TrueLicenseConsumerManagerBuilder manager() {
        return new TrueLicenseConsumerManagerBuilder();
    }

    class TrueLicenseConsumerManagerBuilder
    extends TrueLicenseManagerBuilder<TrueLicenseConsumerManagerBuilder>
    implements LicenseConsumerManagerBuilder<PasswordSpecification> {

        @Override
        public LicenseConsumerManager build() {
            final TrueLicenseParameters lp = new TrueLicenseParameters(this);
            return parent.isEmpty()
                    ? new TrueLicenseCachingManager<>(lp)
                    : new TrueLicenseChildManager<>(lp);
        }

        @Override
        public TrueLicenseConsumerManagerBuilder inject() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ParentTrueLicenseConsumerManagerBuilder parent() {
            return new ParentTrueLicenseConsumerManagerBuilder();
        }

        final class ParentTrueLicenseConsumerManagerBuilder
        extends TrueLicenseConsumerManagerBuilder {

            @Override
            public TrueLicenseConsumerManagerBuilder inject() {
                return TrueLicenseConsumerManagerBuilder.this.parent(build());
            }
        }
    }
}
