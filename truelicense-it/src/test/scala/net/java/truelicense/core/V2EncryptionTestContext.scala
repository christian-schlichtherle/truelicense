/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.core

import net.java.truelicense.core.crypto.PbeParameters
import net.java.truelicense.it.TestContext
import net.java.truelicense.it.TestContext.test1234

/** @author Christian Schlichtherle */
trait V2EncryptionTestContext { this: TestContext =>
  final override def transformation = new V2Encryption(new PbeParameters {
      def password = test1234.toCharArray
      def algorithm = "PBEwithSHA1andDESede"
    })
}
