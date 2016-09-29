package com.wondersgroup.framework.core.extendproperty.dao;

import com.wondersgroup.framework.core.dao.AbstractHibernateExtdDAO;
import com.wondersgroup.framework.core.extendproperty.bo.ExtendPropertyConfig;
import java.util.List;

public abstract interface ExtendPropertyConfigExtdDao extends AbstractHibernateExtdDAO
{
  public abstract List<ExtendPropertyConfig> getAllExtendPropertyConfig();
  
  public abstract ExtendPropertyConfig getExtendPropertyConfigByClass(String paramString);
}
