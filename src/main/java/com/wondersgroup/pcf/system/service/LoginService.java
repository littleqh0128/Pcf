package com.wondersgroup.pcf.system.service;

import com.wondersgroup.pcf.system.model.SysModel;

public interface LoginService {
	
	/**
	 * 用户认证
	 * @param sysModel 用户信息
	 * @return
	 * @throws Exception
	 */
	public void authentication(SysModel sysModel) throws Exception;
}
