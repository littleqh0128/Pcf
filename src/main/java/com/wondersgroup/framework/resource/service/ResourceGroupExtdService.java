package com.wondersgroup.framework.resource.service;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.resource.bo.ResourceGroup;
import com.wondersgroup.framework.resource.connector.Resource;
import com.wondersgroup.framework.security.bo.ACLResourceType;
import java.util.Map;

public abstract interface ResourceGroupExtdService
{
  public abstract Resource[] getAllResource(ResourceGroup paramResourceGroup);
  
  public abstract ResourceGroup loadResourceGroupById(long paramLong);
  
  public abstract void addResourceGroup(ResourceGroup paramResourceGroup);
  
  public abstract void removeResourceGroup(ResourceGroup paramResourceGroup);
  
  public abstract void updateResourceGroup(ResourceGroup paramResourceGroup);
  
  public abstract ResourceGroup[] getAllResourceGroups();
  
  public abstract Page getResourceGroupsByPage(Map paramMap1, Map paramMap2, int paramInt1, int paramInt2);
  
  public abstract ResourceGroup getResourceGroupByCode(String paramString);
  
  public abstract ResourceGroup[] getResourceGroupByType(ACLResourceType paramACLResourceType);
  
  public abstract boolean isResourceInGroup(String paramString, ResourceGroup paramResourceGroup);
  
  public abstract Page getResourceGroupsByTypeByPage(ACLResourceType paramACLResourceType, int paramInt1, int paramInt2);
}
