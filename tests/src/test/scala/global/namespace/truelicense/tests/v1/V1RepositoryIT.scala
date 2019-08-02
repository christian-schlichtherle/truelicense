/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.v1

import de.schlichtherle.xml.GenericCertificate
import global.namespace.truelicense.api.auth.RepositoryFactory
import global.namespace.truelicense.tests.core.RepositoryITLike
import global.namespace.truelicense.v1.auth.V1RepositoryFactory
import org.scalatest.WordSpec

class V1RepositoryIT extends WordSpec with RepositoryITLike[GenericCertificate] with V1TestContext {

  val factory: RepositoryFactory[GenericCertificate] = new V1RepositoryFactory
}
