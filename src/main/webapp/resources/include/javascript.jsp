<%@ page language="java" pageEncoding="UTF-8"%>
<!--[if lt IE 9]>
	<script src="<%=request.getContextPath()%>/resources/crossbrowserjs/html5shiv.js"></script>
	<script src="<%=request.getContextPath()%>/resources/crossbrowserjs/respond.min.js"></script>
	<script src="<%=request.getContextPath()%>/resources/crossbrowserjs/excanvas.min.js"></script>
<![endif]-->

<!-- JSON -->
<script>!window.JSON && document.write('<script src="<%=request.getContextPath()%>/resources/plugins/JSON/js/json2.js"><\/script>')</script>

<!-- RequirJS -->
<script src="<%=request.getContextPath()%>/resources/plugins/requirejs/js/require.js"></script>

<!-- build:jshash -->
<script type="text/javascript">
(function() {
  /**
   * JS version controller
   *
   * @author zhangxiong
   */
   window.urlArgs = "";
})();
</script>
<!-- endbuild -->


<!-- RequirJS Core Configuration -->
<script type="text/javascript">
(function() {
  /**
   * Intergration Requirejs
   *
   * @author zhangxiong
   */
  requirejs.config({
    baseUrl : '<%=request.getContextPath()%>/resources',

    urlArgs : window.urlArgs || '',

    waitSeconds: 15,

    paths : {
      // jQuery
      // --------------
      'jquery'           : 'plugins/jquery/jquery-1.9.1.min',
      
      'jquery.ui'        : 'plugins/jquery-ui/ui/minified/jquery-ui.min',

      // bootstrap
      // --------------
      'bootstrap'              : 'plugins/bootstrap/js/bootstrap.min',

      // Lib
      // --------------

      // Requirejs Plugins
      // --------------
      'domReady' : 'plugins/requirejs-domReady/js/domReady',

      // Application Folders
      // -------------------
    }
  });
})();
</script>

<script type="text/javascript">
/**
 * App Main
 *
 * @author zhangxiong
 */
(function() {
  "use strict";

  // Main
  require(['js/main'], function() {});
})();
</script>
