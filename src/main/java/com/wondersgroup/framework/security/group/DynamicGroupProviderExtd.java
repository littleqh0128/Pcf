package com.wondersgroup.framework.security.group;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.security.bo.SecurityGroup;
import com.wondersgroup.framework.security.bo.SecurityUser;
import java.util.Map;

public abstract interface DynamicGroupProviderExtd
{
  public abstract boolean isUserInGroup(SecurityUser paramSecurityUser, SecurityGroup paramSecurityGroup);
  
  public abstract Page getUsersByPageInGroup(SecurityGroup paramSecurityGroup, Map paramMap1, Map paramMap2, int paramInt1, int paramInt2);
  
  public abstract SecurityGroup[] getDynamicGroupsFromUser(SecurityUser paramSecurityUser);
  
  public abstract SecurityUser[] getAllUsersInGroup(SecurityGroup paramSecurityGroup, Map paramMap, boolean paramBoolean);
}
