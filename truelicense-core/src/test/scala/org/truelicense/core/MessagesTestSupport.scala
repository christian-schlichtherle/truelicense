/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core

import java.util.Locale

import _root_.org.truelicense.api.i18n.Message
import _root_.org.truelicense.spi.codec.Codecs
import org.scalatest.Matchers._

/** @author Christian Schlichtherle */
trait MessagesTestSupport {
  def testSerialization(message: Message) {
    val message2 = Codecs clone message
    message2 toString () should be (message toString ())
    message2 toString Locale.ROOT should be (message toString Locale.ROOT)
  }
}

/** @author Christian Schlichtherle */
object MessagesTestSupport extends MessagesTestSupport
