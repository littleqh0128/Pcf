package com.wondersgroup.framework.security.dao;

import com.wondersgroup.framework.core.dao.AbstractHibernateExtdDAO;
import com.wondersgroup.framework.security.bo.ACLOperation;
import com.wondersgroup.framework.security.bo.ACLResource;
import com.wondersgroup.framework.security.bo.ACLResourceType;
import com.wondersgroup.framework.security.bo.SecurityRole;
import java.util.List;

public abstract interface ACLOperationExtdDAO extends AbstractHibernateExtdDAO
{
  public abstract ACLOperation getOpertionByCode(ACLResourceType paramACLResourceType, String paramString);
  
  public abstract List getRevokeOperationWithRoleAndResource(SecurityRole paramSecurityRole, ACLResource paramACLResource);
  
  public abstract List getACLOperationWithRoleAndResource(SecurityRole paramSecurityRole, ACLResource paramACLResource);
  
  public abstract List getPublishOperationWithRoleAndResource(SecurityRole paramSecurityRole, ACLResource paramACLResource);
}
