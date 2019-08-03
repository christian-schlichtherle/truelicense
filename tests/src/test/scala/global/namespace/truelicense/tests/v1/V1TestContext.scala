/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.v1

import global.namespace.truelicense.api.LicenseManagementContextBuilder
import global.namespace.truelicense.tests.core.{ExtraBeanTestContext, TestContext}
import global.namespace.truelicense.tests.v1.V1TestContext._
import global.namespace.truelicense.v1.V1

trait V1TestContext extends TestContext with ExtraBeanTestContext {

  final def managementContextBuilder: LicenseManagementContextBuilder = V1.builder

  protected def chainedConsumerKeystoreName: String = prefix + "chained-public.jks"

  protected def chainedVendorKeystoreName: String = prefix + "chained-private.jks"

  protected def consumerKeystoreName: String = prefix + "public.jks"

  protected def ftpConsumerKeystoreName: String = prefix + "ftp.jks"

  protected def vendorKeystoreName: String = prefix + "private.jks"
}

object V1TestContext {

  private def prefix = classOf[V1TestContext].getPackage.getName.replace('.', '/') + '/'
}
