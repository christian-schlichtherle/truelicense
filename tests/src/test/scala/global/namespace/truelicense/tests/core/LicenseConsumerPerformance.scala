/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.core

import global.namespace.fun.io.bios.BIOS.memory

trait LicenseConsumerPerformance {
  this: TestContext =>

  final def run(): Unit = new State {
    {
      val tempStore = memory
      vendorManager generateKeyFrom licenseBean saveTo tempStore
      for (i <- 1 to 5) {
        consumerManager install tempStore
        val num = 1000 * 1000
        val start = System.nanoTime
        for (j <- 1 to num) {
          consumerManager.verify()
        }
        val time = System.nanoTime - start
        printf("Iteration %d verified the installed license key %,d times per second.\n", i, num * 1000L * 1000L * 1000L / time)
      }
    }
  }
}
