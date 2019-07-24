/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.api.x500

class MyX500PrincipalBuilder extends GenX500PrincipalBuilder[MyX500PrincipalBuilder] {

  def addFOO(value: String) = {
    addKeyword("FOO", "1.2.3.4.5.6.7.8.9")
    addAttribute("FOO", value)
  }
}
