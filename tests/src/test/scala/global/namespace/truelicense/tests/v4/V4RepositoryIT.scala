/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.v4

import global.namespace.truelicense.api.auth.RepositoryFactory
import global.namespace.truelicense.tests.core.RepositoryITLike
import global.namespace.truelicense.v4.V4
import org.scalatest.WordSpec

class V4RepositoryIT extends WordSpec with RepositoryITLike with V4TestContext {

  val factory: RepositoryFactory[Model] = V4.repositoryFactory.asInstanceOf[RepositoryFactory[Model]]
}
