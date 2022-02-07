// @GENERATOR:play-routes-compiler
// @SOURCE:conf/routes

import play.api.mvc.Call


import _root_.controllers.Assets.Asset

// @LINE:7
package controllers {

  // @LINE:7
  class ReverseApplication(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:7
    def index: Call = {
      
      Call("GET", _prefix)
    }
  
    // @LINE:9
    def graphql(query:String, variables:Option[String], operation:Option[String]): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "graphql" + play.core.routing.queryString(List(Some(implicitly[play.api.mvc.QueryStringBindable[String]].unbind("query", query)), Some(implicitly[play.api.mvc.QueryStringBindable[Option[String]]].unbind("variables", variables)), Some(implicitly[play.api.mvc.QueryStringBindable[Option[String]]].unbind("operation", operation)))))
    }
  
    // @LINE:10
    def graphqlBody: Call = {
      
      Call("POST", _prefix + { _defaultPrefix } + "graphql")
    }
  
  }


}
