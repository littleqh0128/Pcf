package com.wondersgroup.pcf.common.interceptor;

import java.io.PrintWriter;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.wondersgroup.framework.common.web.constant.LoginConstant;
import com.wondersgroup.pcf.common.base.BaseModel;

/**
 * 公共的拦截器，session控制
 */
public class CommonInterceptor extends AbstractInterceptor {
	private static final long serialVersionUID = 1L;
	
	private String excludeActionName;//剔除的拦截方法
	
	@SuppressWarnings("unchecked")
	@Override
	public String intercept(ActionInvocation action) throws Exception {
		Object obj = action.getAction();
		//获取当前访问的action名字
		String actionName = action.getProxy().getActionName();
		ModelDriven<BaseModel> modelDriven = (ModelDriven<BaseModel>) obj;
		//获取baseModel
		BaseModel model = modelDriven.getModel();
		
		//过滤掉某些action
		Set<String> set = TextParseUtil.commaDelimitedStringToSet(excludeActionName);
		if(set.contains(actionName)){
			return action.invoke();
		}

		String result = action.invoke();
		return result;
	}

	public String getExcludeActionName() {
		return excludeActionName;
	}
	public void setExcludeActionName(String excludeActionName) {
		this.excludeActionName = excludeActionName;
	}
}