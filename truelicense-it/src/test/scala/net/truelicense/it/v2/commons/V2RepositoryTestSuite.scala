/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.v2.commons

import net.truelicense.api.auth.RepositoryContext
import net.truelicense.it.core.{RepositoryTestSuite, TestContext}
import net.truelicense.v2.commons.auth.{V2RepositoryContext, V2RepositoryModel}

/** @author Christian Schlichtherle */
abstract class V2RepositoryTestSuite extends RepositoryTestSuite[V2RepositoryModel] {

  this: TestContext[V2RepositoryModel] =>

  val context: RepositoryContext[V2RepositoryModel] = new V2RepositoryContext
}
