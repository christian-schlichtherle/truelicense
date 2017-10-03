/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.core

import java.util.concurrent.Callable

/** @author Christian Schlichtherle */
trait LicenseVendorPerformance extends Callable[Unit] { this: TestContext[_] =>
  def call() {
    for (i <- 1 to 5) {
      val vm = vendorManager
      val num = 1000
      val start = System.nanoTime
      for (j <- 1 to num) {
        vm generateKeyFrom license saveTo store
      }
      val time = System.nanoTime - start
      printf("Iteration %d generated %,d license keys per second.\n", i, num * 1000l * 1000l * 1000l / time)
    }
  }
}
