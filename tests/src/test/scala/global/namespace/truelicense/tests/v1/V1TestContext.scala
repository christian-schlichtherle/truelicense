/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.v1

import global.namespace.truelicense.api.LicenseManagementContextBuilder
import global.namespace.truelicense.tests.core.{ExtraBeanTestContext, TestContext}
import global.namespace.truelicense.v1.V1

trait V1TestContext extends TestContext with ExtraBeanTestContext {

  final def managementContextBuilder: LicenseManagementContextBuilder = V1.builder

  protected final lazy val prefix: String = classOf[V1TestContext].getPackage.getName.replace('.', '/') + '/'

  protected final lazy val postfix: String = ".jks"
}
