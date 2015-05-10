/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.v1

import org.truelicense.api.crypto.PbeParameters
import org.truelicense.it.core.TestContext
import org.truelicense.v1.crypto.V1Encryption

/** @author Christian Schlichtherle */
trait V1EncryptionTestContext { context: TestContext[_] =>
  final override def transformation = new V1Encryption(new PbeParameters {
      def algorithm = "PBEwithMD5andDES"
      def protection = test1234
  })
}
