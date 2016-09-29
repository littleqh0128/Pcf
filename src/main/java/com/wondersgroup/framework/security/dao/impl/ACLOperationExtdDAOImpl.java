package com.wondersgroup.framework.security.dao.impl;

import com.wondersgroup.framework.core.dao.impl.AbstractHibernateExtdDAOImpl;
import com.wondersgroup.framework.security.bo.ACLOperation;
import com.wondersgroup.framework.security.bo.ACLResource;
import com.wondersgroup.framework.security.bo.ACLResourceType;
import com.wondersgroup.framework.security.bo.SecurityRole;
import com.wondersgroup.framework.security.dao.ACLOperationExtdDAO;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class ACLOperationExtdDAOImpl extends AbstractHibernateExtdDAOImpl implements ACLOperationExtdDAO
{
  protected Class getEntityClass()
  {
    return ACLOperation.class;
  }
  
  public ACLOperation getOpertionByCode(ACLResourceType resType, String code)
  {
    List result = getHibernateTemplate().find("select opertion from ACLOperation opertion where opertion.code = ? and opertion.aclResourceType = ? and opertion.removed = 0", new Object[] { code, resType });
    if ((result == null) || (result.size() == 0)) {
      return null;
    }
    return (ACLOperation)result.get(0);
  }
  
  public List getRevokeOperationWithRoleAndResource(SecurityRole role, ACLResource resource)
  {
    List result = getHibernateTemplate().find("select distinct revoke.aclOperation  from  SecurityRole role join role.revokeAclPermissions revoke  where role=? and revoke.aclResource= ?", new Object[] { role, resource });
    
    return result;
  }
  
  public List getACLOperationWithRoleAndResource(SecurityRole role, ACLResource resource)
  {
    List result = getHibernateTemplate().find("select distinct permission.aclOperation  from  SecurityRole role join role.aclPermissions permission  where role=? and permission.aclResource= ?", new Object[] { role, resource });
    
    return result;
  }
  
  public List getPublishOperationWithRoleAndResource(SecurityRole role, ACLResource resource)
  {
    List result = getHibernateTemplate().find("select distinct public.aclOperation  from  SecurityRole role join role.publishAclPermissions public  where role=? and public.aclResource.nativeResourceId= ?", new Object[] { role, resource.getNativeResourceId() });
    
    return result;
  }
}
