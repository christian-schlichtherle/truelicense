/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package global.namespace.truelicense.it.v1

import global.namespace.truelicense.it.core.LicenseVendorPerformance

/** @author Christian Schlichtherle */
object V1LicenseVendorPerformance
extends LicenseVendorPerformance with V1TestContext {
  def main(args: Array[String]): Unit = call()
}
