package com.wondersgroup.framework.security.service.impl;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.resource.bo.ResourceGroup;
import com.wondersgroup.framework.resource.connector.Resource;
import com.wondersgroup.framework.resource.connector.provider.DynamicResourceProvider;
import com.wondersgroup.framework.resource.connector.provider.GeneralResourceProvider;
import com.wondersgroup.framework.resource.connector.service.ResourceService;
import com.wondersgroup.framework.security.bo.ACLOperation;
import com.wondersgroup.framework.security.bo.ACLPermission;
import com.wondersgroup.framework.security.bo.ACLResource;
import com.wondersgroup.framework.security.bo.ACLResourceType;
import com.wondersgroup.framework.security.bo.SecurityRole;
import com.wondersgroup.framework.security.bo.SecurityUser;
import com.wondersgroup.framework.security.dao.ACLPermissionExtdDAO;
import com.wondersgroup.framework.security.dao.RoleExtdDAO;
import com.wondersgroup.framework.security.role.SpecialRole;
import com.wondersgroup.framework.security.service.ACLResourceExtdService;
import com.wondersgroup.framework.security.service.ACLExtdService;
import com.wondersgroup.framework.security.service.RoleExtdService;
import com.wondersgroup.framework.security.service.UserExtdService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("ACLExtService")
@Transactional
public class ACLExtdServiceImpl implements ACLExtdService
{
  private static Log log = LogFactory.getLog(ACLExtdServiceImpl.class);
  @Autowired
  private RoleExtdDAO roleDAO;
  @Autowired
  private ACLPermissionExtdDAO aclPermissionDAO;
  @Autowired
  private RoleExtdService roleService;
  @Autowired
  private ACLResourceExtdService aclResourceService;
  @Autowired
  private UserExtdService userService;
  private GeneralResourceProvider generalResourceProvider;
  private DynamicResourceProvider dynamicResourceProvider;
  
  public void setGeneralResourceProvider(GeneralResourceProvider generalResourceProvider)
  {
    this.generalResourceProvider = generalResourceProvider;
  }
  
  public void setDynamicResourceProvider(DynamicResourceProvider dynamicResourceProvider)
  {
    this.dynamicResourceProvider = dynamicResourceProvider;
  }
  
  public boolean check(String userId, String resourceId, String resourceTypeId, String operationId)
  {
    SecurityUser securityUser = this.userService.loadUserById(Long.parseLong(userId));
    if (checkWithSpecialRole(securityUser)) {
      return true;
    }
    ACLResourceType aclResourceType = this.aclResourceService.loadResourceTypeById(Long.parseLong(resourceTypeId));
    ACLOperation aclOperation = this.aclResourceService.loadOperationById(Long.parseLong(operationId));
    return check(securityUser, resourceId, aclResourceType, aclOperation);
  }
  
  public boolean checkByCode(String userId, String resourceId, String resourceTypeCode, String operationCode)
  {
    SecurityUser securityUser = this.userService.loadUserById(Long.parseLong(userId));
    if (checkWithSpecialRole(securityUser)) {
      return true;
    }
    ACLResourceType aclResourceType = this.aclResourceService.getResourceTypeByCode(resourceTypeCode);
    if (aclResourceType == null) {
      return false;
    }
    ACLOperation aclOperation = this.aclResourceService.getOperationByCode(aclResourceType, operationCode);
    if (aclOperation == null) {
      return false;
    }
    return check(securityUser, resourceId, aclResourceType, aclOperation);
  }
  
  public Map check(String userId, List resourceIds, String resourceTypeId, String operationId)
  {
    Map result = new HashMap();
    SecurityUser securityUser = this.userService.loadUserById(Long.parseLong(userId));
    if (checkWithSpecialRole(securityUser))
    {
      for (Iterator iter = resourceIds.iterator(); iter.hasNext();)
      {
        String element = (String)iter.next();
        result.put(element, new Boolean(true));
      }
      return result;
    }
    SecurityRole[] roles = this.roleService.loadAllRolesFromUser(securityUser);
    ACLResourceType aclResourceType = this.aclResourceService.loadResourceTypeById(Long.parseLong(resourceTypeId));
    if (aclResourceType == null) {
      return result;
    }
    if (aclResourceType.getCatalog() == 1) {
      return check(roles, resourceIds, resourceTypeId, operationId);
    }
    if (aclResourceType.getCatalog() == 2) {
      return checkDynamicResource(roles, resourceIds, aclResourceType, operationId);
    }
    for (Iterator iter = resourceIds.iterator(); iter.hasNext();)
    {
      String element = (String)iter.next();
      result.put(element, new Boolean(false));
    }
    return result;
  }
  
