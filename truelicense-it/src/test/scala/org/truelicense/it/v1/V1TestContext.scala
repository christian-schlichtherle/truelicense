/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.v1

import de.schlichtherle.xml.GenericCertificate
import org.truelicense.api._
import org.truelicense.api.crypto.EncryptionParameters
import org.truelicense.api.io.{Transformation, Store}
import org.truelicense.it.core.TestContext
import org.truelicense.it.v1.V1TestContext._
import org.truelicense.v1.V1LicenseApplicationContext

/** @author Christian Schlichtherle */
trait V1TestContext extends TestContext[GenericCertificate] {

  override final val applicationContext = new V1LicenseApplicationContext

  override final def chainedConsumerManager(parent: ConsumerLicenseManager, store: Store) = {
    val cm = managementContext.consumer
      .authentication
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
      .authentication
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
      .authentication
        .alias("mykey")
        .loadFromResource(prefix + "public.jks")
        .storeProtection(test1234)
        .inject
      .storeIn(store)
      .build
    require(cm.context eq managementContext)
    cm
  }

  final override def encryption = applicationContext encryption new EncryptionParameters {
    def algorithm = "PBEWithMD5AndDES"
    def protection = test1234
  }

  override final def ftpConsumerManager(parent: ConsumerLicenseManager, store: Store) = {
    val cm = managementContext.consumer
      .authentication
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

  override final def vendorManager = {
    val vm = managementContext.vendor
      .encryption
        .protection(test1234)
        .inject
      .authentication
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

  private def prefix = classOf[V1TestContext].getPackage.getName.replace('.', '/') + '/'
}
