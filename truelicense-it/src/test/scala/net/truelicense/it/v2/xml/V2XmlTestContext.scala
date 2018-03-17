/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.v2.xml

import javax.xml.bind._
import net.truelicense.api.{License, LicenseManagementContextBuilder}
import net.truelicense.it.core.ExtraData
import net.truelicense.it.v2.commons.V2TestContext
import net.truelicense.v2.commons.auth.V2RepositoryModel
import net.truelicense.v2.xml.V2XmlLicenseApplicationContext
import net.truelicense.v2.xml.codec.XMLCodec

/** @author Christian Schlichtherle */
trait V2XmlTestContext extends V2TestContext {

  final val applicationContext = new V2XmlLicenseApplicationContext

  override def managementContextBuilder: LicenseManagementContextBuilder = {
    super.managementContextBuilder.codec(new XMLCodec(
      try {
        JAXBContext.newInstance(
          classOf[License],
          classOf[ExtraData],
          classOf[V2RepositoryModel]
        )
      } catch {
        case ex: JAXBException => throw new AssertionError(ex)
      }
    ))
  }

  override def extraData: AnyRef = {
    val bean = new ExtraData
    bean.setMessage("This is some private extra data!")
    bean
  }
}
