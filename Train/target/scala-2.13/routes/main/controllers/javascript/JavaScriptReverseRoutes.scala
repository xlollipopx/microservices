// @GENERATOR:play-routes-compiler
// @SOURCE:conf/routes

import play.api.routing.JavaScriptReverseRoute


import _root_.controllers.Assets.Asset

// @LINE:7
package controllers.javascript {

  // @LINE:7
  class ReverseApplication(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:7
    def index: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Application.index",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + """"})
        }
      """
    )
  
    // @LINE:9
    def graphql: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Application.graphql",
      """
        function(query0,variables1,operation2) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "graphql" + _qS([(""" + implicitly[play.api.mvc.QueryStringBindable[String]].javascriptUnbind + """)("query", query0), (""" + implicitly[play.api.mvc.QueryStringBindable[Option[String]]].javascriptUnbind + """)("variables", variables1), (""" + implicitly[play.api.mvc.QueryStringBindable[Option[String]]].javascriptUnbind + """)("operation", operation2)])})
        }
      """
    )
  
    // @LINE:10
    def graphqlBody: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Application.graphqlBody",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "graphql"})
        }
      """
    )
  
  }


}