  public Map checkByCode(String userId, List resourceIds, String resourceTypeCode, String operationCode)
  {
    ACLResourceType aclResourceType = this.aclResourceService.getResourceTypeByCode(resourceTypeCode);
    ACLOperation aclOperation = this.aclResourceService.getOperationByCode(aclResourceType, operationCode);
    if ((aclResourceType == null) || (aclOperation == null)) {
      return check(userId, resourceIds, null, null);
    }
    String resourceTypeId = String.valueOf(aclResourceType.getId());
    String operationId = String.valueOf(aclOperation.getId());
    return check(userId, resourceIds, resourceTypeId, operationId);
  }
  
  private boolean check(SecurityUser securityUser, String resourceId, ACLResourceType aclResourceType, ACLOperation aclOperation)
  {
    SecurityRole[] roles = this.roleService.loadAllRolesFromUser(securityUser);
    if ((aclResourceType == null) || (aclOperation == null)) {
      return false;
    }
    if (aclResourceType.getCatalog() == 1) {
      return checkGeneralResource(resourceId, roles, aclOperation, aclResourceType);
    }
    if (aclResourceType.getCatalog() == 2) {
      return checkDynamicResource(resourceId, roles, aclOperation, aclResourceType);
    }
    return false;
  }
  
  private boolean checkGeneralResource(String resourceId, SecurityRole[] roles, ACLOperation aclOperation, ACLResourceType aclResourceType)
  {
    ACLResource aclResource = this.aclResourceService.getResourceByTypeAndNativeId(aclResourceType, resourceId);
    if (aclResource == null) {
      return false;
    }
    return check(roles, aclResource, aclOperation);
  }
  
  private boolean checkDynamicResource(String resourceId, SecurityRole[] roles, ACLOperation aclOperation, ACLResourceType aclResourceType)
  {
    boolean result = false;
    ResourceGroup[] resourceGroups = this.dynamicResourceProvider.getGroupsByResource(resourceId, aclResourceType);
    for (int i = 0; i < resourceGroups.length; i++)
    {
      String nativeId = resourceGroups[i].getResourceId();
      ACLResource aclResource = this.aclResourceService.getResourceByTypeAndNativeId(aclResourceType, nativeId);
      if (aclResource != null)
      {
        ACLPermission permission = getACLPermissionWithResourceAndOperation(aclResource, aclOperation);
        for (int j = 0; j < roles.length; j++) {
          if (isExistRevokePermission(roles[i], permission))
          {
            result = false;
            return result;
          }
        }
      }
    }
    for (int i = 0; i < resourceGroups.length; i++)
    {
      String nativeId = resourceGroups[i].getResourceId();
      ACLResource aclResource = this.aclResourceService.getResourceByTypeAndNativeId(aclResourceType, nativeId);
      if (aclResource != null)
      {
        ACLPermission permission = getACLPermissionWithResourceAndOperation(aclResource, aclOperation);
        for (int j = 0; j < roles.length; j++) {
          if (isExistPermission(roles[i], permission)) {
            result = true;
          }
        }
      }
    }
    return result;
  }
  
  private Map checkDynamicResource(SecurityRole[] roles, List resourceIds, ACLResourceType aclResourceType, String operationId)
  {
    Map result = new HashMap();
    for (Iterator iter = resourceIds.iterator(); iter.hasNext();)
    {
      String resourceId = (String)iter.next();
      ResourceGroup[] resourceGroups = this.dynamicResourceProvider.getGroupsByResource(resourceId, aclResourceType);
      List groupIds = new ArrayList();
      for (int i = 0; i < resourceGroups.length; i++) {
        groupIds.add(resourceGroups[i].getResourceId());
      }
      List list = this.roleDAO.findPermission(groupIds, String.valueOf(aclResourceType.getId()), operationId);
      if (isExistRevokePermission(roles, groupIds, list))
      {
        result.put(resourceId, new Boolean(false));
      }
      else
      {
        Map map = check(roles, groupIds, String.valueOf(aclResourceType.getId()), operationId);
        if (map.containsValue(new Boolean(true))) {
          result.put(resourceId, new Boolean(true));
        }
      }
    }
    return result;
  }
  
