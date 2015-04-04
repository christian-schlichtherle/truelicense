package net.java.truelicense.it.core

import java.io.File

import net.java.truelicense.core.io.FileStore

/** @author Christian Schlichtherle */
trait FileStoreITContext { this: TestContext =>
  override final def store = {
    val file = File.createTempFile("truelicense", null)
    file delete ()
    new FileStore(file)
  }
}
