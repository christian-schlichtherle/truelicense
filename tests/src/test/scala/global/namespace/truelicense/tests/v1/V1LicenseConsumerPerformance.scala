/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.v1

import global.namespace.truelicense.tests.core.LicenseConsumerPerformance

object V1LicenseConsumerPerformance extends LicenseConsumerPerformance with V1TestContext {

  def main(args: Array[String]): Unit = run()
}