  public boolean check(SecurityRole[] roles, ACLResource resource, ACLOperation operation)
  {
    boolean result = false;
    
    List revokeRoles = this.roleDAO.findRevokeSecurityRoleWithResourceAndOperation(resource, operation);
    Collection intersectionRev = CollectionUtils.intersection(revokeRoles, Arrays.asList(roles));
    if ((intersectionRev != null) && (intersectionRev.size() > 0)) {
      return false;
    }
    List resultRoles = this.roleDAO.findSecurityRoleWithResourceAndOperation(resource, operation);
    Collection intersectionAcc = CollectionUtils.intersection(resultRoles, Arrays.asList(roles));
    if ((intersectionAcc != null) && (intersectionAcc.size() > 0)) {
      return true;
    }
    return result;
  }
  
  public Map check(SecurityRole[] roles, List resourceIds, String resourceTypeId, String operationId)
  {
    Map result = new HashMap();
    for (Object resourceId : resourceIds) {
      result.put(resourceId, new Boolean(false));
    }
    List list = this.roleDAO.findPermission(resourceTypeId, operationId, roles);
    for (Object resourceId : list) {
      if (resourceIds.contains(resourceId)) {
        result.put(resourceId, new Boolean(true));
      }
    }
    list = this.roleDAO.findRevokePermission(resourceTypeId, operationId, roles);
    for (Object resourceId : list) {
      if (resourceIds.contains(resourceId)) {
        result.put(resourceId, new Boolean(false));
      }
    }
    return result;
  }
  
  private boolean isExistRevokePermission(SecurityRole[] roles, List resourceIds, List list)
  {
    Map revokeRoles = findRolesWithResourcePermission(resourceIds, list, true);
    for (Iterator iter = resourceIds.iterator(); iter.hasNext();)
    {
      String resourceId = (String)iter.next();
      Set revokeSet = (Set)revokeRoles.get(resourceId);
      if (revokeSet != null)
      {
        List revokeList = new ArrayList(revokeSet);
        Collection intersectionRev = CollectionUtils.intersection(revokeList, Arrays.asList(roles));
        if ((intersectionRev != null) && (intersectionRev.size() > 0)) {
          return true;
        }
      }
    }
    return false;
  }
  
  public Map getAllACLResourceStatusByTypeAndRole(ACLResource[] resources, SecurityRole role, ACLResourceType type)
  {
    Map result = new HashMap();
    ACLOperation[] aclOperations = this.aclResourceService.getOperations(type);
    for (int i = 0; i < resources.length; i++)
    {
      ACLResource res = resources[i];
      ACLResource aclResource = this.aclResourceService.getResourceByTypeAndNativeId(type, res.getNativeResourceId());
      if ((aclResource == null) || (aclResource.getId() == 0L)) {
        for (int j = 0; j < aclOperations.length; j++)
        {
          ACLOperation aclOperation = aclOperations[j];
          String key = res.getNativeResourceId() + "," + new Long(type.getId()).toString();
          key = key + "," + new Long(aclOperation.getId()).toString();
          result.put(key, "INVALID");
        }
      } else {
        for (int j = 0; j < aclOperations.length; j++)
        {
          String key = res.getNativeResourceId() + "," + new Long(type.getId()).toString();
          ACLOperation aclOperation = aclOperations[j];
          ACLPermission aclPermission = getACLPermissionWithResourceAndOperation(aclResource, aclOperation);
          
          key = key + "," + new Long(aclOperation.getId()).toString();
          if (aclPermission == null)
          {
            result.put(key, "INVALID");
          }
          else if (aclPermission != null)
          {
            result.put(key, "INVALID");
            if (isExistPermission(role, aclPermission)) {
              result.put(key, "VALID");
            }
            if (isExistRevokePermission(role, aclPermission)) {
              result.put(key, "REVOKE");
            }
          }
        }
      }
    }
    return result;
  }
  
