/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.v1

import de.schlichtherle.xml.GenericCertificate
import org.slf4j.LoggerFactory
import org.truelicense.api._
import org.truelicense.api.io.Store
import org.truelicense.it.core.TestContext
import org.truelicense.it.v1.V1TestContext._
import org.truelicense.v1.V1LicenseManagementContext

/** @author Christian Schlichtherle */
trait V1TestContext extends TestContext[GenericCertificate] {

  override final def chainedConsumerManager(parent: ConsumerLicenseManager, store: Store) = {
    val cm = managementContext.consumer
      .keyStore
        .alias("mykey")
        .loadFromResource(prefix + "chained-public.jks")
        .storeProtection(test1234)
        .inject
      .parent(parent)
      .storeIn(store)
      .build
    require(cm.context eq managementContext)
    cm
  }

  override final def chainedVendorManager = {
    val vm = managementContext.vendor
      .encryption
        .protection(test1234)
        .inject
      .keyStore
        .alias("mykey")
        .loadFromResource(prefix + "chained-private.jks")
        .storeProtection(test1234)
        .inject
      .build
    require(vm.context eq managementContext)
    vm
  }

  override final def consumerManager(store: Store) = {
    val cm = managementContext.consumer
      .encryption
        .protection(test1234)
        .inject
      .keyStore
        .alias("mykey")
        .loadFromResource(prefix + "public.jks")
        .storeProtection(test1234)
        .inject
      .storeIn(store)
      .build
    require(cm.context eq managementContext)
    cm
  }

  override final def ftpConsumerManager(parent: ConsumerLicenseManager, store: Store) = {
    val cm = managementContext.consumer
      .keyStore
        .alias("mykey")
        .loadFromResource(prefix + "ftp.jks")
        .storeProtection(test1234)
        .inject
      .parent(parent)
      .storeIn(store)
      .ftpDays(1)
      .build
    require(cm.context eq managementContext)
    cm
  }

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
    val vm = managementContext.vendor
      .encryption
        .protection(test1234)
        .inject
      .keyStore
        .alias("mykey")
        .loadFromResource(prefix + "private.jks")
        .storeProtection(test1234)
        .inject
      .build
    require(vm.context eq managementContext)
    vm
  }
}

/** @author Christian Schlichtherle */
object V1TestContext {
  private val logger = LoggerFactory getLogger classOf[V1TestContext]
  private def prefix = classOf[V1TestContext].getPackage.getName.replace('.', '/') + '/'
}
