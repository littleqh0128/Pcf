package com.wondersgroup.framework.security.dao;

import com.wondersgroup.framework.core.dao.AbstractHibernateExtdDAO;
import com.wondersgroup.framework.security.bo.ACLOperation;
import com.wondersgroup.framework.security.bo.ACLPermission;
import com.wondersgroup.framework.security.bo.ACLResource;
import com.wondersgroup.framework.security.bo.SecurityRole;
import java.util.Date;
import java.util.List;

public abstract interface ACLPermissionExtdDAO extends AbstractHibernateExtdDAO
{
  public abstract List getACLPermissionsWithRoleAndResource(SecurityRole paramSecurityRole, ACLResource paramACLResource);
  
  public abstract ACLPermission getACLPermissionWithResourceAndOperation(ACLResource paramACLResource, ACLOperation paramACLOperation);
  
  public abstract ACLPermission getACLTempPermissionWithResourceAndOperation(ACLResource paramACLResource, ACLOperation paramACLOperation);
  
  public abstract ACLPermission getACLPermissionWithResourceOperationAndTime(ACLResource paramACLResource, ACLOperation paramACLOperation, Date paramDate1, Date paramDate2);
}
