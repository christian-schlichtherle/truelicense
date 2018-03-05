/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.core

import java.util.concurrent.Callable

import global.namespace.fun.io.bios.BIOS.memoryStore

/** @author Christian Schlichtherle */
trait LicenseConsumerPerformance extends Callable[Unit] { this: TestContext[_] =>
  def call() {
    val store = memoryStore
    vendorManager generateKeyFrom license saveTo store.output
    for (i <- 1 to 5) {
      val manager = consumerManager()
      manager install store.input
      val num = 1000 * 1000
      val start = System.nanoTime
      for (j <- 1 to num) {
        manager verify ()
      }
      val time = System.nanoTime - start
      printf("Iteration %d verified the installed license key %,d times per second.\n", i, num * 1000l * 1000l * 1000l / time)
    }
  }
}
