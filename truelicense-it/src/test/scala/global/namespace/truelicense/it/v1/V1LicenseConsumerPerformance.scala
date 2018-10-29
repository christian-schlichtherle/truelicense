/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package global.namespace.truelicense.it.v1

import global.namespace.truelicense.it.core.LicenseConsumerPerformance

/** @author Christian Schlichtherle */
object V1LicenseConsumerPerformance
extends LicenseConsumerPerformance with V1TestContext {
  def main(args: Array[String]): Unit = call()
}
