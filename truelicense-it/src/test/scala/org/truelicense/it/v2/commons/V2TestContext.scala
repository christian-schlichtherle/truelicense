/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.v2.commons

import org.truelicense.api._
import org.truelicense.api.io.Store
import org.truelicense.it.core.TestContext
import org.truelicense.it.v2.commons.V2TestContext.prefix
import org.truelicense.v2.commons.auth.V2RepositoryModel

/** @author Christian Schlichtherle */
trait V2TestContext extends TestContext[V2RepositoryModel] {

  override final def chainedConsumerManager(parent: LicenseConsumerManager, store: Store) = {
    val cm = managementContext.consumer
      .keyStore
        .alias("mykey")
        .loadFromResource(prefix + "chained-public.jceks")
        .storePassword(test1234)
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
        .password(test1234)
        .inject
      .keyStore
        .alias("mykey")
        .loadFromResource(prefix + "chained-private.jceks")
        .storePassword(test1234)
        .inject
      .build
    require(vm.context eq managementContext)
    vm
  }

  override final def consumerManager(store: Store) = {
    val cm = managementContext.consumer
      .encryption
        .password(test1234)
        .inject
      .keyStore
        .alias("mykey")
        .loadFromResource(prefix + "public.jceks")
        .storePassword(test1234)
        .inject
      .storeIn(store)
      .build
    require(cm.context eq managementContext)
    cm
  }

  override final def ftpConsumerManager(parent: LicenseConsumerManager, store: Store) = {
    val cm = managementContext.consumer
      .ftpDays(1)
      .keyStore
        .alias("mykey")
        .loadFromResource(prefix + "ftp.jceks")
        .storePassword(test1234)
        .inject
      .parent(parent)
      .storeIn(store)
      .build
    require(cm.context eq managementContext)
    cm
  }

  override final def vendorManager = {
    val vm = managementContext.vendor
      .encryption
        .password(test1234)
        .inject
      .keyStore
        .alias("mykey")
        .loadFromResource(prefix + "private.jceks")
        .storePassword(test1234)
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
