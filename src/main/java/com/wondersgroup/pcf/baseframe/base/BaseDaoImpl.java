package com.wondersgroup.pcf.baseframe.base;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wondersgroup.framework.core5.dao.impl.AbstractHibernateExtdDaoImpl;

public class BaseDaoImpl<T> extends AbstractHibernateExtdDaoImpl<T> {

	@Autowired
	public void setMySessionFactory(SessionFactory sessionFactory){
	     super.setSessionFactory(sessionFactory);
	}
}