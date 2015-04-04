/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.it.v2.xml

import javax.xml.bind._

import net.java.truelicense.core._
import net.java.truelicense.core.auth.BasicRepository
import net.java.truelicense.it.core.ExtraData
import net.java.truelicense.it.v2.core.V2TestContext
import net.java.truelicense.it.v2.xml.V2XmlTestContext.logger
import org.slf4j.LoggerFactory

/** @author Christian Schlichtherle */
trait V2XmlTestContext extends V2TestContext {

  override final val managementContext =
    new V2XmlLicenseManagementContext("subject") {
      override def license = super.license
      override def now = super.now
      override def initialization = {
        val initialization = super.initialization
        new LicenseInitialization {
          override def initialize(bean: License) {
            initialization.initialize(bean)
          }
        }
      }
      override def validation = {
        val validation = super.validation
        new LicenseValidation {
          override def validate(bean: License) {
            validation.validate(bean)
            logger debug ("Validated {}.", bean)
          }
        }
      }
      override def codec = super.codec
      override def policy = super.policy
      override def newContext() = {
        try {
          JAXBContext newInstance (
            classOf[License],
            classOf[ExtraData],
            classOf[BasicRepository])
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

/** @author Christian Schlichtherle */
private object V2XmlTestContext {
  private val logger = LoggerFactory getLogger classOf[V2XmlTestContext]
}
