/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.ui

import java.util.Locale
import org.truelicense.api.i18n.Message
import org.truelicense.core.codec.Codecs
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
