/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.v2.commons

import net.truelicense.api._
import net.truelicense.api.crypto.EncryptionParameters
import global.namespace.fun.io.api.{Store, Transformation}
import net.truelicense.api.passwd.PasswordProtection
import net.truelicense.it.core.TestContext
import net.truelicense.it.v2.commons.V2TestContext.prefix
import net.truelicense.v2.commons.auth.V2RepositoryModel

/** @author Christian Schlichtherle */
trait V2TestContext extends TestContext[V2RepositoryModel] {

  final def chainedConsumerManager(parent: ConsumerLicenseManager, store: Store): ConsumerLicenseManager = {
    val cm = managementContext.consumer
      .authentication
        .alias("mykey")
        .loadFromResource(prefix + "chained-public.jceks")
        .storeProtection(test1234)
        .inject
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
        .inject
      .authentication
        .alias("mykey")
        .loadFromResource(prefix + "chained-private.jceks")
        .storeProtection(test1234)
        .inject
      .build
    require(vm.context eq managementContext)
    vm
  }

  final def consumerManager(store: Store): ConsumerLicenseManager = {
    val cm = managementContext.consumer
      .encryption
        .protection(test1234)
        .inject
      .authentication
        .alias("mykey")
        .loadFromResource(prefix + "public.jceks")
        .storeProtection(test1234)
        .inject
      .storeIn(store)
      .build
    require(cm.context eq managementContext)
    cm
  }

  final def encryption: Transformation = applicationContext encryption new EncryptionParameters {
    def algorithm: String = "PBEWithSHA1AndDESede"
    def protection: PasswordProtection = test1234
  }

  final def ftpConsumerManager(parent: ConsumerLicenseManager, store: Store): ConsumerLicenseManager = {
    val cm = managementContext.consumer
      .ftpDays(1)
      .authentication
        .alias("mykey")
        .loadFromResource(prefix + "ftp.jceks")
        .storeProtection(test1234)
        .inject
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
        .inject
      .authentication
        .alias("mykey")
        .loadFromResource(prefix + "private.jceks")
        .storeProtection(test1234)
        .inject
      .build
    require(vm.context eq managementContext)
    vm
  }
}

/** @author Christian Schlichtherle */
object V2TestContext {
  private def prefix = classOf[V2TestContext].getPackage.getName.replace('.', '/') + '/'
}
