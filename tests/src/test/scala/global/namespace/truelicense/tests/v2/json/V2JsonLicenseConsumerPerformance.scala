/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.v2.json

import global.namespace.truelicense.tests.core.LicenseConsumerPerformance

object V2JsonLicenseConsumerPerformance extends LicenseConsumerPerformance with V2JsonTestContext {

  def main(args: Array[String]): Unit = run()
}
