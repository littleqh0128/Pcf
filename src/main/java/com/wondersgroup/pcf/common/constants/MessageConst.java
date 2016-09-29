package com.wondersgroup.pcf.common.constants;

public interface MessageConst {

	interface Error {
		
		// 用户名或密码错误, 请重新输入。
		String ERROR_LOGIN_INVALID = "error.login.invalid";
		
		// 用户状态无效, 请联系管理员。
		String ERROR_LOGIN_STATUS_UNNORMAL = "error.login.status.unnormal";
				
		// 请输入{0}！
		String ERRORS_REQUIRED = "errors.required";
	}
	
	interface UI {
		
		// 登录名
		String UI_LOGINNAME = "ui.loginName";
		
		// 密码
		String UI_PASSWORD = "ui.password";
	}
}
