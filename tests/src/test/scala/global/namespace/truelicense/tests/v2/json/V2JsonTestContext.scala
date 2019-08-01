/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.v2.json

import global.namespace.truelicense.api.LicenseManagementContextBuilder
import global.namespace.truelicense.tests.v2.core.V2TestContext
import global.namespace.truelicense.v2.json.V2Json

trait V2JsonTestContext extends V2TestContext {

  //noinspection ScalaDeprecation
  final override def managementContextBuilder: LicenseManagementContextBuilder = V2Json.builder

  lazy val licenseDtoClass: Class[V2JsonLicenseDTO] = classOf[V2JsonLicenseDTO]
}
