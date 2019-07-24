/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.v2.xml

import global.namespace.truelicense.api.{License, LicenseManagementContextBuilder}
import global.namespace.truelicense.tests.core.ExtraData
import global.namespace.truelicense.tests.v2.core.V2TestContext
import global.namespace.truelicense.v2.core.auth.V2RepositoryModel
import global.namespace.truelicense.v2.xml.V2Xml
import javax.xml.bind._

/** @author Christian Schlichtherle */
trait V2XmlTestContext extends V2TestContext {

  final override def managementContextBuilder: LicenseManagementContextBuilder = {
    V2Xml builder JAXBContext.newInstance(classOf[License], classOf[ExtraData], classOf[V2RepositoryModel])
  }

  override def extraData: AnyRef = {
    val bean = new ExtraData
    bean.setMessage("This is some private extra data!")
    bean
  }
}
