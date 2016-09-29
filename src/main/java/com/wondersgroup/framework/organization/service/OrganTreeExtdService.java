package com.wondersgroup.framework.organization.service;

import com.wondersgroup.framework.organization.bo.OrganTree;
import com.wondersgroup.framework.security.bo.SecurityUser;

public abstract interface OrganTreeExtdService
{
  public abstract OrganTree[] getOrganTreeByName(String paramString);
  
  public abstract OrganTree getOrganTreeByCode(String paramString);
  
  public abstract OrganTree loadOrganTreeById(long paramLong);
  
  public abstract void removeOrganTree(OrganTree paramOrganTree);
  
  public abstract void addOrganTree(OrganTree paramOrganTree);
  
  public abstract void updateOrganTree(OrganTree paramOrganTree);
  
  public abstract boolean isAdminInTree(SecurityUser paramSecurityUser, OrganTree paramOrganTree);
}
