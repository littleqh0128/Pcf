<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!-- begin page-header 
<h1 class="page-header">Dashboard <small>header small text goes here...</small></h1>-->
<!-- end page-header -->

<script type="text/javascript">
	;(function() {
		"use strict";
		require([ 'jquery', 'domReady', 'bootstrap', 'js/common/utils/forms' ],function($, domReady, bootstrap, forms) {
			domReady(function(){
				App.init();
			});
		});
	})();
</script>

<!-- begin row -->
<div class="row">
	<!-- begin col-3 -->
	<div class="col-md-3 col-sm-6">
		<div class="widget widget-stats bg-green">
			<div class="stats-icon"><i class="fa fa-desktop"></i></div>
			<div class="stats-info">
				<h4>网上申报</h4>
				<p id="spanOnlineAcceptCnt">0件</p>
			</div>
			<div class="stats-link">
				<a href="javascript:;" onclick="pageJumpByMenuCode('online_accept');">查看详细 <i class="fa fa-arrow-circle-o-right"></i></a>
			</div>
		</div>
	</div>
	<!-- end col-3 -->
	<!-- begin col-3 -->
	<div class="col-md-3 col-sm-6">
		<div class="widget widget-stats bg-blue">
			<div class="stats-icon"><i class="fa fa-chain-broken"></i></div>
			<div class="stats-info">
				<h4>窗口申报</h4>
				<p id="spanWindowReportCnt">0件</p>	
			</div>
			<div class="stats-link">
				<a href="javascript:;" onclick="pageJumpByMenuCode('window_accept');">查看详细 <i class="fa fa-arrow-circle-o-right"></i></a>
			</div>
		</div>
	</div>
	<!-- end col-3 -->
	<!-- begin col-3 -->
	<div class="col-md-3 col-sm-6">
		<div class="widget widget-stats bg-purple">
			<div class="stats-icon"><i class="fa fa-users"></i></div>
			<div class="stats-info">
				<h4>待处理</h4>
				<p id="spanTodoCnt">0件</p>
			</div>
			<div class="stats-link">
				<a href="javascript:;" onclick="pageJumpByMenuCode('record_unfinish_list');">查看详细 <i class="fa fa-arrow-circle-o-right"></i></a>
			</div>
		</div>
	</div>
	<!-- end col-3 -->
	<!-- begin col-3 -->
	<div class="col-md-3 col-sm-6">
		<div class="widget widget-stats bg-black">
			<div class="stats-icon"><i class="fa fa-binoculars"></i></div>
			<div class="stats-info">
				<h4>已办结</h4>
				<p id="spanApplyFinishedCnt">0件</p>
			</div>
			<div class="stats-link">
				<a href="javascript:;" onclick="pageJumpByMenuCode('apply_finished_list');">查看详细 <i class="fa fa-arrow-circle-o-right"></i></a>
			</div>
		</div>
	</div>
	<!-- end col-3 -->
</div>
<!-- end row -->
