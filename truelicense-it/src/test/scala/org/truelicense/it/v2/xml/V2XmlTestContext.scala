/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.v2.xml

import javax.xml.bind._

import org.slf4j.LoggerFactory
import org.truelicense.api._
import org.truelicense.core.TrueLicenseManagementContext
import org.truelicense.it.core.ExtraData
import org.truelicense.it.v2.commons.V2TestContext
import org.truelicense.it.v2.xml.V2XmlTestContext.logger
import org.truelicense.v2.commons.auth.V2RepositoryModel
import org.truelicense.v2.xml.V2XmlLicenseApplicationContext

/** @author Christian Schlichtherle */
trait V2XmlTestContext extends V2TestContext {

  override final val managementContext =
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
      .context
      .subject("subject")
      .validation(new LicenseValidation {
          override def validate(bean: License) {
            logger debug ("Validated {}.", bean)
          }
        })
      .build
      .asInstanceOf[TrueLicenseManagementContext[V2RepositoryModel]]

  override def extraData = {
    val bean = new ExtraData
    bean.setMessage("This is some private extra data!")
    bean
  }
}

object V2XmlTestContext {

  private val logger = LoggerFactory getLogger classOf[V2XmlTestContext]
}
