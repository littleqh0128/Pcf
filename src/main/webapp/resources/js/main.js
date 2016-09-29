!(function( root, factory ) {
  if ( typeof define === 'function' && define.amd ) {
    define( [ 'jquery'
              ,'domReady'
            ], factory );
  } else {
    factory( root.jQuery );
  }
}(this, function( $ ) {
  'use strict';


  return $;
}));
