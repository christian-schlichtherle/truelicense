/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core

import net.java.truelicense.core.crypto.PbeParameters
import net.java.truelicense.it.core.TestContext
import TestContext.test1234
import net.java.truelicense.it.core.TestContext

/** @author Christian Schlichtherle */
trait V1EncryptionTestContext { this: TestContext =>
  final override def transformation = new V1Encryption(new PbeParameters {
      def password: Array[Char] = test1234.toCharArray
      def algorithm: String = "PBEwithMD5andDES"
    })
}
