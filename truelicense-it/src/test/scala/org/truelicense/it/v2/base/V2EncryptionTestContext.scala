/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.v2.base

import org.truelicense.api.crypto.PbeParameters
import org.truelicense.it.core.TestContext
import org.truelicense.it.core.TestContext.test1234
import org.truelicense.v2.base.V2Encryption

/** @author Christian Schlichtherle */
trait V2EncryptionTestContext { this: TestContext =>
  final override def transformation = new V2Encryption(new PbeParameters {
      def algorithm = "PBEwithSHA1andDESede"
      def protection = test1234
  })
}
