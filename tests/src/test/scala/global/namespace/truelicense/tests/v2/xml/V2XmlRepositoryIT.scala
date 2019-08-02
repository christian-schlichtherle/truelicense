/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.v2.xml

import global.namespace.truelicense.api.auth.RepositoryFactory
import global.namespace.truelicense.tests.core.RepositoryITLike
import global.namespace.truelicense.v2.xml.V2Xml
import org.scalatest.WordSpec

class V2XmlRepositoryIT extends WordSpec with RepositoryITLike with V2XmlTestContext {

  val factory: RepositoryFactory[Model] = V2Xml.repositoryFactory.asInstanceOf[RepositoryFactory[Model]]
}
