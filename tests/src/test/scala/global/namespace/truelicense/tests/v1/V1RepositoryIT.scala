/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.v1

import global.namespace.truelicense.api.auth.RepositoryFactory
import global.namespace.truelicense.tests.core.RepositoryITLike
import global.namespace.truelicense.v1.V1
import org.scalatest.WordSpec

class V1RepositoryIT extends WordSpec with RepositoryITLike with V1TestContext {

  val factory: RepositoryFactory[Model] = V1.repositoryFactory.asInstanceOf[RepositoryFactory[Model]]
}
