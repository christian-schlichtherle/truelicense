/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.core;

import global.namespace.truelicense.api.License;

import java.util.Locale;
import java.util.Objects;

/**
 * A stub class for any license.
 */
public abstract class LicenseStub implements License {

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof License)) {
            return false;
        }
        final License that = (License) obj;
        return that.canEqual(this) &&
                this.getConsumerAmount() == that.getConsumerAmount() &&
                Objects.equals(this.getConsumerType(), that.getConsumerType()) &&
                Objects.equals(this.getExtra(), that.getExtra()) &&
                Objects.equals(this.getHolder(), that.getHolder()) &&
                Objects.equals(this.getInfo(), that.getInfo()) &&
                Objects.equals(this.getIssued(), that.getIssued()) &&
                Objects.equals(this.getIssuer(), that.getIssuer()) &&
                Objects.equals(this.getNotAfter(), that.getNotAfter()) &&
                Objects.equals(this.getNotBefore(), that.getNotBefore()) &&
                Objects.equals(this.getSubject(), that.getSubject());
    }

    @Override
    public int hashCode() {
        int c = 17;
        c = 31 * c + getConsumerAmount();
        c = 31 * c + Objects.hashCode(getConsumerType());
        c = 31 * c + Objects.hashCode(getExtra());
        c = 31 * c + Objects.hashCode(getHolder());
        c = 31 * c + Objects.hashCode(getInfo());
        c = 31 * c + Objects.hashCode(getIssued());
        c = 31 * c + Objects.hashCode(getIssuer());
        c = 31 * c + Objects.hashCode(getNotAfter());
        c = 31 * c + Objects.hashCode(getNotBefore());
        c = 31 * c + Objects.hashCode(getSubject());
        return c;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH,
                "%s@%x[subject=%s, holder=%s, issuer=%s, issued=%tc, notBefore=%tc, notAfter=%tc, consumerType=%s, consumerAmount=%d, info=%s]",
                getClass().getName(),
                hashCode(),
                literal(getSubject()),
                literal(getHolder()),
                literal(getIssuer()),
                getIssued(),
                getNotBefore(),
                getNotAfter(),
                literal(getConsumerType()),
                getConsumerAmount(),
                literal(getInfo()));
    }

    private static String literal(final Object obj) {
        if (null == obj) {
            return null;
        }
        final String s = obj.toString();
        return '"' +
                s.replace("\\", "\\\\")
                        .replace("\"", "\\\"") +
                '"';
    }
}
