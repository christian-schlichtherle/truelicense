package org.truelicense.api.passwd;

/**
 * A provider for a password protection.
 *
 * @param <PasswordSpecification> the generic password specification type.
 * @author Christian Schlichtherle
 */
public interface PasswordProtectionProvider<PasswordSpecification> {

    /** Returns a password protection for the given password specification. */
    PasswordProtection protection(PasswordSpecification specification);
}
