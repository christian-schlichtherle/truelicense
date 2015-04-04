package net.java.truelicense.it

import java.util.concurrent.Callable

/** @author Christian Schlichtherle */
trait LicenseVendorPerformance extends Callable[Unit] { this: TestContext =>
  override def call() {
    for (i <- 1 to 5) {
      val vm = vendorManager
      val num = 1000
      val start = System.nanoTime
      for (j <- 1 to num) vm create (license, store)
      val time = System.nanoTime - start
      printf("Iteration %d created %,d license keys per second.\n", i, num * 1000l * 1000l * 1000l / time)
    }
  }
}
