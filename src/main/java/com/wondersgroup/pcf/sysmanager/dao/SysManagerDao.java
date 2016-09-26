package com.wondersgroup.pcf.sysmanager.dao;

import java.util.Map;

/**
 * 申请基本信息
 * @创建人:admin
 * @创建时间:2016-04-18 16:32:24
 * @修改人:
 * @修改时间:
 * @修改原因:
 */
public interface SysManagerDao extends AbstractHibernateExtdDao<InfApply> {
	
	/**
	 * 修改数据
	 * * @param infApply
	 */
	void update(InfApply infApply);
	
	/**
	 * 动态条件 分页查询
	 * @param condMap  动态条件
	 * @param pageNo   页码
	 * @param pageSize 每页显示条数 
	 */
	Page findByCondMap(Map condMap, int pageNo, int pageSize);
	
	/**
	 * 动态条件 分页查询总数
	 * @param condMap  动态条件
	 * @param pageNo   页码
	 * @param pageSize 每页显示条数 
	 */
	int findByCondMapCount(Map condMap);
	
	/**
	 * 通过主键id获取对象（关联查询时用）
	 * @param
	 * @return
	 */
	InfApply loadById(String stApplyId);
}
