package com.wondersgroup.framework.dict.dao;

import com.wondersgroup.framework.core.dao.AbstractHibernateExtdDAO;
import com.wondersgroup.framework.dict.bo.CodeInfo;
import java.util.Collection;
import java.util.List;

public abstract interface CodeInfoExtdDAO extends AbstractHibernateExtdDAO
{
  public abstract Collection findCodeInfoByName(String paramString);
  
  public abstract Collection findCodeInfoByCodeType(String paramString1, String paramString2);
  
  public abstract List findCodeInfoByCodeType(String paramString1, String paramString2, String paramString3, String paramString4);
  
  @Deprecated
  public abstract Collection getTopCodeInfo();
  
  @Deprecated
  public abstract Collection getChildsByParentCode(String paramString);
  
  public abstract CodeInfo getCodeInfoByCode(String paramString1, String paramString2);
}
