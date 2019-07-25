/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.v1

import global.namespace.fun.io.api.Store
import global.namespace.truelicense.api.{ConsumerLicenseManager, LicenseManagementContextBuilder, VendorLicenseManager}
import global.namespace.truelicense.tests.core.TestContext
import global.namespace.truelicense.tests.v1.V1TestContext._
import global.namespace.truelicense.v1.V1

trait V1TestContext extends TestContext {

  //noinspection ScalaDeprecation
  final override def managementContextBuilder: LicenseManagementContextBuilder = V1.builder

  final def chainedConsumerManager(parent: ConsumerLicenseManager, store: Store): ConsumerLicenseManager = {
    val cm = managementContext.consumer
      .authentication
        .alias("mykey")
        .loadFromResource(prefix + "chained-public.jks")
        .storeProtection(test1234)
        .up
      .parent(parent)
      .storeIn(store)
      .build
    require(cm.context eq managementContext)
    cm
  }

  final def chainedVendorManager: VendorLicenseManager = {
    val vm = managementContext.vendor
      .encryption
        .protection(test1234)
        .up
      .authentication
        .alias("mykey")
        .loadFromResource(prefix + "chained-private.jks")
        .storeProtection(test1234)
        .up
      .build
    require(vm.context eq managementContext)
    vm
  }

  final def consumerManager(store: Store): ConsumerLicenseManager = {
    val cm = managementContext.consumer
      .encryption
        .protection(test1234)
        .up
      .authentication
        .alias("mykey")
        .loadFromResource(prefix + "public.jks")
        .storeProtection(test1234)
        .up
      .storeIn(store)
      .build
    require(cm.context eq managementContext)
    cm
  }

  final def ftpConsumerManager(parent: ConsumerLicenseManager, store: Store): ConsumerLicenseManager = {
    val cm = managementContext.consumer
      .ftpDays(1)
      .authentication
        .alias("mykey")
        .loadFromResource(prefix + "ftp.jks")
        .storeProtection(test1234)
        .up
      .parent(parent)
      .storeIn(store)
      .build
    require(cm.context eq managementContext)
    cm
  }

  final def vendorManager: VendorLicenseManager = {
    val vm = managementContext.vendor
      .encryption
        .protection(test1234)
        .up
      .authentication
        .alias("mykey")
        .loadFromResource(prefix + "private.jks")
        .storeProtection(test1234)
        .up
      .build
    require(vm.context eq managementContext)
    vm
  }
}

object V1TestContext {

  private def prefix = classOf[V1TestContext].getPackage.getName.replace('.', '/') + '/'
}
