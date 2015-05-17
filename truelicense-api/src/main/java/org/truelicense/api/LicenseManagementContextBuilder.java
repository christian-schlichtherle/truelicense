/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

import org.truelicense.api.misc.Builder;
import org.truelicense.api.misc.Clock;

/**
 * @author Christian Schlichtherle
 */
public interface LicenseManagementContextBuilder
extends Builder<LicenseManagementContext> {

    LicenseManagementContextBuilder authorization(LicenseManagementAuthorization authorization);

    LicenseManagementContextBuilder clock(Clock clock);

    LicenseManagementContextBuilder initialization(LicenseInitialization initialization);

    LicenseManagementContextBuilder initializationComposition(LicenseFunctionComposition composition);

    /**
     * Sets the license subject.
     * The provided string should get computed on demand from an obfuscated
     * form, e.g. by annotating a constant string value with the
     * {@link org.truelicense.obfuscate.Obfuscate} annotation and processing it
     * with the TrueLicense Maven Plugin.
     *
     * @param subject the license subject, i.e. a product name with an optional
     *                version range, e.g. {@code MyApp 1}.
     */
    LicenseManagementContextBuilder subject(String subject);

    LicenseManagementContextBuilder validation(LicenseValidation validation);

    LicenseManagementContextBuilder validationComposition(LicenseFunctionComposition composition);
}
