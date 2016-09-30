package com.wondersgroup.pcf.common.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.struts2.json.annotations.JSON;

import com.wondersgroup.pcf.system.model.vo.LoginUserVo;

public class BaseModel {

	/* 封装返回的消息 */
	private List<Map<String, Object>> messageList = null;
	/* 成功标识 */
	private Boolean flag;
	/* 返回业务表主键 */
	private String[] businessId;
	
	/* 封装session用户 */
	private LoginUserVo sessionUser = null;
	
	public List<Map<String, Object>> getMessageList() {
		if (messageList == null) {
			messageList = new ArrayList<Map<String, Object>>();
		}
		return messageList;
	}
	
	public void setMessageList(List<Map<String, Object>> messageList) {
		this.messageList = messageList;
	}
	
	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}
	
	public String[] getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String[] businessId) {
		this.businessId = businessId;
	}
	
	@JSON(serialize = false)
	public LoginUserVo getSessionUser() {
		return sessionUser;
	}

	public void setSessionUser(LoginUserVo sessionUser) {
		this.sessionUser = sessionUser;
	}
}
