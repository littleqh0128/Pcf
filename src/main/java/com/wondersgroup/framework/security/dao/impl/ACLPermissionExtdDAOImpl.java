package com.wondersgroup.framework.security.dao.impl;

import com.wondersgroup.framework.core.dao.impl.AbstractHibernateExtdDAOImpl;
import com.wondersgroup.framework.security.bo.ACLOperation;
import com.wondersgroup.framework.security.bo.ACLPermission;
import com.wondersgroup.framework.security.bo.ACLResource;
import com.wondersgroup.framework.security.bo.SecurityRole;
import com.wondersgroup.framework.security.dao.ACLPermissionExtdDAO;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class ACLPermissionExtdDAOImpl extends AbstractHibernateExtdDAOImpl implements ACLPermissionExtdDAO
{
  protected Class getEntityClass()
  {
    return ACLPermission.class;
  }
  
  public List getACLPermissionsWithRoleAndResource(SecurityRole role, ACLResource resource)
  {
    List result = getHibernateTemplate().find("select permission from SecurityRole role join role.aclPermissions permission where role = ? and permission.aclResource = ?", new Object[] { role, resource });
    
    return new ArrayList(new HashSet(result));
  }
  
  public ACLPermission getACLPermissionWithResourceAndOperation(ACLResource resource, ACLOperation operation)
  {
    List result = getHibernateTemplate().find("from ACLPermission aclPermission  where aclPermission.aclResource = ? and aclPermission.aclOperation = ? and aclPermission.dueTime is null", new Object[] { resource, operation });
    if (result.size() > 0) {
      return (ACLPermission)result.get(0);
    }
    return null;
  }
  
  public ACLPermission getACLTempPermissionWithResourceAndOperation(ACLResource resource, ACLOperation operation)
  {
    List result = getHibernateTemplate().find("from ACLPermission aclPermission  where aclPermission.aclResource = ? and aclPermission.aclOperation = ? and aclPermission.dueTime is not null", new Object[] { resource, operation });
    if (result.size() > 0) {
      return (ACLPermission)result.get(0);
    }
    return null;
  }
  
  public ACLPermission getACLPermissionWithResourceOperationAndTime(ACLResource resource, ACLOperation operation, Date startTime, Date dueTime)
  {
    List result = getHibernateTemplate().find("from ACLPermission aclPermission  where aclPermission.aclResource = ? and aclPermission.aclOperation = ? and aclPermission.startTime = ? and aclPermission.dueTime = ?", new Object[] { resource, operation, startTime, dueTime });
    if (result.size() > 0) {
      return (ACLPermission)result.get(0);
    }
    return null;
  }
}
