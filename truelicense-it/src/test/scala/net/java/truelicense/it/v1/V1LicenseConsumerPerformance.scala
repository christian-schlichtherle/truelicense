package net.java.truelicense.it.v1

import net.java.truelicense.it.core.LicenseConsumerPerformance

/** @author Christian Schlichtherle */
object V1LicenseConsumerPerformance
extends LicenseConsumerPerformance with V1TestContext {
  def main(args: Array[String]) = call ()
}
