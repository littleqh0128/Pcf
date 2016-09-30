<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!-- begin #header -->
<div id="header" class="header navbar navbar-default navbar-fixed-top">
	<!-- begin container-fluid -->
	<div class="container-fluid">
		<!-- begin mobile sidebar expand / collapse button -->
		<div class="navbar-header">
			<a href="login.index.do" class="navbar-brand"><span class="navbar-logo"></span>个人平台</a>
			<button type="button" class="navbar-toggle" data-click="sidebar-toggled">
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			</button>
		</div>
		<!-- end mobile sidebar expand / collapse button -->
		
		<!-- begin header navigation right -->
		<ul class="nav navbar-nav navbar-right">
			<li>
				<form class="navbar-form full-width">
					<div class="form-group">
						<input type="text" class="form-control" placeholder="Enter keyword" />
						<button type="submit" class="btn btn-search"><i class="fa fa-search"></i></button>
					</div>
				</form>
			</li>
			<li class="dropdown">
				<a href="javascript:;" data-toggle="dropdown" class="dropdown-toggle f-s-14">
					<i class="fa fa-bell-o"></i>
					<span class="label"><span class="spanMsgUnread">0</span></span>
				</a>
				<ul class="dropdown-menu media-list pull-right animated fadeInDown" id="ulUnread">
	                <li class="dropdown-header">未读消息 (<span class="spanMsgUnread">0</span>)</li>
	                <li class="text-center">
	                    <a href="javascript:;" onclick="showInformationList();">查看更多</a>
	                </li>
				</ul>
			</li>
			<li class="dropdown navbar-user">
				<a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown">
					<span class="hidden-xs"><s:property value="#session['security.login.user'].userName"/></span> <b class="caret"></b>
				</a>
	
				<ul class="dropdown-menu animated fadeInLeft">
					<li class="arrow"></li>
					<li><a href="javascript:;">个人信息</a></li>
					<li><a href="javascript:;">修改密码</a></li>
					<li class="divider"></li>
					<li><a href="login.logout.do">安全退出</a></li>
				</ul>
			</li>
		</ul>
		<!-- end header navigation right -->
	</div>
	<!-- end container-fluid -->
</div>
<!-- end #header -->

<script>
	$(document).ready(function() {
		getUnReadInformation();
	});

	// 定时获取未读消息
	function getUnReadInformation() {
		$.ajax({
			url: "sysInformationSub.getUnReadInformation.do",
			type: "post",
			success:function(json) {
				$(".spanMsgUnread").html(json.unReadCnt);
				$("#ulUnread .media").remove(); 
				for(var index = 0; index < json.data.length; index++) {
					//<div class='media-left'><i class='fa fa-envelope media-object bg-blue'></i></div>
					var ele = "<li class='media'><a href='javascript:;' onclick=\"showHeaderInformationById('" + json.data[index].stId + "')\">";
					ele += "<div class='media-body'><h6 class='media-heading' title=\"" + json.data[index].stTitle + "\">" + json.data[index].stSubmiter + "："+json.data[index].stTitleSimple + "</h6><div class='text-muted f-s-11'>" + json.data[index].dtSubmit + "</div>";
					ele += "</div></a></li>";
					$("#ulUnread .dropdown-header").after(ele);
				}
			}
		});
		setTimeout("getUnReadInformation()",600000);
	}
	
	// 显示更多消息
	function showInformationList() {
		window.location.href = "sysInformation.init.do";
	}
	
	// 显示消息内容
	function showHeaderInformationById(id) {
		alert(id);
		var url = "sysInformation.showBySubId.do?ran="+getTimeLong()+"&sysInformationVo.stInformationSubId="+id;
		var title = '<s:text name="tool_detail"/>';
		Common.showWideDialog(url, title);
	}
</script>