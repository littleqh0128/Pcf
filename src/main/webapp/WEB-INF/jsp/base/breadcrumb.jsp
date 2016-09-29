<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!-- begin breadcrumb -->
<div class="row">
<ol class="breadcrumb pull-right">
	
	<s:if test="#session['security.login.user'].breadcrumbMenuIds != null && #session['security.login.user'].breadcrumbMenuIds != ''">
	<s:iterator value="#session['security.login.user'].breadcrumbMenuIds.split(',')" var="stMenuId" status='statusMenuId'>
		<s:iterator value="#session['security.login.user'].breadcrumbMenuUrls.split(',')" var="stMenuUrl" status='statusMenuUrl'>
			<s:if test="#statusMenuUrl.index == #statusMenuId.index">
				<li class="active">
				<s:if test='#stMenuUrl == "1"'>
					<a href="javascript:;" onclick="pageJumpByMenuId('<s:property value="#stMenuId"/>');">
					<s:iterator value="#session['security.login.user'].breadcrumbMenuNames.split(',')" var="stMenuName" status='statusMenuName'>
						<s:if test="#statusMenuName.index == #statusMenuId.index">
							<s:if test="#statusMenuId.index == (#session['security.login.user'].breadcrumbMenuIds.split(',').length - 1)">
								<!-- 最后一个为当前选中的菜单 -->
								<strong><s:property value="@java.net.URLDecoder@decode(#stMenuName,'utf-8')"/></strong>
							</s:if>
							<s:else>
								<s:property value="@java.net.URLDecoder@decode(#stMenuName,'utf-8')"/>
							</s:else>
						</s:if>
					</s:iterator>
					</a>
				</s:if>
				<s:else>
					<s:iterator value="#session['security.login.user'].breadcrumbMenuNames.split(',')" var="stMenuName" status='statusMenuName'>
						<s:if test="#statusMenuName.index == #statusMenuId.index">
							<s:property value="@java.net.URLDecoder@decode(#stMenuName,'utf-8')"/>
						</s:if>
					</s:iterator>
				</s:else>
				</li>
			</s:if>
		</s:iterator>
    </s:iterator>
    </s:if>
</ol>
</div>
<!-- end breadcrumb -->
	