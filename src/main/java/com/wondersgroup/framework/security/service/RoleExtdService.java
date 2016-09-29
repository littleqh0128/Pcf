package com.wondersgroup.framework.security.service;

import com.wondersgroup.framework.organization.bo.OrganTree;
import com.wondersgroup.framework.security.bo.ACLOperation;
import com.wondersgroup.framework.security.bo.ACLPermission;
import com.wondersgroup.framework.security.bo.ACLResource;
import com.wondersgroup.framework.security.bo.SecurityGroup;
import com.wondersgroup.framework.security.bo.SecurityRole;
import com.wondersgroup.framework.security.bo.SecurityUser;
import java.util.List;

public abstract interface RoleExtdService
{
  public abstract void createDefaultRole(SecurityRole paramSecurityRole);
  
  public abstract SecurityRole loadRoleById(long paramLong);
  
  public abstract SecurityRole loadRoleByIdWithLazy(long paramLong, String[] paramArrayOfString);
  
  public abstract SecurityRole loadDefaultRoleWithUser(SecurityUser paramSecurityUser);
  
  public abstract SecurityRole loadDefaultRoleWithGroup(SecurityGroup paramSecurityGroup);
  
  public abstract SecurityRole[] loadAllRolesFromUser(SecurityUser paramSecurityUser);
  
  public abstract boolean isUserHasSpecificRoles(SecurityUser paramSecurityUser);
  
  public abstract boolean isRoleWithUser(SecurityUser paramSecurityUser, SecurityRole paramSecurityRole);
  
  public abstract ACLPermission[] loadPermissionFromRole(SecurityRole paramSecurityRole);
  
  public abstract SecurityRole[] getAllParentRoles(SecurityRole paramSecurityRole);
  
  public abstract void removeRole(long paramLong);
  
  public abstract void addRoleToUser(SecurityRole paramSecurityRole, SecurityUser paramSecurityUser);
  
  public abstract void revokeRoleFromUser(SecurityRole paramSecurityRole, SecurityUser paramSecurityUser);
  
  public abstract SecurityRole[] loadAllSpecialRoleFromUser(SecurityUser paramSecurityUser);
  
  public abstract List findSecurityRoleWithResourceAndOperation(ACLResource paramACLResource, ACLOperation paramACLOperation);
  
  public abstract SecurityRole[] loadAllRolesFromUser(SecurityUser paramSecurityUser, OrganTree paramOrganTree);
  
  public abstract SecurityRole[] loadAdminRolesFromUser(SecurityUser paramSecurityUser, OrganTree paramOrganTree);
}
