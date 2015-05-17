/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.v2.json

import org.slf4j.LoggerFactory
import org.truelicense.api._
import org.truelicense.core.TrueLicenseManagementContext
import org.truelicense.it.v2.commons.V2TestContext
import org.truelicense.it.v2.json.V2JsonTestContext.logger
import org.truelicense.v2.commons.auth.V2RepositoryModel
import org.truelicense.v2.json.V2JsonLicenseApplicationContext

/** @author Christian Schlichtherle */
trait V2JsonTestContext extends V2TestContext {

  override final val managementContext =
    new V2JsonLicenseApplicationContext()
      .context
      .subject("subject")
      .validation(new LicenseValidation {
          override def validate(bean: License) {
            logger debug ("Validated {}.", bean)
          }
        })
      .build
      .asInstanceOf[TrueLicenseManagementContext[V2RepositoryModel]]
}

object V2JsonTestContext {

  private val logger = LoggerFactory getLogger classOf[V2JsonTestContext]
}
