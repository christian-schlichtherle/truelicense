/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.v1

import org.truelicense.api.crypto.PbeParameters
import org.truelicense.it.core.TestContext
import org.truelicense.it.core.TestContext.test1234
import org.truelicense.v1.base.V1Encryption

/** @author Christian Schlichtherle */
trait V1EncryptionTestContext { this: TestContext =>
  final override def transformation = new V1Encryption(new PbeParameters {
      def password = test1234.toCharArray
      def algorithm = "PBEwithMD5andDES"
    })
}
