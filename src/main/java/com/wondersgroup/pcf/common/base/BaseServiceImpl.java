package com.wondersgroup.pcf.common.base;

import java.util.HashMap;

import com.wondersgroup.pcf.common.constants.MessageConst;

public class BaseServiceImpl {

	/**
	 * 在model中增加一个message
	 * @param model
	 * @param key
	 * @param parameters
	 */
	protected void addMessages(BaseModel model, String key, String[] parameters, boolean flag, String[] businessId) {
		
		// 构筑消息体
		HashMap<String, Object> message = new HashMap<String, Object>();
		message.put(MessageConst.MSG_KEY, key);
		message.put(MessageConst.MSG_ARGS, parameters);
		model.getMessageList().add(message);
		
		// 是否成功标志
		model.setFlag(flag);
		
		// 设置返回业务Id
		model.setBusinessId(businessId);
	}
	
	protected void addMessages(BaseModel model, String key, String[] parameters, boolean flag) {
		addMessages(model, key, parameters, flag, null);
	}

	protected void addMessages(BaseModel model, String key, boolean flag) {
		addMessages(model, key, null, flag, null);
	}
	
	protected void addMessages(BaseModel model, String key, boolean flag, String[] businessId) {
		addMessages(model, key, null, flag, businessId);
	}
}
