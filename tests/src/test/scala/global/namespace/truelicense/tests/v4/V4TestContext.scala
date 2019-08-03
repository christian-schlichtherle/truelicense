/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.v4

import global.namespace.truelicense.api.LicenseManagementContextBuilder
import global.namespace.truelicense.tests.core.{ExtraMapTestContext, TestContext}
import global.namespace.truelicense.tests.v4.V4TestContext._
import global.namespace.truelicense.v4.V4

trait V4TestContext extends TestContext with ExtraMapTestContext {

  final def managementContextBuilder: LicenseManagementContextBuilder = V4.builder

  protected def chainedConsumerKeystoreName: String = prefix + "chained-public.pkcs12"

  protected def chainedVendorKeystoreName: String = prefix + "chained-private.pkcs12"

  protected def consumerKeystoreName: String = prefix + "public.pkcs12"

  protected def ftpConsumerKeystoreName: String = prefix + "ftp.pkcs12"

  protected def vendorKeystoreName: String = prefix + "private.pkcs12"
}

object V4TestContext {

  private def prefix = classOf[V4TestContext].getPackage.getName.replace('.', '/') + '/'
}
