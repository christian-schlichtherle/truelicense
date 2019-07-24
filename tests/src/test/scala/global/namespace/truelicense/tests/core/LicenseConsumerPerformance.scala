/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.core

import java.util.concurrent.Callable

import global.namespace.fun.io.bios.BIOS.memory

trait LicenseConsumerPerformance extends Callable[Unit] { this: TestContext =>

  def call(): Unit = {
    val store = memory
    vendorManager generateKeyFrom license saveTo store
    for (i <- 1 to 5) {
      val manager = consumerManager()
      manager install store
      val num = 1000 * 1000
      val start = System.nanoTime
      for (j <- 1 to num) {
        manager.verify()
      }
      val time = System.nanoTime - start
      printf("Iteration %d verified the installed license key %,d times per second.\n", i, num * 1000L * 1000L * 1000L / time)
    }
  }
}
