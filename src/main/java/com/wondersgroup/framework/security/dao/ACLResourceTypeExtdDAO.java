package com.wondersgroup.framework.security.dao;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.dao.AbstractHibernateExtdDAO;
import com.wondersgroup.framework.security.bo.ACLResourceType;
import java.util.List;
import java.util.Map;

public abstract interface ACLResourceTypeExtdDAO extends AbstractHibernateExtdDAO
{
  public abstract Page getOperationsByPage(ACLResourceType paramACLResourceType, int paramInt1, int paramInt2);
  
  public abstract List getOperations(ACLResourceType paramACLResourceType);
  
  public abstract Page findResourceTypesByPage(Map paramMap1, Map paramMap2, int paramInt1, int paramInt2);
}
