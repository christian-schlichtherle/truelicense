/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.it.v2.json

import java.util.Date

import net.java.truelicense.core._
import net.java.truelicense.core.codec.Codec
import net.java.truelicense.core.policy.PasswordPolicy
import net.java.truelicense.it.v2.core.V2TestContext
import net.java.truelicense.it.v2.json.V2JsonTestContext.logger
import net.java.truelicense.json.V2JsonLicenseManagementContext
import net.java.truelicense.json.codec.JsonCodec
import org.slf4j.LoggerFactory

/** @author Christian Schlichtherle */
trait V2JsonTestContext extends V2TestContext {

  final val managementContext: LicenseManagementContext =
    new V2JsonLicenseManagementContext("subject") {
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
      override def codec: JsonCodec = super.codec
      override def policy: PasswordPolicy = super.policy
    }
}

object V2JsonTestContext {
  private val logger = LoggerFactory getLogger classOf[V2JsonTestContext]
}
