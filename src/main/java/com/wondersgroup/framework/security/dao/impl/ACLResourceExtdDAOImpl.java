package com.wondersgroup.framework.security.dao.impl;

import com.wondersgroup.framework.core.dao.impl.AbstractHibernateExtdDAOImpl;
import com.wondersgroup.framework.security.bo.ACLResource;
import com.wondersgroup.framework.security.bo.ACLResourceType;
import com.wondersgroup.framework.security.bo.SecurityRole;
import com.wondersgroup.framework.security.dao.ACLResourceExtdDAO;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class ACLResourceExtdDAOImpl extends AbstractHibernateExtdDAOImpl implements ACLResourceExtdDAO
{
  protected Class getEntityClass()
  {
    return ACLResource.class;
  }
  
  public ACLResource getResourceByTypeAndNativeId(ACLResourceType type, String nativeId)
  {
    List result = getHibernateTemplate().find("from ACLResource res where res.aclResourceType = ? and res.nativeResourceId = ?", new Object[] { type, nativeId });
    if (result.size() > 0) {
      return (ACLResource)result.get(0);
    }
    return null;
  }
  
  public ACLResource[] getACLResourceByTypeAndRole(SecurityRole role, ACLResourceType type)
  {
    List result = getHibernateTemplate().find("select distinct permission.aclResource from SecurityRole role join role.aclPermissions permission   where role= ? and permission.aclResource.aclResourceType= ?", new Object[] { role, type });
    
    return (ACLResource[])result.toArray(new ACLResource[result.size()]);
  }
  
  public ACLResource[] getRevokeACLResourceByTypeAndRole(SecurityRole role, ACLResourceType type)
  {
    List result = getHibernateTemplate().find("select distinct revoke.aclResource from SecurityRole role join role.revokeAclPermissions revoke   where role= ? and revoke.aclResource.aclResourceType= ?", new Object[] { role, type });
    
    return (ACLResource[])result.toArray(new ACLResource[result.size()]);
  }
  
  public ACLResource[] getPublishACLResourceByTypeAndRole(SecurityRole role, ACLResourceType type)
  {
    List result = getHibernateTemplate().find("select distinct public.aclResource from SecurityRole role join role.publishAclPermissions public   where role= ? and public.aclResource.aclResourceType= ?", new Object[] { role, type });
    
    return (ACLResource[])result.toArray(new ACLResource[result.size()]);
  }
  
  public ACLResource[] getACLResourceByType(long resourceTypeId)
  {
    List result = getHibernateTemplate().find("from ACLResource res where res.aclResourceType.id = ? ", new Object[] { Long.valueOf(resourceTypeId) });
    
    return (ACLResource[])result.toArray(new ACLResource[result.size()]);
  }
  
  public ACLResource[] getAclResourceByNativeResourceId(String nativeResourceId)
  {
    List result = getHibernateTemplate().find("from ACLResource res where res.nativeResourceId = ? ", new Object[] { nativeResourceId });
    
    return (ACLResource[])result.toArray(new ACLResource[result.size()]);
  }
}
