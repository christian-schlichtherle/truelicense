/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.core

/** @author Christian Schlichtherle */
abstract class LicenseCodecTestSuite
  extends CodecTestSuite { this: TestContext[_] =>

  final override def artifact = license
}
