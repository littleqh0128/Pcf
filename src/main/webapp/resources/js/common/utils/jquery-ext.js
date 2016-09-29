/**
 * jQuery 扩展
 *
 * @author zhangxiong
 */
!(function( root, factory ) {
  if ( typeof define === 'function' && define.amd ) {
    // AMD. Register as an anonymous module.
    define([
      'jquery'
      , 'underscore'
    ], factory);
  }
}(this, function( $, _ ) {
  "use strict";
  
  //-----------------------------------
  // Lock Button
  // -----------------------------------
  $.extend($.fn, {
    
    /**
     * lock the button (Disabled)
     */
    lockButton: function() {
      return this.attr('disabled', 'disabled');
    },
    
    /**
     * unlock the button (enabled)
     */
    unLockButton: function() {
      return this.removeAttr('disabled');
    },
    
    /**
     * the button is locked status
     */
    isLockedButton: function() {
      return this.attr('disabled') === 'disabled';
    }
  });
  
  //-----------------------------------
  // Utils
  // -----------------------------------  
  
  /**
   *
   * $.isBlank(" ")       // true
   * $.isBlank("")        // true
   * $.isBlank("\n")      // true
   * $.isBlank("a")       // false
   * $.isBlank(null)      // true
   * $.isBlank(undefined) // true
   * $.isBlank(false)     // true
   * $.isBlank([])        // true
   *
   */
  $.isBlank = function( obj ) {
    return (!obj || $.trim(obj) === "");
  };
  
  $.isNotBlank = function( obj ) {
    return $.isBlank(obj) === false;
  };
  
  /**
   * Open a new window via "window.open()" function
   *
   * @param {String} url
   * @param {String} windowName
   * @param {Object} features
   * @return {Window} opened new window
   */
  $.openWindow = function( url, windowName, features ) {
    var popupCenter     = function( w, h ) {
          var
            // Fixes dual-screen position                        Most browsers      Firefox
            dualScreenLeft = window.screenLeft !== undefined ? window.screenLeft : screen.left,
            dualScreenTop  = window.screenTop !== undefined ? window.screenTop : screen.top,
        
            width          = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width,
            height         = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;
      
          var w    = _.isNumber(+w) ? +w : 100,  // default values
              h    = _.isNumber(+h) ? +h : 100,
              left = ((width / 2) - (w / 2)) + dualScreenLeft,
              top  = ((height / 2) - (h / 2)) + dualScreenTop;
      
          return {
            'left'    : left
            , 'top'   : top
            , 'height': h
            , 'width' : w
          };
        },
        defaultFeatures = {
          'position'    : 'center'  // center | custom
          , 'focus'     : true
          , 'left'      : '0'
          , 'top'       : '0'
          , 'height'    : '100'
          , 'width'     : '100'
          , 'location'  : false
          , 'menubar'   : false
          , 'resizable' : true
          , 'scrollbars': true
          , 'status'    : false
          , 'toolbar'   : false
        },
        mergedFeatures  = $.extend({}, defaultFeatures, features);
    
    if ( mergedFeatures.position && mergedFeatures.position === 'center' ) {
      mergedFeatures = $.extend({}, mergedFeatures, popupCenter(mergedFeatures.width, mergedFeatures.height));
    }
    
    var
      strFeature = _.chain(mergedFeatures)
        .map(function( v, k ) {
          return [ k, v || '' ].join('=');
        })
        .value()
        .join(','),
      newWindow  = window.open(url, windowName, strFeature);
    
    // Puts focus on the newWindow
    if ( !!mergedFeatures.focus ) {
      newWindow.focus();
    }
    
    return newWindow;
  };
  
  return $;
}));