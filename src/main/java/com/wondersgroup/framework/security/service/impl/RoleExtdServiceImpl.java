package com.wondersgroup.framework.security.service.impl;

import com.wondersgroup.framework.organization.bo.OrganTree;
import com.wondersgroup.framework.security.bo.ACLOperation;
import com.wondersgroup.framework.security.bo.ACLPermission;
import com.wondersgroup.framework.security.bo.ACLResource;
import com.wondersgroup.framework.security.bo.SecurityGroup;
import com.wondersgroup.framework.security.bo.SecurityRole;
import com.wondersgroup.framework.security.bo.SecurityUser;
import com.wondersgroup.framework.security.dao.RoleExtdDAO;
import com.wondersgroup.framework.security.dao.UserExtdDAO;
import com.wondersgroup.framework.security.exception.SecurityException;
import com.wondersgroup.framework.security.group.DynamicGroupProviderExtd;
import com.wondersgroup.framework.security.service.RoleExtdService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("RoleExtService")
@Transactional
public class RoleExtdServiceImpl implements RoleExtdService
{
  @Autowired
  private RoleExtdDAO roleDAO;
  @Autowired
  private UserExtdDAO userDAO;
  @Autowired
  private DynamicGroupProviderExtd dynamicGroupProvider;
  
  public void createDefaultRole(SecurityRole role)
  {
    this.roleDAO.save(role);
  }
  
  public SecurityRole loadRoleById(long id)
  {
    return (SecurityRole)this.roleDAO.load(new Long(id));
  }
  
  public SecurityRole loadRoleByIdWithLazy(long id, String[] propertyNames)
  {
    return (SecurityRole)this.roleDAO.loadWithLazy(new Long(id), propertyNames);
  }
  
  public SecurityRole loadDefaultRoleWithUser(SecurityUser user)
  {
    if (user == null) {
      throw new SecurityException("User Principal not existed.");
    }
    return this.roleDAO.loadDefaultRoleWithUser(user);
  }
  
  public SecurityRole loadDefaultRoleWithGroup(SecurityGroup group)
  {
    if (group == null) {
      throw new SecurityException("Group Principal not existed.");
    }
    return this.roleDAO.loadDefaultRoleWithGroup(group);
  }
  
  public SecurityRole[] loadAllRolesFromUser(SecurityUser user)
  {
    if (user == null) {
      throw new SecurityException("User Principal not existed.");
    }
    List result = this.roleDAO.loadAllRoleFromUser(user);
    SecurityGroup[] groups = this.dynamicGroupProvider.getDynamicGroupsFromUser(user);
    for (int i = 0; i < groups.length; i++) {
      result.add(this.roleDAO.loadDefaultRoleWithGroup(groups[i]));
    }
    return (SecurityRole[])result.toArray(new SecurityRole[result.size()]);
  }
  
  public boolean isUserHasSpecificRoles(SecurityUser user)
  {
    SecurityRole[] roles = loadAllSpecialRoleFromUser(user);
    if ((roles != null) && (roles.length > 0)) {
      return true;
    }
    return false;
  }
  
  public boolean isRoleWithUser(SecurityUser user, SecurityRole role)
  {
    SecurityRole[] roles = loadAllRolesFromUser(user);
    for (int i = 0; i < roles.length; i++) {
      if (role.equals(roles[i])) {
        return true;
      }
    }
    return false;
  }
  
  public ACLPermission[] loadPermissionFromRole(SecurityRole role)
  {
    SecurityRole roleWithLazy = (SecurityRole)this.roleDAO.loadWithLazy(new Long(role.getId()), new String[] { "aclPermissions" });
    
    Set permissions = roleWithLazy.getAclPermissions();
    return (ACLPermission[])permissions.toArray(new ACLPermission[permissions.size()]);
  }
  
  public SecurityRole[] getAllParentRoles(SecurityRole role)
  {
    return getAllParentRoles(role, null);
  }
  
  public void removeRole(long id)
  {
    SecurityRole role = (SecurityRole)this.roleDAO.load(new Long(id));
    this.roleDAO.remove(role);
  }
  
  public void addRoleToUser(SecurityRole role, SecurityUser user)
  {
    if (user == null) {
      throw new SecurityException("User Principal not existed.");
    }
    user.getRoles().add(role);
    this.userDAO.update(user);
  }
  
  public void revokeRoleFromUser(SecurityRole role, SecurityUser user)
  {
    if (user == null) {
      throw new SecurityException("User Principal not existed.");
    }
    user.getRoles().remove(role);
    this.userDAO.update(user);
  }
  
  public SecurityRole[] loadAllSpecialRoleFromUser(SecurityUser user)
  {
    Set result = new HashSet();
    SecurityRole[] roles = loadAllRolesFromUser(user);
    for (int i = 0; i < roles.length; i++) {
      if (roles[i].getType().equals("S")) {
        result.add(roles[i]);
      }
    }
    return (SecurityRole[])result.toArray(new SecurityRole[result.size()]);
  }
  
  private SecurityRole[] getAllParentRoles(SecurityRole role, List parentRoles)
  {
    List result;
    if (parentRoles == null) {
      result = new ArrayList();
    } else {
      result = parentRoles;
    }
    if (role.getParentRole() != null)
    {
      result.add(role.getParentRole());
      getAllParentRoles(role.getParentRole(), result);
    }
    return (SecurityRole[])result.toArray(new SecurityRole[result.size()]);
  }
  
  public List findSecurityRoleWithResourceAndOperation(ACLResource aclResource, ACLOperation aclOperation)
  {
    return this.roleDAO.findSecurityRoleWithResourceAndOperation(aclResource, aclOperation);
  }
  
  public SecurityRole[] loadAllRolesFromUser(SecurityUser user, OrganTree tree)
  {
    if (user == null) {
      throw new SecurityException("User Principal not existed.");
    }
    List result = this.roleDAO.loadAllRolesFromUser(user, tree);
    SecurityGroup[] groups = this.dynamicGroupProvider.getDynamicGroupsFromUser(user);
    for (int i = 0; i < groups.length; i++) {
      result.add(this.roleDAO.loadDefaultRoleWithGroup(groups[i]));
    }
    return (SecurityRole[])result.toArray(new SecurityRole[result.size()]);
  }
  
  public SecurityRole[] loadAdminRolesFromUser(SecurityUser user, OrganTree tree)
  {
    if (user == null) {
      throw new SecurityException("User Principal not existed.");
    }
    List result = this.roleDAO.loadAdminRolesFromUser(user, tree);
    return (SecurityRole[])result.toArray(new SecurityRole[result.size()]);
  }
}
