package com.wondersgroup.framework.security.dao;

import com.wondersgroup.framework.core.dao.AbstractHibernateExtdDAO;
import com.wondersgroup.framework.organization.bo.OrganTree;
import com.wondersgroup.framework.security.bo.ACLOperation;
import com.wondersgroup.framework.security.bo.ACLResource;
import com.wondersgroup.framework.security.bo.SecurityGroup;
import com.wondersgroup.framework.security.bo.SecurityRole;
import com.wondersgroup.framework.security.bo.SecurityUser;
import java.util.List;

public abstract interface RoleExtdDAO extends AbstractHibernateExtdDAO
{
  public abstract SecurityRole loadDefaultRoleWithUser(SecurityUser paramSecurityUser);
  
  public abstract SecurityRole loadDefaultRoleWithGroup(SecurityGroup paramSecurityGroup);
  
  /**
   * @deprecated
   */
  public abstract List findPermissionWithResourceAndOperation(ACLResource paramACLResource, ACLOperation paramACLOperation);
  
  public abstract List findSecurityRoleWithResourceAndOperation(ACLResource paramACLResource, ACLOperation paramACLOperation);
  
  public abstract List findRevokeSecurityRoleWithResourceAndOperation(ACLResource paramACLResource, ACLOperation paramACLOperation);
  
  public abstract SecurityRole loadDefaultRoleWithNewGroup(SecurityGroup paramSecurityGroup);
  
  public abstract SecurityRole loadDefaultRoleWithNewUser(SecurityUser paramSecurityUser);
  
  public abstract List loadAllRoleFromUser(SecurityUser paramSecurityUser);
  
  public abstract List loadACLPermissionFromRole(SecurityRole paramSecurityRole);
  
  public abstract List loadRevokeACLPermissionFromRole(SecurityRole paramSecurityRole);
  
  public abstract List findPermission(List paramList, String paramString1, String paramString2);
  
  public abstract List loadAllRolesFromUser(SecurityUser paramSecurityUser, OrganTree paramOrganTree);
  
  public abstract List loadAdminRolesFromUser(SecurityUser paramSecurityUser, OrganTree paramOrganTree);
  
  public abstract List findPermission(String paramString1, String paramString2, SecurityRole[] paramArrayOfSecurityRole);
  
  public abstract List findRevokePermission(String paramString1, String paramString2, SecurityRole[] paramArrayOfSecurityRole);
}
