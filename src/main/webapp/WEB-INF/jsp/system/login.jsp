<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html>
<!--[if IE 8]> <html lang="en" class="ie8"> <![endif]-->
<!--[if !IE]><!-->
<html lang="en">
<!--<![endif]-->
<head>
	<title>临时入口</title>
    <s:include value="/resources/include/meta.jsp" />
    <s:include value="/resources/include/css.jsp" />
	<s:include value="/resources/include/javascript.jsp"/>
	<script src="<%=request.getContextPath()%>/resources/js/app/apps.js"></script>
</head>

<script type="text/javascript">
	;(function() {
		"use strict";
		require([ 'jquery', 'domReady', 'bootstrap', 'js/common/utils/forms' ],function($, domReady, bootstrap, forms) {
			domReady(function(){
				$("#btnLogin").on('click',function(){
					//  打开文件下载页面 
					forms.submitForm("loginForm");
				});
				App.init();
			});
		});
	})();
</script>

<body class="pace-top">

	<!-- begin #page-loader -->
	<div id="page-loader" class="fade in"><span class="spinner"></span></div>
	<!-- end #page-loader -->
	
	<!-- begin #page-container -->
	<div id="page-container" class="fade">
	
	    <!-- begin login -->
        <div class="login login-with-news-feed">
            <!-- begin news-feed -->
            <div class="news-feed">
                <div class="news-image">
                    <img src="<%=request.getContextPath()%>/resources/image/login-bg/bg-7.jpg" data-id="login-cover-image" alt="" />
                </div>
            </div>
            <!-- end news-feed -->
            <!-- begin right-content -->
            <div class="right-content">
                <!-- begin login-header -->
                <div class="login-header">
                
                    <s:include value="/WEB-INF/jsp/base/message.jsp"/>

                    <div class="brand">
                        <span class="logo"></span> 临时入口
                    </div>
                    <div class="icon">
                        <i class="fa fa-sign-in"></i>
                    </div>
                </div>
                <!-- end login-header -->
                <!-- begin login-content -->
                <div class="login-content">
                    <form id="loginForm" action="system.check.do" method="POST" class="margin-bottom-0">
                        <div class="form-group m-b-15">
                            <input type="text" class="form-control input-lg" placeholder="用户名" name="sysUserVo.stLoginName"/>
                        </div>
                        <div class="form-group m-b-15">
                            <input type="password" class="form-control input-lg" placeholder="密码" name="sysUserVo.stPassword"/>
                        </div>
                        <div class="checkbox m-b-30">
                            <label>
                                <input type="checkbox" /> 记住我
                            </label>
                        </div>
                        <div class="login-buttons">
                            <button type="submit" class="btn btn-success btn-block btn-lg" id="btnLogin">登 录</button>
                        </div>
                        <hr />
                        <p class="text-center text-inverse">
                            Edit by qian
                        </p>
                    </form>
                </div>
                <!-- end login-content -->
            </div>
            <!-- end right-container -->
        </div>
        <!-- end login -->
	</div>
	<!-- end page container -->
</body>
</html>
