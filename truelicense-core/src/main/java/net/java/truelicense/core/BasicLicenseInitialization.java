/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core;

import java.util.Date;
import javax.annotation.concurrent.Immutable;
import javax.security.auth.x500.X500Principal;
import static net.java.truelicense.core.Messages.*;
import net.java.truelicense.core.util.*;
import net.java.truelicense.obfuscate.Obfuscate;

/**
 * A basic license initialization.
 * <p>
 * Unless stated otherwise, all no-argument methods need to return consistent
 * objects so that caching them is not required.
 * A returned object is considered to be consistent if it compares
 * {@linkplain Object#equals(Object) equal} or at least behaves identical to
 * any previously returned object.
 *
 * @author Christian Schlichtherle
 */
@Immutable
final class BasicLicenseInitialization
implements  ContextProvider<LicenseManagementContext>,
            Clock,
            LicenseInitialization,
            LicenseSubjectProvider {

    @Obfuscate private static final String DEFAULT_CONSUMER_TYPE = "User";

    /** The canonical name prefix for X.500 principals. */
    @Obfuscate private static final String CN_PREFIX = "CN=";

    /** The message key for the canonical name of an unknown user. */
    @Obfuscate static final String UNKNOWN = "unknown";

    private final LicenseManagementContext context;

    BasicLicenseInitialization(final BasicLicenseManagementContext context) {
        assert null != context;
        this.context = context;
    }

    @Override public LicenseManagementContext context() { return context; }

    @Override public String subject() { return context().subject(); }

    @Override public Date now() { return context().now(); }

    /**
     * Initializes undefined properties of the given license bean with default
     * values.
     *
     * @param bean the license bean to initialize.
     */
    @Override public void initialize(final License bean) {
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
}
