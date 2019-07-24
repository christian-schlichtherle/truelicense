/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.v2.core

import global.namespace.fun.io.api.Store
import global.namespace.truelicense.api.{ConsumerLicenseManager, VendorLicenseManager}
import global.namespace.truelicense.tests.core.TestContext
import global.namespace.truelicense.tests.v2.core.V2TestContext.prefix

/** @author Christian Schlichtherle */
trait V2TestContext extends TestContext {

  final def chainedConsumerManager(parent: ConsumerLicenseManager, store: Store): ConsumerLicenseManager = {
    val cm = managementContext.consumer
      .authentication
        .alias("mykey")
        .loadFromResource(prefix + "chained-public.jceks")
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
        .loadFromResource(prefix + "chained-private.jceks")
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
        .loadFromResource(prefix + "public.jceks")
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
        .loadFromResource(prefix + "ftp.jceks")
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
        .loadFromResource(prefix + "private.jceks")
        .storeProtection(test1234)
        .up
      .build
    require(vm.context eq managementContext)
    vm
  }
}

/** @author Christian Schlichtherle */
object V2TestContext {

  private def prefix = classOf[V2TestContext].getPackage.getName.replace('.', '/') + '/'
}
