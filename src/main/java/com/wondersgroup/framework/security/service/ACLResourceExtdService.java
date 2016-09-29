package com.wondersgroup.framework.security.service;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.security.bo.ACLOperation;
import com.wondersgroup.framework.security.bo.ACLResource;
import com.wondersgroup.framework.security.bo.ACLResourceType;
import com.wondersgroup.framework.security.bo.SecurityRole;
import java.util.List;
import java.util.Map;

public abstract interface ACLResourceExtdService
{
  public abstract ACLResource loadResourceById(long paramLong);
  
  public abstract ACLResource getResourceByTypeAndNativeId(ACLResourceType paramACLResourceType, String paramString);
  
  public abstract ACLResource createNewACLResource(ACLResource paramACLResource);
  
  public abstract void deleteResource(ACLResource paramACLResource);
  
  public abstract ACLResourceType[] getAllResourceTypes();
  
  public abstract Page getAllResourceTypesByPage(Map paramMap1, Map paramMap2, int paramInt1, int paramInt2);
  
  public abstract ACLOperation[] getOperations(ACLResourceType paramACLResourceType);
  
  public abstract ACLOperation[] getAllOperations();
  
  public abstract ACLResource[] getAllResources();
  
  public abstract void createNewResourceType(ACLResourceType paramACLResourceType);
  
  public abstract void updateResourceType(ACLResourceType paramACLResourceType, String paramString);
  
  public abstract void removeResourceType(ACLResourceType paramACLResourceType);
  
  public abstract void removeResourceType(long paramLong);
  
  public abstract void removeOperation(ACLOperation paramACLOperation);
  
  public abstract ACLResourceType loadResourceTypeById(long paramLong);
  
  public abstract ACLResourceType loadResourceTypeByName(String paramString);
  
  public abstract ACLResourceType getResourceTypeByName(String paramString);
  
  public abstract void addOperationToResourceType(ACLOperation paramACLOperation, ACLResourceType paramACLResourceType);
  
  public abstract void updateOperation(ACLOperation paramACLOperation);
  
  public abstract void removeOperationFromResourceType(ACLOperation paramACLOperation, ACLResourceType paramACLResourceType);
  
  public abstract ACLResourceType loadResourceTypeByIdWithLazy(long paramLong, String[] paramArrayOfString);
  
  public abstract Page getOperationsByPage(ACLResourceType paramACLResourceType, int paramInt1, int paramInt2);
  
  public abstract ACLOperation loadOperationById(long paramLong);
  
  public abstract ACLResourceType getResourceTypeByCode(String paramString);
  
  public abstract ACLResourceType[] getResourceTypesByCatalog(int paramInt);
  
  public abstract ACLOperation getOperationByCode(ACLResourceType paramACLResourceType, String paramString);
  
  public abstract ACLResource[] getACLResourceByTypeAndRole(SecurityRole paramSecurityRole, ACLResourceType paramACLResourceType);
  
  public abstract ACLResource[] getRevokeACLResourceByTypeAndRole(SecurityRole paramSecurityRole, ACLResourceType paramACLResourceType);
  
  public abstract ACLResource[] getPublishACLResourceByTypeAndRole(SecurityRole paramSecurityRole, ACLResourceType paramACLResourceType);
  
  public abstract List getRevokeOperationWithRoleAndResource(SecurityRole paramSecurityRole, ACLResource paramACLResource);
  
  public abstract List getACLOperationWithRoleAndResource(SecurityRole paramSecurityRole, ACLResource paramACLResource);
  
  public abstract List getPublishOperationWithRoleAndResource(SecurityRole paramSecurityRole, ACLResource paramACLResource);
  
  public abstract ACLResource[] getAclResourceByResourceType(long paramLong);
  
  public abstract ACLResource[] getAclResourceByNativeResourceId(String paramString);
}
