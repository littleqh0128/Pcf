/**
 * Forms Common Utils
 *
 * @author zhangxiong
 */
!(function( factory ) {
  if ( typeof define === 'function' && define.amd ) {
    // AMD. Register as an anonymous module.
    define([ 'jquery' ], factory);
  }
}(function( $ ) {
  "use strict";
  
  $.extend({
    
    /**
     * 根据FormID来提交指定Form
     *
     * Usage:
     *
     * require(['jquery', 'app/common/utils/forms' ], function( $, forms ) {
     * 
     *    // 根据FormID来提交指定Form
     *    forms.submitForm("todoForm");
     * 
     *    // 根据FormID来提交指定Form, 同时指定参数(同名参数会被替换)
     *    forms.submitForm("todoForm", {
     *      'param1' : 'value1',
     *      'param2' : [1, 2, 3]
     *      .....
     *    });
     * });
     *
     * @param {String} formId
     *    表单ID
     * @param {Object} params
     *    表单提交参数
     * @return jQuery
     */
    submitForm: function( formId, params ) {
      var $form = $('#' + formId);
      
      if ( params && $.isPlainObject(params) ) {
        $.each(params, function( key, value ) {
          // remove element if specified name element is presented
          $form.find('[name="' + key + '"]').remove();
          
          if ( $.isArray(value) ) {
            $.each(value, function( _i, _val ) {
              $('<input>', {
                'type': 'hidden',
                'name': key
              }).val(_val).appendTo($form);
            });
          }
          else {
            $('<input>', {
              'type': 'hidden',
              'name': key
            }).val(value).appendTo($form);
          }
        });
      }
      
      // submit form
      $form.submit();
      
      // remove form params if submit to new window
      if ( ($form.attr('target') || '').toLowerCase() === '_blank' ) {
        $.each(params, function( key ) {
          // remove element if specified name element is presented
          $form.find('[name="' + key + '"]').remove();
        });
      }
    }
  });
  
  /**
   * 根据按钮属性,指定form的action并提交
   *
   * Usage:
   *
   *
   * <form id="todoForm"></form>
   * <a class="btn" data-form-action="<s:url action="test" namspace="/demo" />">
   *
   * require(['jquery', 'app/common/utils/forms' ], function( $, forms ) {
   * 
   *    forms.bindMultiActionForm("todoForm", '.btn');
   *    // or
   *    $( '#todoForm' ).bindMultiActionForm( '.btn' );
   * });
   *
   * @param {String} btnSelector
   *    按钮jQuery表达式
   * @return jQuery
   */
  $.fn.bindMultiActionForm = function( btnSelector ) {
    var $form = $(this);
    
    $(btnSelector).on('click', function() {
      var $button          = $(this),
          formAction       = $button.data('formAction'),
          originFormAction = $form.attr('action');
      
      // event skip if action missing  
      if ( $.trim(formAction).length === 0 ) {
        return;
      }
      
      // submit form
      $form.attr('action', formAction).submit();
      
      if ( originFormAction ) $form.attr('action', originFormAction);
    });
  };
  
  return {
    submitForm: $.submitForm,
    
    bindMultiActionForm: function( formId, btnSelector ) {
      $('#' + formId).bindMultiActionForm(btnSelector);
    }
  };
}));