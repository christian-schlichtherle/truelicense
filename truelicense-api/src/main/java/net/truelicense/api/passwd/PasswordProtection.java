/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.passwd;

/**
 * A factory for consistent {@link Password}s.
 * Use this interface with the try-with-resources statement to ensure that the
 * password gets erased after use:
 * <pre>{@code
 *  import net.truelicense.api.passwd.*;
 *  import java.security.*;
 *
 *  class Foo {
 *
 *      KeyStore loadKeyStore(String resourceName, PasswordProtection protection) throws Exception {
 *          try (Password password = protection.password(PasswordUsage.READ);
 *               InputStream in = getClass().getClassLoader().getResourceAsStream(resourceName)) {
 *              KeyStore keyStore = KeyStore.getInstance("JKS");
 *              keyStore.load(in, password.characters());
 *              return keyStore;
 *          }
 *      }
 *  }
 * }</pre>
 *
 * @author Christian Schlichtherle
 */
public interface PasswordProtection {

    /**
     * Returns a new, yet consistent password object.
     *
     * @throws Exception if providing access to the password is not possible.
     */
    Password password(PasswordUsage usage) throws Exception;
}
