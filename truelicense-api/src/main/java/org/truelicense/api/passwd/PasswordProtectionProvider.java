package org.truelicense.api.passwd;

/**
 * A provider for a password protection for a given password specification.
 *
 * @param <PasswordSpecification> the generic password specification type.
 * @author Christian Schlichtherle
 */
public interface PasswordProtectionProvider<PasswordSpecification> {

    /** Returns the password protection for the given password specification. */
    PasswordProtection protection(PasswordSpecification specification);
}
