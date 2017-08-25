/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.it.v2.xml

import java.util.Date
import javax.xml.bind._

import net.java.truelicense.core._
import net.java.truelicense.core.auth.BasicRepository
import net.java.truelicense.core.codec.{Codec, JaxbCodec}
import net.java.truelicense.core.policy.PasswordPolicy
import net.java.truelicense.it.core.ExtraData
import net.java.truelicense.it.v2.core.V2TestContext
import net.java.truelicense.it.v2.xml.V2XmlTestContext.logger
import org.slf4j.LoggerFactory

/** @author Christian Schlichtherle */
trait V2XmlTestContext extends V2TestContext {

  final val managementContext: LicenseManagementContext =
    new V2XmlLicenseManagementContext("subject") {
      override def license: License = super.license
      override def now: Date = super.now
      override def initialization: LicenseInitialization = {
        val initialization = super.initialization
        new LicenseInitialization {
          def initialize(bean: License) {
            initialization.initialize(bean)
          }
        }
      }
      override def validation: LicenseValidation = {
        val validation = super.validation
        new LicenseValidation {
          def validate(bean: License) {
            validation.validate(bean)
            logger debug ("Validated {}.", bean)
          }
        }
      }
      override def codec: JaxbCodec = super.codec
      override def policy: PasswordPolicy = super.policy
      override def newContext(): JAXBContext = {
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

  override def extraData: AnyRef = {
    val bean = new ExtraData
    bean.setMessage("This is some private extra data!")
    bean
  }
}

/** @author Christian Schlichtherle */
private object V2XmlTestContext {
  private val logger = LoggerFactory getLogger classOf[V2XmlTestContext]
}
