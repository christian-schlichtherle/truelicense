/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.it.v2.core

import net.java.truelicense.core._
import net.java.truelicense.core.io.Store
import net.java.truelicense.it.core.TestContext
import net.java.truelicense.it.core.TestContext.test1234
import net.java.truelicense.it.v2.core.V2TestContext.prefix

/** @author Christian Schlichtherle */
trait V2TestContext extends TestContext {

  override final def vendorManager = {
    import vendorContext._
    val vm = manager(keyStore(resource(prefix + "private.jceks"),
                              null, test1234, "mykey", test1234),
            pbe(null, test1234))
    require(vm.context eq vendorContext)
    vm
  }

  override final def chainedVendorManager = {
    import vendorContext._
    val vm = manager(keyStore(resource(prefix + "chained-private.jceks"),
                              null, test1234, "mykey", test1234),
            pbe(null, test1234))
    require(vm.context eq vendorContext)
    vm
  }

  override final def consumerManager(store: Store) = {
    import consumerContext._
    val cm = manager(keyStore(resource(prefix + "public.jceks"),
                              null, test1234, "mykey"),
            pbe(null, test1234),
            store)
    require(cm.context eq consumerContext)
    cm
  }

  override final def chainedConsumerManager(parent: LicenseConsumerManager, store: Store) = {
    import consumerContext._
    val cm = chainedManager(parent,
                   keyStore(resource(prefix + "chained-public.jceks"),
                            null, test1234, "mykey"),
                   null,
                   store)
    require(cm.context eq consumerContext)
    cm
  }

  override final def ftpConsumerManager(parent: LicenseConsumerManager, store: Store) = {
    import consumerContext._
    val cm = ftpManager(parent,
               ftpKeyStore(resource(prefix + "ftp.jceks"),
                           null, test1234, "mykey", test1234),
               null,
               store,
               1)
    require(cm.context eq consumerContext)
    cm
  }
}

/** @author Christian Schlichtherle */
object V2TestContext {
  private def prefix = classOf[V2TestContext].getPackage.getName.replace('.', '/') + '/'
}
