<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="hasActionErrors()">
  <br/>
  <div class="alert alert-warning alert-error" role="alert">
    <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
    <ul>
      <s:iterator value="actionErrors" var="error">
        <li><s:property value="error"/></li>
      </s:iterator>
    </ul>
  </div>
</s:if>

<s:if test="hasActionMessages()">
  <div class="alert alert-warn">
    <s:actionmessage/>
  </div>
</s:if>