  public Map getAllTempACLResourceStatusByTypeAndRole(ACLResource[] resources, SecurityRole role, ACLResourceType type)
  {
    Map result = new HashMap();
    ACLOperation[] aclOperations = this.aclResourceService.getOperations(type);
    for (int i = 0; i < resources.length; i++)
    {
      ACLResource res = resources[i];
      ACLResource aclResource = this.aclResourceService.getResourceByTypeAndNativeId(type, res.getNativeResourceId());
      if ((aclResource == null) || (aclResource.getId() == 0L)) {
        for (int j = 0; j < aclOperations.length; j++)
        {
          ACLOperation aclOperation = aclOperations[j];
          String key = res.getNativeResourceId() + "," + new Long(type.getId()).toString();
          key = key + "," + new Long(aclOperation.getId()).toString();
          result.put(key, "INVALID");
        }
      } else {
        for (int j = 0; j < aclOperations.length; j++)
        {
          String key = res.getNativeResourceId() + "," + new Long(type.getId()).toString();
          ACLOperation aclOperation = aclOperations[j];
          ACLPermission aclPermission = getACLTempPermissionWithResourceAndOperation(aclResource, aclOperation);
          
          key = key + "," + new Long(aclOperation.getId()).toString();
          if (aclPermission != null)
          {
            if (isExistRevokePermission(role, aclPermission)) {
              result.put(key, "REVOKE");
            }
            if ((isExistPermission(role, aclPermission)) && 
              (aclPermission.getDueTime().after(new Date())) && (aclPermission.getStartTime().before(new Date()))) {
              result.put(key, "VALID");
            }
          }
          else
          {
            result.put(key, "INVALID");
          }
        }
      }
    }
    return result;
  }
  
  public boolean check(SecurityUser user, ACLResource resource, ACLOperation operation)
  {
    SecurityRole[] roles = this.roleService.loadAllRolesFromUser(user);
    
    List revokeRoles = this.roleDAO.findRevokeSecurityRoleWithResourceAndOperation(resource, operation);
    Collection interOperation = CollectionUtils.intersection(revokeRoles, Arrays.asList(roles));
    if ((interOperation != null) && (interOperation.size() > 0)) {
      return false;
    }
    if (checkWithSpecialRole(user)) {
      return true;
    }
    if (check(roles, resource, operation)) {
      return true;
    }
    return false;
  }
  
  public void accredit(SecurityRole role, ACLResource resource, ACLOperation operation)
  {
    ACLPermission permission = getACLPermissionWithResourceAndOperation(resource, operation);
    if (permission == null)
    {
      accreditPermission(role, resource, operation);
      return;
    }
    if (isExistRevokePermission(role, permission)) {
      antiRevokePermission(role, permission);
    }
    if (isExistPermission(role, permission)) {
      return;
    }
    accreditPermission(role, permission);
  }
  
  public void revoke(SecurityRole role, ACLResource resource, ACLOperation operation)
  {
    resource = this.aclResourceService.createNewACLResource(resource);
    ACLPermission permission = getACLPermissionWithResourceAndOperation(resource, operation);
    if (permission == null)
    {
      permission = createACLPermission(resource, operation);
      revokePermission(role, permission);
      return;
    }
    if (isExistRevokePermission(role, permission)) {
      return;
    }
    revokePermission(role, permission);
  }
  
  public ACLResource[] getACLResourceByTypeAndRole(SecurityRole role, ACLResourceType type)
  {
    return this.aclResourceService.getACLResourceByTypeAndRole(role, type);
  }
  
  public ACLResource[] getACLResourceByTypeAndUser(SecurityUser user, ACLResourceType type)
  {
    List result = new ArrayList();
    SecurityRole[] roles = this.roleService.loadAllRolesFromUser(user);
    for (int i = 0; i < roles.length; i++)
    {
      ACLResource[] resourceArray = getACLResourceByTypeAndRole(roles[i], type);
      result.addAll(Arrays.asList(resourceArray));
    }
    return (ACLResource[])result.toArray(new ACLResource[result.size()]);
  }
  
  public ACLPermission loadACLPermissionById(long id)
  {
    return (ACLPermission)this.aclPermissionDAO.load(new Long(id));
  }
  
  public ACLOperation[] getResourceOperation(SecurityRole role, ACLResource resource)
  {
    List result = getACLOperationWithRoleAndResource(role, resource);
    List revokeOperation = getRevokeOperationWithRoleAndResource(role, resource);
    result.removeAll(revokeOperation);
    return (ACLOperation[])result.toArray(new ACLOperation[result.size()]);
  }
  
  private List getRevokeOperationWithRoleAndResource(SecurityRole role, ACLResource resource)
  {
    return this.aclResourceService.getRevokeOperationWithRoleAndResource(role, resource);
  }
  
