/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

/**
 * A strategy for composing two {@link LicenseInitialization}s or
 * {@link LicenseValidation}s.
 *
 * @author Christian Schlichtherle
 */
public enum LicenseFunctionComposition {

    override {

        @Override
        public LicenseInitialization compose(LicenseInitialization first, LicenseInitialization second) {
            return first;
        }

        @Override
        public LicenseValidation compose(LicenseValidation first, LicenseValidation second) {
            return first;
        }
    },

    decorate {

        @Override
        public LicenseInitialization compose(final LicenseInitialization first, final LicenseInitialization second) {
            return new LicenseInitialization() {
                @Override
                public void initialize(final License bean) {
                    first.initialize(bean);
                    second.initialize(bean);
                }
            };
        }

        @Override
        public LicenseValidation compose(final LicenseValidation first, final LicenseValidation second) {
            return new LicenseValidation() {
                @Override
                public void validate(final License bean) throws LicenseValidationException {
                    first.validate(bean);
                    second.validate(bean);
                }
            };
        }
    };

    public abstract LicenseInitialization compose(LicenseInitialization first, LicenseInitialization second);

    public abstract LicenseValidation compose(LicenseValidation first, LicenseValidation second);
}
