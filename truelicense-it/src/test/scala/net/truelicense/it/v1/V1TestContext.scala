/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.v1

import de.schlichtherle.xml.GenericCertificate
import global.namespace.fun.io.api.{Store, Transformation}
import net.truelicense.api._
import net.truelicense.api.crypto.EncryptionParameters
import net.truelicense.api.passwd.PasswordProtection
import net.truelicense.it.core.TestContext
import net.truelicense.it.v1.V1TestContext._
import net.truelicense.v1.V1LicenseApplicationContext

/** @author Christian Schlichtherle */
trait V1TestContext extends TestContext[GenericCertificate] {

  final val applicationContext = new V1LicenseApplicationContext

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

  final def encryption: Transformation = applicationContext encryption new EncryptionParameters {
    def algorithm: String = "PBEWithMD5AndDES"
    def protection: PasswordProtection = test1234
  }

  final def ftpConsumerManager(parent: ConsumerLicenseManager, store: Store): ConsumerLicenseManager = {
    val cm = managementContext.consumer
      .authentication
        .alias("mykey")
        .loadFromResource(prefix + "ftp.jks")
        .storeProtection(test1234)
        .up
      .parent(parent)
      .storeIn(store)
      .ftpDays(1)
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

/** @author Christian Schlichtherle */
object V1TestContext {

  private def prefix = classOf[V1TestContext].getPackage.getName.replace('.', '/') + '/'
}
