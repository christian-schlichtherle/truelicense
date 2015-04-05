/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.v1

import org.truelicense.it.core.LicenseVendorPerformance

/** @author Christian Schlichtherle */
object V1LicenseVendorPerformance
extends LicenseVendorPerformance with V1TestContext {
  def main(args: Array[String]) = call ()
}
