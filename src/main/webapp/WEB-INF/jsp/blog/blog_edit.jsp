<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<s:i18n name="com.wondersgroup.autumn.serviceaid.action.SysInformationAction">
	<!-- begin row -->
	<div class="row">
		<!-- begin col-12 -->
		<div class="col-md-12">
    	<!-- begin panel -->
             <div class="panel panel-inverse">
                 <div class="panel-body">
                     <form class="form-horizontal" method="post" id="baseFormInformation">
	    				<!-- 业务id  -->
						<input name="loginUserVo.id" id="id" type="hidden" value="<s:property value="loginUserVo.id"/>"/>
	    				
						<div class="form-group">
							<div class="control-group">
	                         	<!-- 收件人  -->
								<label class="col-md-12 control-label input-sm text-left"><s:text name="sendtoUser"/>*：</label>
								<div class="col-md-12">
									<div class="input-group">
										<input type="text" id="stSendtoUserNames" class="form-control input-sm" placeholder="<s:text name="sendtoUser"/>" readonly/>
										<a href="javascript:;" onclick="selectUser();" class="btn btn-sm btn-primary input-group-addon"><i class="fa fa-users"></i></a>
										<input type="hidden" id="hidSendtoUserIds" name="sysInformationVo.stSendtoUser">
										<input type="hidden" id="hidSendtoNames" name="sysInformationVo.stSendtoName">
									</div>
								</div>
							</div>
							
							<div class="control-group">
	                         	<!-- 主题  -->
								<label class="col-md-12 control-label input-sm text-left"><s:text name="title"/>*：</label>
								<div class="col-md-12">
									<input type="text" id="title" name="sysInformationVo.stTitle" class="form-control input-sm" placeholder="<s:text name="title"/>" />
								</div>
							</div>
						</div>
						
						<div class="form-group">
							<div class="control-group">
	                         	<!-- 内容 -->
								<label class="col-md-12 control-label input-sm text-left"><s:text name="content"/>*：</label>
								<div class="col-md-12">
									<input type="hidden" id="hidContent" name="sysInformationVo.clContent">
									<textarea class="ckeditor" id="ckContent" rows="20"></textarea>
									<script>CKEDITOR.replace('ckContent');</script>
								</div>
							</div>
						</div>

                        <div class="form-group pull-right">
                             <div class="col-md-12 ">
                             	<a href="javascript:;" onclick="sendInformation();" class="btn btn-sm btn-primary">&nbsp;<s:text name="button_save"/>&nbsp;</a>
                             	<a href="javascript:;" id="btnCLose" class="btn btn-sm btn-default" data-dismiss="modal">&nbsp;<s:text name="button_close"/>&nbsp;</a>
                             </div>
                        </div>
                     </form>
                     
                     <!-- 引入公共的文件上传页面 -->
					 <s:include value="/WEB-INF/jsp/modules/common/attachment/upload_file.jsp" />
                 </div>
             </div>
             <!-- end panel -->
         </div>
         <!-- end col-12 -->
     </div>
     <!-- end row -->
    
	<script>
		$(document).ready(function() {
			//表单校验
			$("#baseFormInformation").validate();
			$("#baseFormInformation #stSendtoUserNames").rules('add', { required: true });
			$("#baseFormInformation #title").rules('add', { required: true, byteMaxLength:500 });
			$("#baseFormInformation #content").rules('add', { required: true });
		});
		
		//保存
		function sendInformation(){
			if ($('#baseFormInformation').valid()) {
				content = CKEDITOR.instances['ckContent'].getData();
		        if(content==null||content==""){  
		            alert("数据为空不能提交");
		            return;
		        } else{  
					$("#hidContent").val(content);
		        }
		        
		        Common.showConfirm('<s:text name="confirm.submit"/>',function(){
					var data = $("#baseFormInformation").serialize();
					var dataAttach = $("#fileupload").serialize();
					$.ajax({
						url: "sysInformation.addSubmit.do",
						type: "post",
						data: data + "&" + dataAttach,
						success:function(json) {
							Common.showMsg(json, function(){
								queryInformation();
								$("#btnCLose").click();
							});
						}
					});
		        });
			}
		}
		
		// 选取用户
		function selectUser() {
			var url = "userCustom.userSelectInit.do?ran="+getTimeLong();
			var title = '<s:text name="tool_select"/>';
			Common.showNormalDialog(url, title);
		}
		// 设置用户Id和名字列表
		function setUserIdNames(ids, names) {
			$("#hidSendtoUserIds").val(ids);
			$("#stSendtoUserNames").val(names);
			$("#hidSendtoNames").val(names);
		}
	</script>
</s:i18n>
