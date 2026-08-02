/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.build.tasks.commons

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

/**
 * The error count gates task failure in `ObfuscateClassesTask`, so counters must accumulate across calls and stay
 * isolated per instance.
 */
class CountingLoggerSpec extends AnyWordSpec {

  private def noOpLogger: Logger = new Logger {
    override def isDebugEnabled: Boolean = true
    override def isInfoEnabled: Boolean = true
    override def isWarnEnabled: Boolean = true
    override def isErrorEnabled: Boolean = true
    override def debug(error: Throwable): Unit = ()
    override def debug(message: CharSequence): Unit = ()
    override def debug(message: CharSequence, error: Throwable): Unit = ()
    override def info(error: Throwable): Unit = ()
    override def info(message: CharSequence): Unit = ()
    override def info(message: CharSequence, error: Throwable): Unit = ()
    override def warn(error: Throwable): Unit = ()
    override def warn(message: CharSequence): Unit = ()
    override def warn(message: CharSequence, error: Throwable): Unit = ()
    override def error(error: Throwable): Unit = ()
    override def error(message: CharSequence): Unit = ()
    override def error(message: CharSequence, error: Throwable): Unit = ()
  }

  "A CountingLogger" should {
    "accumulate counts across calls" in {
      val logger = new CountingLogger(noOpLogger)
      logger.error("one")
      logger.error("two")
      logger.error(new Exception("three"))
      logger.errorCounter.get shouldBe 3
      logger.warn("w")
      logger.warnCounter.get shouldBe 1
      logger.debugCounter.get shouldBe 0
      logger.infoCounter.get shouldBe 0
    }

    "keep counters isolated per instance" in {
      val a = new CountingLogger(noOpLogger)
      val b = new CountingLogger(noOpLogger)
      a.error("boom")
      a.errorCounter.get shouldBe 1
      b.errorCounter.get shouldBe 0
    }
  }
}
