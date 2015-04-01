/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.core;

import java.util.Date;
import javax.annotation.concurrent.Immutable;
import static net.java.truelicense.core.Messages.*;
import net.java.truelicense.core.util.*;
import net.java.truelicense.obfuscate.Obfuscate;

/**
 * A basic license validation.
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
final class BasicLicenseValidation
implements  ContextProvider<LicenseManagementContext>,
            Clock,
            LicenseSubjectProvider,
            LicenseValidation {

    @Obfuscate
    static final String INVALID_SUBJECT = "invalidSubject";

    @Obfuscate
    static final String HOLDER_IS_NULL = "holderIsNull";

    @Obfuscate
    static final String ISSUER_IS_NULL = "issuerIsNull";

    @Obfuscate
    static final String ISSUED_IS_NULL = "issuedIsNull";

    @Obfuscate
    static final String LICENSE_IS_NOT_YET_VALID = "licenseIsNotYetValid";

    @Obfuscate
    static final String LICENSE_HAS_EXPIRED = "licenseHasExpired";

    @Obfuscate
    static final String CONSUMER_TYPE_IS_NULL = "consumerTypeIsNull";

    @Obfuscate
    static final String CONSUMER_AMOUNT_IS_NOT_POSITIVE = "consumerAmountIsNotPositive";

    private final LicenseManagementContext context;

    BasicLicenseValidation(final LicenseManagementContext context) {
        assert null != context;
        this.context = context;
    }

    @Override public LicenseManagementContext context() { return context; }

    @Override public String subject() { return context().subject(); }

    @Override public Date now() { return context().now(); }

    @Override
    public void validate(final License bean) throws LicenseValidationException {
        if (0 >= bean.getConsumerAmount())
            throw new LicenseValidationException(message(CONSUMER_AMOUNT_IS_NOT_POSITIVE, bean.getConsumerAmount()));
        if (null == bean.getConsumerType())
            throw new LicenseValidationException(message(CONSUMER_TYPE_IS_NULL));
        if (null == bean.getHolder())
            throw new LicenseValidationException(message(HOLDER_IS_NULL));
        if (null == bean.getIssued())
            throw new LicenseValidationException(message(ISSUED_IS_NULL));
        if (null == bean.getIssuer())
            throw new LicenseValidationException(message(ISSUER_IS_NULL));
        final Date now = now(); // don't trust the system clock!
        final Date notAfter = bean.getNotAfter();
        if (null != notAfter && now.after(notAfter))
            throw new LicenseValidationException(message(LICENSE_HAS_EXPIRED, notAfter));
        final Date notBefore = bean.getNotBefore();
        if (null != notBefore && now.before(notBefore))
            throw new LicenseValidationException(message(LICENSE_IS_NOT_YET_VALID, notBefore));
        if (!subject().equals(bean.getSubject()))
            throw new LicenseValidationException(message(INVALID_SUBJECT, bean.getSubject(), subject()));
    }
}
