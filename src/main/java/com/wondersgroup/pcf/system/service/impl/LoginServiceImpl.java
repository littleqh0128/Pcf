package com.wondersgroup.pcf.system.service.impl;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondersgroup.framework.security.exception.BadCredentialsException;
import com.wondersgroup.pcf.common.base.BaseServiceImpl;
import com.wondersgroup.pcf.common.constants.tables.SystemInfo.UserStatus;
import com.wondersgroup.pcf.common.utils.MD5Util;
import com.wondersgroup.pcf.system.dao.SysUserDao;
import com.wondersgroup.pcf.system.model.SysModel;
import com.wondersgroup.pcf.system.model.bo.SysUser;
import com.wondersgroup.pcf.system.service.LoginService;

@Service
@Transactional
public class LoginServiceImpl extends BaseServiceImpl implements LoginService{
	
	@Autowired
	private SysUserDao sysUserDao;
	
	/**
	 * {@inheritDoc}
	 * @throws Exception 
	 */
	@Override
	public void authentication(SysModel sysModel) throws Exception {
		
		// 登录名
		String stLoginName = sysModel.getSysUserVo().getStLoginName();
		// 密码(MD5加密)
		String stPassword = MD5Util.MD5(sysModel.getSysUserVo().getStPassword());

		// 根据登录名获取用户信息
		SysUser sysUser = sysUserDao.loadByLoginName(stLoginName);
		
		// 用户未找到
		if (sysUser == null)
			throw new BadCredentialsException(BadCredentialsException.USER_NOT_EXISTED);
		
		// 密码不正确
		if (!sysUser.getStPassword().equals(stPassword))
			throw new BadCredentialsException(BadCredentialsException.PASSWORD_ERROR);
		
		// 状态不正常
		if (UserStatus.UNNORMAL.getValue().equals(sysUser.getStStatus()))
			throw new BadCredentialsException(BadCredentialsException.USER_STATUS_UNNORMAL);

		// 设置返回信息
		BeanUtils.copyProperties(sysModel.getSysUserVo(), sysUser);
		sysModel.getSysUserVo().setStPassword(null);
	}
}
