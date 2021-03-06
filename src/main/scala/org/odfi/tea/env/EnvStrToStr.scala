package org.odfi.tea.env

object EnvStrToStr {

  def apply(str: String) = {

    var searchReg = """\$\{?([\w-_]+)\}?""".r

    var result = str
    searchReg.findAllMatchIn(str).foreach { matchRes =>
      // Search for VarName
      var varName = matchRes.group(1)
      sys.env.get(varName) match {
        case Some(varValue) =>
          result = result.replace(matchRes.matched, varValue)
        case None if (sys.props.contains(varName)) =>
          println(
            s"Replacing with sys prop -> " + result
              .replace(matchRes.matched, sys.props.get(varName).get)
          )
          result = result.replace(matchRes.matched, sys.props.get(varName).get)
        case None =>
          /*sys.env.foreach {
              case (name,value) =>
                println(s"Env: "+name+" -> "+value)
            }*/
          sys.error(
            "Env Variable: " + varName + " in source String: " + str + " cannot be found in actual Environment or System Properties"
          )
      }

    }

    result
  }

}