  private List getACLOperationWithRoleAndResource(SecurityRole role, ACLResource resource)
  {
    return this.aclResourceService.getACLOperationWithRoleAndResource(role, resource);
  }
  
  private List getPublishOperationWithRoleAndResource(SecurityRole role, ACLResource resource)
  {
    return this.aclResourceService.getPublishOperationWithRoleAndResource(role, resource);
  }
  
  public ACLPermission[] getACLPermission(SecurityRole role, ACLResource resource)
  {
    List result = this.aclPermissionDAO.getACLPermissionsWithRoleAndResource(role, resource);
    return (ACLPermission[])result.toArray(new ACLPermission[result.size()]);
  }
  
  public void revokeResourcePermission(SecurityRole role, ACLResource resource)
  {
    List result = this.aclPermissionDAO.getACLPermissionsWithRoleAndResource(role, resource);
    SecurityRole sr = this.roleService.loadRoleByIdWithLazy(role.getId(), new String[] { "aclPermissions" });
    sr.getAclPermissions().removeAll(result);
    this.roleDAO.update(sr);
  }
  
  public ACLPermission getACLPermissionWithResourceAndOperation(ACLResource resource, ACLOperation operation)
  {
    return this.aclPermissionDAO.getACLPermissionWithResourceAndOperation(resource, operation);
  }
  
  public ACLPermission getACLTempPermissionWithResourceAndOperation(ACLResource resource, ACLOperation operation)
  {
    return this.aclPermissionDAO.getACLTempPermissionWithResourceAndOperation(resource, operation);
  }
  
  private Map findRolesWithResourcePermission(List resourceIds, List list, boolean isRevoke)
  {
    Map result = new HashMap();
    for (Iterator iter = resourceIds.iterator(); iter.hasNext();)
    {
      String element = (String)iter.next();
      result.put(element, null);
    }
    for (Iterator iter = list.iterator(); iter.hasNext();)
    {
      Object[] object = (Object[])iter.next();
      String resourceId = (String)object[0];
      ACLPermission permission = (ACLPermission)object[1];
      Set set = new HashSet();
      Set prevSet = (Set)result.get(resourceId);
      if (prevSet != null) {
        set.addAll(prevSet);
      }
      if (isRevoke) {
        set.addAll(permission.getRevokeRoles());
      } else {
        set.addAll(permission.getRoles());
      }
      if (set.size() > 0) {
        result.put(resourceId, set);
      }
    }
    return result;
  }
  
  public void revokePermission(SecurityRole role, ACLPermission aclPermission)
  {
    SecurityRole sr = this.roleService.loadRoleByIdWithLazy(role.getId(), new String[] { "revokeAclPermissions" });
    sr.getRevokeAclPermissions().add(aclPermission);
    this.roleDAO.update(sr);
  }
  
  public void antiRevokePermission(SecurityRole role, ACLPermission aclPermission)
  {
    SecurityRole sr = this.roleService.loadRoleByIdWithLazy(role.getId(), new String[] { "revokeAclPermissions" });
    sr.getRevokeAclPermissions().remove(aclPermission);
    this.roleDAO.save(sr);
  }
  
  public void antiRevokePermission(SecurityRole role, ACLResource resource, ACLOperation operation)
  {
    resource = this.aclResourceService.createNewACLResource(resource);
    ACLPermission permission = getACLPermissionWithResourceAndOperation(resource, operation);
    if (permission == null) {
      return;
    }
    antiRevokePermission(role, permission);
  }
  
  public void revokeAccreditPermission(SecurityRole role, ACLPermission aclPermission)
  {
    SecurityRole sr = this.roleService.loadRoleByIdWithLazy(role.getId(), new String[] { "aclPermissions" });
    sr.getAclPermissions().remove(aclPermission);
    this.roleDAO.update(sr);
  }
  
  public void revokeAccreditPermission(SecurityRole role, ACLResource resource, ACLOperation operation)
  {
    resource = this.aclResourceService.createNewACLResource(resource);
    ACLPermission permission = getACLPermissionWithResourceAndOperation(resource, operation);
    if (permission == null) {
      return;
    }
    revokeAccreditPermission(role, permission);
  }
  
  public void accreditPermission(SecurityRole role, ACLPermission aclPermission)
  {
    role = this.roleService.loadRoleByIdWithLazy(role.getId(), new String[] { "aclPermissions" });
    role.getAclPermissions().add(aclPermission);
    this.roleDAO.update(role);
  }
  
