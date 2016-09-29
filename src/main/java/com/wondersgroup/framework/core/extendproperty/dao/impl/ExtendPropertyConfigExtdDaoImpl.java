package com.wondersgroup.framework.core.extendproperty.dao.impl;

import com.wondersgroup.framework.core.bo.hibernate.HqlParameter;
import com.wondersgroup.framework.core.dao.impl.AbstractHibernateExtdDAOImpl;
import com.wondersgroup.framework.core.extendproperty.bo.ExtendPropertyConfig;
import com.wondersgroup.framework.core.extendproperty.dao.ExtendPropertyConfigExtdDao;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class ExtendPropertyConfigExtdDaoImpl extends AbstractHibernateExtdDAOImpl implements ExtendPropertyConfigExtdDao
{
  protected Class getEntityClass()
  {
    return ExtendPropertyConfig.class;
  }
  
  public List<ExtendPropertyConfig> getAllExtendPropertyConfig()
  {
    String hql = "select config from ExtendPropertyConfig config";
    return (List<ExtendPropertyConfig>)getHibernateTemplate().find(hql);
  }
  
  public ExtendPropertyConfig getExtendPropertyConfigByClass(String className)
  {
    List<HqlParameter> args = new ArrayList();
    String hql = "select config from ExtendPropertyConfig config where config.className = :className";
    args.add(new HqlParameter("className", className));
    List<ExtendPropertyConfig> list = findByHQL(hql, args);
    if (!list.isEmpty()) {
      return (ExtendPropertyConfig)list.get(0);
    }
    return null;
  }
}
