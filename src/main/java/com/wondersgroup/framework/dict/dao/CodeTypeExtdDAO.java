package com.wondersgroup.framework.dict.dao;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.dao.AbstractHibernateExtdDAO;
import com.wondersgroup.framework.dict.bo.CodeType;
import java.util.Collection;
import java.util.Map;

public abstract interface CodeTypeExtdDAO extends AbstractHibernateExtdDAO
{
  public abstract Collection findCodeTypeByName(String paramString);
  
  public abstract CodeType getCodeType(String paramString);
  
  public abstract Page findCodeTypeByPageAndName(Map paramMap1, Map paramMap2, int paramInt1, int paramInt2);
}
