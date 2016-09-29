<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!-- begin #sidebar -->
<div id="sidebar" class="sidebar sidebar-grid">
	<!-- begin sidebar scrollbar -->
	<div data-scrollbar="true" data-height="100%">
		<!-- begin sidebar user -->
		<ul class="nav">
			<li class="nav-profile">
				<div class="info">
					欢迎：<s:property value="#session['security.login.user'].userName"/>
					<small><s:property value="#session['security.login.user'].organizationNodeVo.name"/></small>
				</div>
			</li>
		</ul>
            	
		<!-- end sidebar user -->
		<!-- begin sidebar nav -->
		<ul class="nav" id="ulLinkMenu">
			<!-- 一级菜单 -->
			<s:iterator value="#session['security.login.user'].arrMenuList" var="stLv1">
			<li class='<s:property value="#stLv1.hasSub"/> <s:property value="#stLv1.expand"/> <s:property value="#stLv1.active"/>'>
				
				<a class="linkMenu" id="linkMenu<s:property value="#stLv1.id"/>" code="<s:property value="#stLv1.code"/>" title='<s:property value="#stLv1.resourceName"/>' parentMenuId="<s:property value="#stLv1.parentMenuId"/>" linkPath="<s:property value="#stLv1.linkPath"/>" level="1" href="javascript:;">
				<s:if test="#stLv1.childMenuList.size()>0"><b class="caret pull-right"></b><i class="fa fa-align-left"></i></s:if>
				<span><s:property value="#stLv1.resourceName"/></span>
				</a>

				<s:if test="#stLv1.childMenuList.size()>0">
				<!-- 二级菜单 -->
				<ul class="sub-menu" style='<s:property value="#stLv1.block"/>'>
							
			  		<s:iterator value="#stLv1.childMenuList" var="stLv2">
					<li class='<s:property value="#stLv2.hasSub"/> <s:property value="#stLv2.expand"/> <s:property value="#stLv2.active"/>'>
						<a class="linkMenu" id="linkMenu<s:property value="#stLv2.id"/>" code="<s:property value="#stLv2.code"/>" title='<s:property value="#stLv2.resourceName"/>' parentMenuId="<s:property value="#stLv2.parentMenuId"/>" linkPath="<s:property value="#stLv2.linkPath"/>" level="2" href="javascript:;">
						<s:if test="#stLv2.childMenuList.size()>0"><b class="caret pull-right"></b><i class="fa fa-align-left"></i></s:if>
						<span><s:property value="#stLv2.resourceName"/></span>
						</a>

						<s:if test="#stLv2.childMenuList.size()>0">
						<!-- 三级菜单 -->
						<ul class="sub-menu" style='<s:property value="#stLv2.block"/>'>
							
				  			<s:iterator value="#stLv2.childMenuList" var="stLv3">
							<li class='<s:property value="#stLv3.hasSub"/> <s:property value="#stLv3.expand"/> <s:property value="#stLv3.active"/>'>
								<a class="linkMenu" id="linkMenu<s:property value="#stLv3.id"/>" code="<s:property value="#stLv3.code"/>" title='<s:property value="#stLv3.resourceName"/>' parentMenuId="<s:property value="#stLv3.parentMenuId"/>" linkPath="<s:property value="#stLv3.linkPath"/>" level="3" href="javascript:;">
								<s:if test="#stLv3.childMenuList.size()>0"><b class="caret pull-right"></b><i class="fa fa-align-left"></i></s:if>
								<span><s:property value="#stLv3.resourceName"/></span>
								</a>
							</li>
							</s:iterator>
						</ul>
						</s:if>
					</li>
					</s:iterator>
			  	</ul>
				</s:if>
		  	</li>
		  	</s:iterator>
			
	        <!-- begin sidebar minify button -->
			<li><a href="javascript:;" class="sidebar-minify-btn" data-click="sidebar-minify"><i class="fa fa-angle-double-left"></i></a></li>
	        <!-- end sidebar minify button -->
		</ul>
		<!-- end sidebar nav -->
	</div>
	<!-- end sidebar scrollbar -->
</div>
<div class="sidebar-bg"></div>
<!-- end #sidebar -->

<script>
	$(document).ready(function() {
		$("[data-click=sidebar-minify]").click();
		$("a.linkMenu").click(function(){
			menuPageJump($(this));
		});
	});
	// 根据菜单id跳转
	function pageJumpByMenuId(menuId) {
		menuPageJump($("#"+menuId));
	}
	// 根据菜单code跳转
	function pageJumpByMenuCode(menuCode) {
		
		$("#ulLinkMenu").find("a.linkMenu").each(function() {
			if ($(this).attr("code") == menuCode) {
				menuPageJump($(this));
			}
		});
	}
	// 根据菜单jquery对象跳转
	function menuPageJump(menu) {
		
		var id = menu.attr("id");
		var linkPath = menu.attr("linkPath");

		if (linkPath != null && linkPath.length > 0) {
			if (linkPath.indexOf("?") != -1)
				linkPath += "&";
			else
				linkPath += "?";
			linkPath = linkPath + "currentMenuId="+id;
			window.location.href = linkPath;
		}
	}
</script>