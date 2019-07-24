/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.core

import java.util.Locale

import global.namespace.fun.io.bios.BIOS
import global.namespace.truelicense.api.i18n.Message
import org.scalatest.Matchers._

trait MessagesTestSupport {

  def testSerialization(message: Message): Unit = {
    val message2 = BIOS clone message
    message2.toString() shouldBe message.toString()
    message2 toString Locale.ROOT shouldBe (message toString Locale.ROOT)
  }
}

object MessagesTestSupport extends MessagesTestSupport
