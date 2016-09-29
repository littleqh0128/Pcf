package com.wondersgroup.pcf.system.dao;

import com.wondersgroup.framework.core5.dao.AbstractHibernateExtdDao;
import com.wondersgroup.pcf.system.model.bo.SysUser;

public interface SysUserDao extends AbstractHibernateExtdDao<SysUser> {
	
	/**
	 * 通过主键id获取对象
	 * @param stId 用户Id
	 * @return
	 */
	SysUser loadById(String stId);
	
	/**
	 * 通过登录名获取对象
	 * @param stLoginName 登录名
	 * @return
	 */
	SysUser loadByLoginName(String stLoginName);
}
