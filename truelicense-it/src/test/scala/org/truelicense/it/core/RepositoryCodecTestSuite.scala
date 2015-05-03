/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.core

/** @author Christian Schlichtherle */
abstract class RepositoryCodecTestSuite
extends CodecTestSuite { this: TestContext =>
  override def artifact = {
    val l = license
    val c = vendorContext.codec
    val p = vendorManager.parameters
    val a = p.authentication
    val rx = p.repositoryContext
    val rm = rx.model
    val rc = rx.controller(rm, c)
    a sign (rc, l)
    rm
  }
}
