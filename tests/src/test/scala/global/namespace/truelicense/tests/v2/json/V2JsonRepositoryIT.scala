/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.v2.json

import global.namespace.truelicense.api.auth.RepositoryFactory
import global.namespace.truelicense.tests.core.RepositoryITLike
import global.namespace.truelicense.v2.json.V2Json
import org.scalatest.WordSpec

class V2JsonRepositoryIT extends WordSpec with RepositoryITLike with V2JsonTestContext {

  val factory: RepositoryFactory[Model] = V2Json.repositoryFactory.asInstanceOf[RepositoryFactory[Model]]
}
