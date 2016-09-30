package com.wondersgroup.pcf.blog.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.wondersgroup.pcf.blog.dao.BlogDao;
import com.wondersgroup.pcf.blog.model.bo.Blog;
import com.wondersgroup.pcf.common.base.BaseDaoImpl;

@Repository
public class BlogDaoImpl extends BaseDaoImpl<Blog> implements BlogDao{

	/**
	 * {@inheritDoc}
	 * @throws Exception 
	 */
	public Blog loadById(String stId) {
		//动态查询条件组装
		Criteria criteria = getSession().createCriteria(getEntityClass());
		//主键id
		criteria.add(Restrictions.eq("stId", stId));
		
		Object result = criteria.uniqueResult();
		return result != null ? (Blog) result : null;
	}
}
