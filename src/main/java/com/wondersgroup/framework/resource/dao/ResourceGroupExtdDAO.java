package com.wondersgroup.framework.resource.dao;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.dao.AbstractHibernateExtdDAO;
import com.wondersgroup.framework.security.bo.ACLResourceType;

public abstract interface ResourceGroupExtdDAO extends AbstractHibernateExtdDAO
{
  public abstract Page getResourceGroupsByTypeByPage(ACLResourceType paramACLResourceType, int paramInt1, int paramInt2);
}
