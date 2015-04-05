/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.v1

import org.truelicense.core._
import org.truelicense.core.io.Store
import org.truelicense.it.core.TestContext
import org.truelicense.it.core.TestContext.test1234
import org.truelicense.it.v1.V1TestContext._
import org.slf4j.LoggerFactory

/** @author Christian Schlichtherle */
trait V1TestContext extends TestContext {

  override final val managementContext =
    new V1LicenseManagementContext("subject") {
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

  override final def vendorManager = {
    val vm = vendorContext.managerBuilder
      .keyStore
        .alias("mykey")
        .loadFromResource(prefix + "private.jks")
        .storePassword(test1234)
        .inject
      .pbe
        .password(test1234)
        .inject
      .build
    require(vm.context eq vendorContext)
    vm
  }

  override final def chainedVendorManager = {
    val vm = vendorContext.managerBuilder
      .keyStore
        .alias("mykey")
        .loadFromResource(prefix + "chained-private.jks")
        .storePassword(test1234)
        .inject
      .pbe
        .password(test1234)
        .inject
      .build
    require(vm.context eq vendorContext)
    vm
  }

  override final def consumerManager(store: Store) = {
    val cm = consumerContext.managerBuilder
      .keyStore
        .alias("mykey")
        .loadFromResource(prefix + "public.jks")
        .storePassword(test1234)
        .inject
      .pbe
        .password(test1234)
        .inject
      .storeIn(store)
      .build
    require(cm.context eq consumerContext)
    cm
  }

  override final def chainedConsumerManager(parent: LicenseConsumerManager, store: Store) = {
    val cm = consumerContext.managerBuilder
      .keyStore
        .alias("mykey")
        .loadFromResource(prefix + "chained-public.jks")
        .storePassword(test1234)
        .inject
      .parent(parent)
      .storeIn(store)
      .build
    require(cm.context eq consumerContext)
    cm
  }

  override final def ftpConsumerManager(parent: LicenseConsumerManager, store: Store) = {
    val cm = consumerContext.managerBuilder
      .keyStore
        .alias("mykey")
        .loadFromResource(prefix + "ftp.jks")
        .storePassword(test1234)
        .inject
      .parent(parent)
      .storeIn(store)
      .ftpDays(1)
      .build
    require(cm.context eq consumerContext)
    cm
  }
}

/** @author Christian Schlichtherle */
object V1TestContext {
  private val logger = LoggerFactory getLogger classOf[V1TestContext]
  private def prefix = classOf[V1TestContext].getPackage.getName.replace('.', '/') + '/'
}
