/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.core

/** @author Christian Schlichtherle */
abstract class RepositoryCodecTestSuite[Model <: AnyRef]
  extends CodecTestSuite { this: TestContext[Model] =>

  final override def artifact = {
    val l = license
    val c = managementContext.codec
    val p = vendorManager.parameters
    val a = p.authentication
    val rx = repositoryContext
    val rm = rx.model
    val rc = rx.controller(rm, c)
    a sign (rc, l)
    rm
  }
}
