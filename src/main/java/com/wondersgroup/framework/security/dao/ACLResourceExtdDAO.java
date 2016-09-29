package com.wondersgroup.framework.security.dao;

import com.wondersgroup.framework.core.dao.AbstractHibernateExtdDAO;
import com.wondersgroup.framework.security.bo.ACLResource;
import com.wondersgroup.framework.security.bo.ACLResourceType;
import com.wondersgroup.framework.security.bo.SecurityRole;

public abstract interface ACLResourceExtdDAO extends AbstractHibernateExtdDAO
{
  public abstract ACLResource getResourceByTypeAndNativeId(ACLResourceType paramACLResourceType, String paramString);
  
  public abstract ACLResource[] getACLResourceByTypeAndRole(SecurityRole paramSecurityRole, ACLResourceType paramACLResourceType);
  
  public abstract ACLResource[] getRevokeACLResourceByTypeAndRole(SecurityRole paramSecurityRole, ACLResourceType paramACLResourceType);
  
  public abstract ACLResource[] getPublishACLResourceByTypeAndRole(SecurityRole paramSecurityRole, ACLResourceType paramACLResourceType);
  
  public abstract ACLResource[] getACLResourceByType(long paramLong);
  
  public abstract ACLResource[] getAclResourceByNativeResourceId(String paramString);
}
