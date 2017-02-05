/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.it.v1

import net.java.truelicense.it.core.LicenseVendorPerformance

/** @author Christian Schlichtherle */
object V1LicenseVendorPerformance
extends LicenseVendorPerformance with V1TestContext {
  def main(args: Array[String]) = call ()
}
