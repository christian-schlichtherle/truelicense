/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.it.v2.xml

import net.java.truelicense.it.core.LicenseConsumerPerformance

/** @author Christian Schlichtherle */
object V2XmlLicenseConsumerPerformance
extends LicenseConsumerPerformance with V2XmlTestContext {
  def main(args: Array[String]): Unit = call()
}
