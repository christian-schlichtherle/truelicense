/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.v4

import global.namespace.truelicense.api.LicenseManagementContextBuilder
import global.namespace.truelicense.tests.core.{ExtraMapTestContext, TestContext}
import global.namespace.truelicense.v4.V4

trait V4TestContext extends TestContext with ExtraMapTestContext {

  final def managementContextBuilder: LicenseManagementContextBuilder = V4.builder

  protected final lazy val prefix = classOf[V4TestContext].getPackage.getName.replace('.', '/') + '/'

  protected final lazy val postfix: String = ".pkcs12"
}
