/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.ui

import global.namespace.fun.io.bios.BIOS
import global.namespace.truelicense.api.i18n.Message
import org.scalatest.matchers.should.Matchers._

import java.util.Locale

trait MessagesTestSupport {

  def testSerialization(message: Message): Unit = {
    val message2 = BIOS clone message
    message2.toString() shouldBe message.toString()
    message2 toString Locale.ROOT shouldBe (message toString Locale.ROOT)
  }
}

object MessagesTestSupport extends MessagesTestSupport
