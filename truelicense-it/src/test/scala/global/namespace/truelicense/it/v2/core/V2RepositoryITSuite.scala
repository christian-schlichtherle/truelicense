/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.it.v2.core

import global.namespace.truelicense.api.auth.RepositoryContext
import global.namespace.truelicense.it.core.{RepositoryITSuite, TestContext}
import global.namespace.truelicense.v2.core.auth.{V2RepositoryContext, V2RepositoryModel}

/** @author Christian Schlichtherle */
abstract class V2RepositoryITSuite extends RepositoryITSuite[V2RepositoryModel] {
  this: TestContext =>

  val context: RepositoryContext[V2RepositoryModel] = new V2RepositoryContext
}
