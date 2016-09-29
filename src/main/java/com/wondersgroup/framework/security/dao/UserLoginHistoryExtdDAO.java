package com.wondersgroup.framework.security.dao;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.dao.AbstractHibernateExtdDAO;
import java.util.List;
import java.util.Map;

public abstract interface UserLoginHistoryExtdDAO extends AbstractHibernateExtdDAO
{
  public abstract List findUserLogins(Map paramMap);
  
  public abstract Page findUserLoginsByPage(Map paramMap, int paramInt1, int paramInt2);
}
