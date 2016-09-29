package com.wondersgroup.pcf.system.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.wondersgroup.pcf.common.base.BaseDaoImpl;
import com.wondersgroup.pcf.system.dao.SysUserDao;
import com.wondersgroup.pcf.system.model.bo.SysUser;

@Repository
public class SysUserDaoImpl extends BaseDaoImpl<SysUser> implements SysUserDao{

	/**
	 * {@inheritDoc}
	 * @throws Exception 
	 */
	public SysUser loadById(String stId) {
		//动态查询条件组装
		Criteria criteria = getSession().createCriteria(getEntityClass());
		//主键id
		criteria.add(Restrictions.eq("stId", stId));
		
		Object result = criteria.uniqueResult();
		return result != null ? (SysUser) result : null;
	}
	
	/**
	 * {@inheritDoc}
	 * @throws Exception 
	 */
	public SysUser loadByLoginName(String stLoginName) {
		//动态查询条件组装
		Criteria criteria = getSession().createCriteria(getEntityClass());
		//主键id
		criteria.add(Restrictions.eq("stLoginName", stLoginName));
		
		Object result = criteria.uniqueResult();
		return result != null ? (SysUser) result : null;
	}
}