  public void accreditPermission(SecurityRole role, ACLResource resource, ACLOperation operation)
  {
    resource = this.aclResourceService.createNewACLResource(resource);
    ACLPermission permission = getACLPermissionWithResourceAndOperation(resource, operation);
    if (permission == null) {
      permission = createACLPermission(resource, operation);
    }
    accreditPermission(role, permission);
  }
  
  public void accreditPermission(SecurityRole role, ACLResource resource, ACLOperation operation, Date startTime, Date dueTime)
  {
    resource = this.aclResourceService.createNewACLResource(resource);
    ACLPermission permission = this.aclPermissionDAO.getACLPermissionWithResourceOperationAndTime(resource, operation, startTime, dueTime);
    if (permission == null) {
      permission = createTempPermission(resource, operation, startTime, dueTime);
    }
    accreditPermission(role, permission);
  }
  
  public boolean isPermissionBePublished(SecurityRole role, ACLPermission aclPermission)
  {
    return role.getPublishAclPermissions().contains(aclPermission);
  }
  
  public SecurityRole[] loadRevokeRoleWithResourceAndOperation(ACLResource resource, ACLOperation operation)
  {
    List result = this.roleDAO.findRevokeSecurityRoleWithResourceAndOperation(resource, operation);
    return (SecurityRole[])result.toArray(new SecurityRole[result.size()]);
  }
  
  public SecurityRole[] loadAccreditRoleWithResourceAndOperation(ACLResource resource, ACLOperation operation)
  {
    List result = this.roleDAO.findSecurityRoleWithResourceAndOperation(resource, operation);
    return (SecurityRole[])result.toArray(new SecurityRole[result.size()]);
  }
  
  public boolean checkWithSpecialRole(SecurityUser user)
  {
    boolean result = false;
    SecurityRole[] roles = this.roleService.loadAllSpecialRoleFromUser(user);
    if ((roles != null) || (roles.length > 0)) {
      try
      {
        for (int i = 0; i < roles.length; i++)
        {
          Class clazz = Class.forName("com.wondersgroup.framework.security.role.impl." + roles[i].getName() + "Role");
          
          SpecialRole specialRole = (SpecialRole)clazz.newInstance();
          if (!result) {
            result = specialRole.check();
          }
        }
      }
      catch (ClassNotFoundException e)
      {
        log.warn(e);
      }
      catch (InstantiationException e)
      {
        log.warn(e);
      }
      catch (IllegalAccessException e)
      {
        log.warn(e);
      }
    }
    return result;
  }
  
  private ACLPermission createACLPermission(ACLResource resource, ACLOperation operation)
  {
    ACLPermission permission = new ACLPermission();
    permission.setAclOperation(operation);
    permission.setAclResource(resource);
    this.aclPermissionDAO.save(permission);
    return permission;
  }
  
  private ACLPermission createTempPermission(ACLResource resource, ACLOperation operation, Date startTime, Date dueTime)
  {
    ACLPermission permission = new ACLPermission();
    permission.setAclOperation(operation);
    permission.setAclResource(resource);
    permission.setDueTime(dueTime);
    permission.setStartTime(startTime);
    this.aclPermissionDAO.save(permission);
    return permission;
  }
  
  private boolean isExistRevokePermission(SecurityRole role, ACLPermission permission)
  {
    role = this.roleService.loadRoleByIdWithLazy(role.getId(), new String[] { "revokeAclPermissions" });
    return role.getRevokeAclPermissions().contains(permission);
  }
  
  private boolean isExistPermission(SecurityRole role, ACLPermission permission)
  {
    role = this.roleService.loadRoleByIdWithLazy(role.getId(), new String[] { "aclPermissions" });
    return role.getAclPermissions().contains(permission);
  }
  
  public ACLOperation[] getRevokeResourceOperation(SecurityRole role, ACLResource revokeResource)
  {
    List result = getRevokeOperationWithRoleAndResource(role, revokeResource);
    return (ACLOperation[])result.toArray(new ACLOperation[result.size()]);
  }
  
  public ACLOperation[] getRevokeResourceOperation(SecurityUser user, ACLResource revokeResource)
  {
    List result = new ArrayList();
    SecurityRole[] roles = this.roleService.loadAllRolesFromUser(user);
    for (int i = 0; i < roles.length; i++)
    {
      List operationList = getRevokeOperationWithRoleAndResource(roles[i], revokeResource);
      result.addAll(operationList);
    }
    return (ACLOperation[])result.toArray(new ACLOperation[result.size()]);
  }
  
