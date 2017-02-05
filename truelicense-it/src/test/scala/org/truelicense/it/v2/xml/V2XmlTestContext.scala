/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.v2.xml

import javax.xml.bind._

import org.truelicense.api._
import org.truelicense.it.core.ExtraData
import org.truelicense.it.v2.commons.V2TestContext
import org.truelicense.v2.commons.auth.V2RepositoryModel
import org.truelicense.v2.xml.V2XmlLicenseApplicationContext

/** @author Christian Schlichtherle */
trait V2XmlTestContext extends V2TestContext {

  override final val applicationContext =
    new V2XmlLicenseApplicationContext {
      override def newJaxbContext() = {
        try {
          JAXBContext newInstance (
            classOf[License],
            classOf[ExtraData],
            classOf[V2RepositoryModel])
        } catch {
          case ex: JAXBException => throw new AssertionError(ex)
        }
      }
    }

  override def extraData = {
    val bean = new ExtraData
    bean.setMessage("This is some private extra data!")
    bean
  }
}
