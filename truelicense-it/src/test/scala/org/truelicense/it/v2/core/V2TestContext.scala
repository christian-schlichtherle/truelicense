/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.v2.core

import org.truelicense.api._
import org.truelicense.api.io.Store
import org.truelicense.it.core.TestContext
import org.truelicense.it.core.TestContext.test1234
import org.truelicense.it.v2.core.V2TestContext.prefix

/** @author Christian Schlichtherle */
trait V2TestContext extends TestContext {

  override final def vendorManager = {
    val vm = vendorContext.manager
      .keyStore
        .alias("mykey")
        .loadFromResource(prefix + "private.jceks")
        .storePassword(test1234)
        .inject
      .encryption
        .password(test1234)
        .inject
      .build
    require(vm.context eq vendorContext)
    vm
  }

  override final def chainedVendorManager = {
    val vm = vendorContext.manager
      .keyStore
        .alias("mykey")
        .loadFromResource(prefix + "chained-private.jceks")
        .storePassword(test1234)
        .inject
      .encryption
        .password(test1234)
        .inject
      .build
    require(vm.context eq vendorContext)
    vm
  }

  override final def consumerManager(store: Store) = {
    val cm = consumerContext.manager
      .keyStore
        .alias("mykey")
        .loadFromResource(prefix + "public.jceks")
        .storePassword(test1234)
        .inject
      .encryption
        .password(test1234)
        .inject
      .storeIn(store)
      .build
    require(cm.context eq consumerContext)
    cm
  }

  override final def chainedConsumerManager(parent: LicenseConsumerManager, store: Store) = {
    val cm = consumerContext.manager
      .keyStore
        .alias("mykey")
        .loadFromResource(prefix + "chained-public.jceks")
        .storePassword(test1234)
        .inject
      .parent(parent)
      .storeIn(store)
      .build
    require(cm.context eq consumerContext)
    cm
  }

  override final def ftpConsumerManager(parent: LicenseConsumerManager, store: Store) = {
    val cm = consumerContext.manager
      .ftpDays(1)
      .keyStore
        .alias("mykey")
        .loadFromResource(prefix + "ftp.jceks")
        .storePassword(test1234)
        .inject
      .parent(parent)
      .storeIn(store)
      .build
    require(cm.context eq consumerContext)
    cm
  }
}

/** @author Christian Schlichtherle */
object V2TestContext {
  private def prefix = classOf[V2TestContext].getPackage.getName.replace('.', '/') + '/'
}
