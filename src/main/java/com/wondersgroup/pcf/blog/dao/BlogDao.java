package com.wondersgroup.pcf.blog.dao;

import com.wondersgroup.framework.core5.dao.AbstractHibernateExtdDao;
import com.wondersgroup.pcf.blog.model.bo.Blog;

public interface BlogDao extends AbstractHibernateExtdDao<Blog> {
	
	/**
	 * 通过主键id获取对象
	 * @param stId 用户Id
	 * @return
	 */
	Blog loadById(String stId);
}
