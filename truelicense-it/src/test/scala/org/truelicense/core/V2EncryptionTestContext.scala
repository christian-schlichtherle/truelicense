/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core

import org.truelicense.api.crypto.PbeParameters
import org.truelicense.it.core.TestContext
import TestContext.test1234
import org.truelicense.it.core.TestContext

/** @author Christian Schlichtherle */
trait V2EncryptionTestContext { this: TestContext =>
  final override def transformation = new V2Encryption(new PbeParameters {
      def password = test1234.toCharArray
      def algorithm = "PBEwithSHA1andDESede"
    })
}