  public ACLOperation[] getPublishResourceOperation(SecurityRole role, ACLResource publishResource)
  {
    List result = getPublishOperationWithRoleAndResource(role, publishResource);
    return (ACLOperation[])result.toArray(new ACLOperation[result.size()]);
  }
  
  public ACLOperation[] getPublishResourceOperation(SecurityUser user, ACLResource publishResource)
  {
    List result = new ArrayList();
    SecurityRole[] roles = this.roleService.loadAllRolesFromUser(user);
    for (int i = 0; i < roles.length; i++)
    {
      ACLOperation[] operationArray = getPublishResourceOperation(roles[i], publishResource);
      result.addAll(Arrays.asList(operationArray));
    }
    return (ACLOperation[])result.toArray(new ACLOperation[result.size()]);
  }
  
  public ACLResource[] getRevokeACLResourceByTypeAndUser(SecurityUser user, ACLResourceType type)
  {
    List result = new ArrayList();
    SecurityRole[] roles = this.roleService.loadAllRolesFromUser(user);
    for (int i = 0; i < roles.length; i++)
    {
      ACLResource[] resourceArray = getRevokeACLResourceByTypeAndRole(roles[i], type);
      result.addAll(Arrays.asList(resourceArray));
    }
    return (ACLResource[])result.toArray(new ACLResource[result.size()]);
  }
  
  public ACLResource[] getRevokeACLResourceByTypeAndRole(SecurityRole role, ACLResourceType type)
  {
    return this.aclResourceService.getRevokeACLResourceByTypeAndRole(role, type);
  }
  
  public ACLResource[] getPublishACLResourceByTypeAndRole(SecurityRole role, ACLResourceType type)
  {
    return this.aclResourceService.getPublishACLResourceByTypeAndRole(role, type);
  }
  
  public ACLResource[] getPublishACLResourceByTypeAndRole(SecurityUser user, ACLResourceType type)
  {
    List result = new ArrayList();
    SecurityRole[] roles = this.roleService.loadAllRolesFromUser(user);
    for (int i = 0; i < roles.length; i++)
    {
      ACLResource[] resourceArray = getPublishACLResourceByTypeAndRole(roles[i], type);
      result.addAll(Arrays.asList(resourceArray));
    }
    return (ACLResource[])result.toArray(new ACLResource[result.size()]);
  }
  
  public Page getPublishPermission(SecurityRole role, int pageNo, int pageSize)
  {
    return null;
  }
  
  public void publishPermission(SecurityRole role, ACLResource resource, ACLOperation operation)
  {
    resource = this.aclResourceService.createNewACLResource(resource);
    ACLPermission permission = getACLPermissionWithResourceAndOperation(resource, operation);
    if (permission == null)
    {
      permission = createACLPermission(resource, operation);
      publishPermission(role, permission);
      return;
    }
    if (isPublishPermission(role, permission)) {
      return;
    }
    publishPermission(role, permission);
  }
  
  public void publishPermission(SecurityRole role, ACLPermission aclPermission)
  {
    SecurityRole sr = this.roleService.loadRoleByIdWithLazy(role.getId(), new String[] { "publishAclPermissions" });
    sr.getPublishAclPermissions().add(aclPermission);
    this.roleDAO.update(sr);
  }
  
  public void antiPublishPermission(SecurityRole role, ACLPermission aclPermission)
  {
    SecurityRole sr = this.roleService.loadRoleByIdWithLazy(role.getId(), new String[] { "publishAclPermissions" });
    sr.getPublishAclPermissions().remove(aclPermission);
    this.roleDAO.save(sr);
  }
  
  public void antiPublishPermission(SecurityRole role, ACLResource resource, ACLOperation operation)
  {
    resource = this.aclResourceService.createNewACLResource(resource);
    ACLPermission permission = getACLPermissionWithResourceAndOperation(resource, operation);
    if (permission == null) {
      return;
    }
    antiPublishPermission(role, permission);
  }
  
  public boolean isPublishPermission(SecurityRole role, ACLPermission publishPermission)
  {
    role = this.roleService.loadRoleByIdWithLazy(role.getId(), new String[] { "publishAclPermissions" });
    return role.getPublishAclPermissions().contains(publishPermission);
  }
  
