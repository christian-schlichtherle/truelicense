/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.ui

import java.util.Locale

import net.truelicense.api.i18n.Message
import net.truelicense.spi.io.MemoryStore
import org.scalatest.Matchers._

/** @author Christian Schlichtherle */
trait MessagesTestSupport {
  def testSerialization(message: Message) {
    val message2 = new MemoryStore clone message
    message2 toString () shouldBe (message toString ())
    message2 toString Locale.ROOT shouldBe (message toString Locale.ROOT)
  }
}

/** @author Christian Schlichtherle */
object MessagesTestSupport extends MessagesTestSupport
