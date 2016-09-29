/**
 * Protect window.console method calls. eg. console is not defined on IE unless
 * dev tools are open, and IE doesn't define console
 *
 * @author zhangxiong
 */
!(function( root, factory ) {
  if ( typeof define === 'function' && define.amd ) {
    // AMD. Register as an anonymous module.
    define([ 'jquery' ], factory);
  }
  else {
    // Browser globals
    root.console = factory(root.jQuery);
  }
}(this, function( $ ) {
  "use strict";
  
  if ( !window.console ) {
    window.console = {};
  }
  
  // union of Chrome, Firefox, IE and safari console methods
  var methods = [
    "log", "info", "warn", "error", "debug", "trace", "dir",
    "group", "groupCollapsed", "groupEnd", "time", "timeEnd", "profile",
    "profileEnd", "dirxml", "assert", "count", "markTimeline", "timeStamp",
    "clear"
  ];
  
  for ( var i = 0; i < methods.length; i++ ) {
    if ( !window.console[ methods[ i ] ] ) {
      window.console[ methods[ i ] ] = function() {
        // var args = Array.prototype.splice.call(arguments, 0,
        // arguments.length);
        // alert( args.join(',') );
      };
    }
  }
  
  return window.console;
}));