  public ACLOperation[] getOperationWithResource(Resource resource, SecurityRole role)
  {
    ACLResourceType aclResourceType = this.aclResourceService.loadResourceTypeByName(resource.getResourceType());
    ResourceService resourceService = this.generalResourceProvider.getResourceService(aclResourceType.getCode());
    Resource[] parentResources = resourceService.getAllParentResource(resource);
    List resources = Arrays.asList(parentResources);
    Set accreditOperations = new HashSet();Set revokeOperations = new HashSet();
    resources.add(resource);
    for (Iterator i = resources.iterator(); i.hasNext();)
    {
      Resource res = (Resource)i.next();
      String nativeResourceId = res.getResourceId();
      ACLResource aclResource = this.aclResourceService.getResourceByTypeAndNativeId(aclResourceType, nativeResourceId);
      if (aclResource != null)
      {
        ACLOperation[] accreditOperationArray = getResourceOperation(role, aclResource);
        ACLOperation[] revokeOperationArray = getRevokeResourceOperation(role, aclResource);
        accreditOperations.addAll(Arrays.asList(accreditOperationArray));
        revokeOperations.addAll(Arrays.asList(revokeOperationArray));
      }
    }
    Collection interOperation = CollectionUtils.intersection(accreditOperations, revokeOperations);
    accreditOperations.removeAll(interOperation);
    return (ACLOperation[])accreditOperations.toArray(new ACLOperation[accreditOperations.size()]);
  }
  
  public Resource getResource(String resourceTypeCode, String resourceId)
  {
    ResourceService resourceService = this.generalResourceProvider.getResourceService(resourceTypeCode);
    return resourceService.getResourceById(resourceId);
  }
  
  public List getAllResources(String userId, String resourceTypeCode, String operationCode)
  {
    List result = new ArrayList();
    List removedList = new ArrayList();
    ResourceService resourceService = this.generalResourceProvider.getResourceService(resourceTypeCode);
    result.addAll(Arrays.asList(resourceService.getResource()));
    List resourceIds = new ArrayList();
    for (int i = 0; i < result.size(); i++)
    {
      Resource resource = (Resource)result.get(i);
      resourceIds.add(resource.getResourceId());
    }
    Map map = checkByCode(userId, resourceIds, resourceTypeCode, operationCode);
    for (int i = 0; i < result.size(); i++)
    {
      Resource resource = (Resource)result.get(i);
      if (!Boolean.TRUE.equals(map.get(resource.getResourceId()))) {
        removedList.add(resource);
      }
    }
    result.removeAll(removedList);
    return result;
  }
  
  public List getTopResources(String userId, String resourceTypeCode, String operationCode)
  {
    List result = new ArrayList();
    List removedList = new ArrayList();
    ResourceService resourceService = this.generalResourceProvider.getResourceService(resourceTypeCode);
    result.addAll(Arrays.asList(resourceService.getTopResources()));
    List resourceIds = new ArrayList();
    for (int i = 0; i < result.size(); i++)
    {
      Resource resource = (Resource)result.get(i);
      resourceIds.add(resource.getResourceId());
    }
    Map map = checkByCode(userId, resourceIds, resourceTypeCode, operationCode);
    for (int i = 0; i < result.size(); i++)
    {
      Resource resource = (Resource)result.get(i);
      if (!Boolean.TRUE.equals(map.get(resource.getResourceId()))) {
        removedList.add(resource);
      }
    }
    result.removeAll(removedList);
    return result;
  }
  
  public List getChildResources(String userId, String resourceTypeCode, String operationCode, String parentResourceId)
  {
    List result = new ArrayList();
    List removedList = new ArrayList();
    ResourceService resourceService = this.generalResourceProvider.getResourceService(resourceTypeCode);
    result.addAll(Arrays.asList(resourceService.getChildResources(parentResourceId)));
    List resourceIds = new ArrayList();
    for (int i = 0; i < result.size(); i++)
    {
      Resource resource = (Resource)result.get(i);
      resourceIds.add(resource.getResourceId());
    }
    Map map = checkByCode(userId, resourceIds, resourceTypeCode, operationCode);
    for (int i = 0; i < result.size(); i++)
    {
      Resource resource = (Resource)result.get(i);
      if (!Boolean.TRUE.equals(map.get(resource.getResourceId()))) {
        removedList.add(resource);
      }
    }
    result.removeAll(removedList);
    return result;
  }
}
