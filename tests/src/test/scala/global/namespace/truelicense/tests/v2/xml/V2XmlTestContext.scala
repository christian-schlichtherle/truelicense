/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.v2.xml

import global.namespace.truelicense.api.LicenseManagementContextBuilder
import global.namespace.truelicense.tests.core.{ExtraBean, ExtraBeanTestContext}
import global.namespace.truelicense.tests.v2.core.V2TestContext
import global.namespace.truelicense.v2.xml.{V2Xml, V2XmlCodecFactory}
import javax.xml.bind.JAXBContext

trait V2XmlTestContext extends V2TestContext with ExtraBeanTestContext {

  final def managementContextBuilder: LicenseManagementContextBuilder = {
    V2Xml.builder.codecFactory(
      new V2XmlCodecFactory {

        override def jaxbContext: JAXBContext = jaxbContext(classesToBeBound :+ classOf[ExtraBean]: _*)
      }
    )
  }
}
