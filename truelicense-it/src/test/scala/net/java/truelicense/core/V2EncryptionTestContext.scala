/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.core

import net.java.truelicense.core.TestContext.test1234
import net.java.truelicense.core.crypto.PbeParameters

/** @author Christian Schlichtherle */
trait V2EncryptionTestContext { this: TestContext =>
  final override def transformation = new V2Encryption(new PbeParameters {
      def password = test1234.toCharArray
      def algorithm = "PBEwithSHA1andDESede"
    })
}
