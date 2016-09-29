package com.wondersgroup.framework.security.service;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.resource.connector.Resource;
import com.wondersgroup.framework.security.bo.ACLOperation;
import com.wondersgroup.framework.security.bo.ACLPermission;
import com.wondersgroup.framework.security.bo.ACLResource;
import com.wondersgroup.framework.security.bo.ACLResourceType;
import com.wondersgroup.framework.security.bo.SecurityRole;
import com.wondersgroup.framework.security.bo.SecurityUser;
import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract interface ACLExtdService
{
  public abstract boolean check(String paramString1, String paramString2, String paramString3, String paramString4);
  
  public abstract boolean checkByCode(String paramString1, String paramString2, String paramString3, String paramString4);
  
  public abstract boolean check(SecurityRole[] paramArrayOfSecurityRole, ACLResource paramACLResource, ACLOperation paramACLOperation);
  
  public abstract Map check(SecurityRole[] paramArrayOfSecurityRole, List paramList, String paramString1, String paramString2);
  
  public abstract Map getAllACLResourceStatusByTypeAndRole(ACLResource[] paramArrayOfACLResource, SecurityRole paramSecurityRole, ACLResourceType paramACLResourceType);
  
  public abstract Map getAllTempACLResourceStatusByTypeAndRole(ACLResource[] paramArrayOfACLResource, SecurityRole paramSecurityRole, ACLResourceType paramACLResourceType);
  
  public abstract boolean check(SecurityUser paramSecurityUser, ACLResource paramACLResource, ACLOperation paramACLOperation);
  
  public abstract boolean checkWithSpecialRole(SecurityUser paramSecurityUser);
  
  public abstract void accredit(SecurityRole paramSecurityRole, ACLResource paramACLResource, ACLOperation paramACLOperation);
  
  public abstract void revoke(SecurityRole paramSecurityRole, ACLResource paramACLResource, ACLOperation paramACLOperation);
  
  public abstract ACLOperation[] getResourceOperation(SecurityRole paramSecurityRole, ACLResource paramACLResource);
  
  public abstract ACLOperation[] getRevokeResourceOperation(SecurityRole paramSecurityRole, ACLResource paramACLResource);
  
  public abstract ACLOperation[] getRevokeResourceOperation(SecurityUser paramSecurityUser, ACLResource paramACLResource);
  
  public abstract ACLOperation[] getPublishResourceOperation(SecurityRole paramSecurityRole, ACLResource paramACLResource);
  
  public abstract ACLOperation[] getPublishResourceOperation(SecurityUser paramSecurityUser, ACLResource paramACLResource);
  
  public abstract ACLPermission[] getACLPermission(SecurityRole paramSecurityRole, ACLResource paramACLResource);
  
  public abstract ACLResource[] getACLResourceByTypeAndRole(SecurityRole paramSecurityRole, ACLResourceType paramACLResourceType);
  
  public abstract ACLResource[] getACLResourceByTypeAndUser(SecurityUser paramSecurityUser, ACLResourceType paramACLResourceType);
  
  public abstract ACLResource[] getPublishACLResourceByTypeAndRole(SecurityUser paramSecurityUser, ACLResourceType paramACLResourceType);
  
  public abstract ACLResource[] getRevokeACLResourceByTypeAndUser(SecurityUser paramSecurityUser, ACLResourceType paramACLResourceType);
  
  public abstract ACLResource[] getRevokeACLResourceByTypeAndRole(SecurityRole paramSecurityRole, ACLResourceType paramACLResourceType);
  
  public abstract ACLResource[] getPublishACLResourceByTypeAndRole(SecurityRole paramSecurityRole, ACLResourceType paramACLResourceType);
  
  public abstract ACLPermission getACLPermissionWithResourceAndOperation(ACLResource paramACLResource, ACLOperation paramACLOperation);
  
  public abstract ACLPermission getACLTempPermissionWithResourceAndOperation(ACLResource paramACLResource, ACLOperation paramACLOperation);
  
  public abstract ACLPermission loadACLPermissionById(long paramLong);
  
  public abstract void revokePermission(SecurityRole paramSecurityRole, ACLPermission paramACLPermission);
  
  public abstract void antiRevokePermission(SecurityRole paramSecurityRole, ACLPermission paramACLPermission);
  
  public abstract void antiRevokePermission(SecurityRole paramSecurityRole, ACLResource paramACLResource, ACLOperation paramACLOperation);
  
  public abstract void revokeAccreditPermission(SecurityRole paramSecurityRole, ACLPermission paramACLPermission);
  
  public abstract void revokeAccreditPermission(SecurityRole paramSecurityRole, ACLResource paramACLResource, ACLOperation paramACLOperation);
  
  public abstract void revokeResourcePermission(SecurityRole paramSecurityRole, ACLResource paramACLResource);
  
  public abstract void accreditPermission(SecurityRole paramSecurityRole, ACLPermission paramACLPermission);
  
  public abstract void accreditPermission(SecurityRole paramSecurityRole, ACLResource paramACLResource, ACLOperation paramACLOperation);
  
  public abstract void accreditPermission(SecurityRole paramSecurityRole, ACLResource paramACLResource, ACLOperation paramACLOperation, Date paramDate1, Date paramDate2);
  
  public abstract boolean isPermissionBePublished(SecurityRole paramSecurityRole, ACLPermission paramACLPermission);
  
  public abstract SecurityRole[] loadRevokeRoleWithResourceAndOperation(ACLResource paramACLResource, ACLOperation paramACLOperation);
  
  public abstract SecurityRole[] loadAccreditRoleWithResourceAndOperation(ACLResource paramACLResource, ACLOperation paramACLOperation);
  
  public abstract Page getPublishPermission(SecurityRole paramSecurityRole, int paramInt1, int paramInt2);
  
  public abstract void publishPermission(SecurityRole paramSecurityRole, ACLPermission paramACLPermission);
  
  public abstract void publishPermission(SecurityRole paramSecurityRole, ACLResource paramACLResource, ACLOperation paramACLOperation);
  
  public abstract void antiPublishPermission(SecurityRole paramSecurityRole, ACLPermission paramACLPermission);
  
  public abstract void antiPublishPermission(SecurityRole paramSecurityRole, ACLResource paramACLResource, ACLOperation paramACLOperation);
  
  /**
   * @deprecated
   */
  public abstract ACLOperation[] getOperationWithResource(Resource paramResource, SecurityRole paramSecurityRole);
  
  public abstract Map check(String paramString1, List paramList, String paramString2, String paramString3);
  
  public abstract Map checkByCode(String paramString1, List paramList, String paramString2, String paramString3);
  
  public abstract Resource getResource(String paramString1, String paramString2);
  
  public abstract List getAllResources(String paramString1, String paramString2, String paramString3);
  
  public abstract List getTopResources(String paramString1, String paramString2, String paramString3);
  
  public abstract List getChildResources(String paramString1, String paramString2, String paramString3, String paramString4);
}
