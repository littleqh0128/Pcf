package com.wondersgroup.framework.security.group;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.security.bo.SecurityUser;
import java.util.Map;

public abstract interface DynaGroupExtdService
{
  public abstract Page getAllUsersByPage(Map paramMap1, Map paramMap2, int paramInt1, int paramInt2);
  
  public abstract SecurityUser[] getAllUsersInGroup(Map paramMap);
}
