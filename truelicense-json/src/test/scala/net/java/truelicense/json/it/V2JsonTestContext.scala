/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.json.it

import net.java.truelicense.core._
import net.java.truelicense.core.it.V2TestContext
import net.java.truelicense.json.V2JsonLicenseManagementContext
import org.slf4j.LoggerFactory
import V2JsonTestContext._

/** @author Christian Schlichtherle */
trait V2JsonTestContext extends V2TestContext {

  override final val managementContext =
    new V2JsonLicenseManagementContext("subject") {
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
    }
}

object V2JsonTestContext {
  private val logger = LoggerFactory getLogger classOf[V2JsonTestContext]
}
