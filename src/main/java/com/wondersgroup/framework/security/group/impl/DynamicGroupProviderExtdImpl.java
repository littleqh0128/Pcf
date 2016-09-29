package com.wondersgroup.framework.security.group.impl;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.security.bo.SecurityGroup;
import com.wondersgroup.framework.security.bo.SecurityUser;
import com.wondersgroup.framework.security.exception.SecurityException;
import com.wondersgroup.framework.security.group.DynaGroupExtdService;
import com.wondersgroup.framework.security.group.DynamicGroupProviderExtd;
import com.wondersgroup.framework.security.service.UserExtdService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("DynamicGroupProviderExt")
@Transactional
public class DynamicGroupProviderExtdImpl implements DynamicGroupProviderExtd, ApplicationContextAware
{
  private ApplicationContext context;
  @Autowired
  private UserExtdService userService;
  
  public void setApplicationContext(ApplicationContext context)
  {
    this.context = context;
  }
  
  public Page getUsersByPageInGroup(SecurityGroup group, Map filter, Map sort, int pageNo, int pageSize)
  {
    DynaGroupExtdService dynaGroupService = getService(group);
    if (filter == null) {
      filter = new HashMap();
    }
    filter.putAll(group.getParamsMap());
    return dynaGroupService.getAllUsersByPage(filter, sort, pageNo, pageSize);
  }
  
  public SecurityUser[] getAllUsersInGroup(SecurityGroup group, Map filter, boolean ignore)
  {
    DynaGroupExtdService dynaGroupService = getService(group);
    SecurityUser[] users1 = dynaGroupService.getAllUsersInGroup(filter);
    if (ignore) {
      return users1;
    }
    SecurityUser[] users2 = this.userService.findUsersBy(group.getParamsMap());
    List userList1 = Arrays.asList(users1);
    List userList2 = Arrays.asList(users2);
    Collection collection = CollectionUtils.intersection(userList1, userList2);
    return (SecurityUser[])collection.toArray(new SecurityUser[collection.size()]);
  }
  
  public SecurityGroup[] getDynamicGroupsFromUser(SecurityUser user)
  {
    List result = new ArrayList();
    SecurityGroup[] groups = this.userService.getAllAuthControlDynaGroups();
    for (int i = 0; i < groups.length; i++) {
      if (isUserInGroup(user, groups[i])) {
        result.add(groups[i]);
      }
    }
    return (SecurityGroup[])result.toArray(new SecurityGroup[result.size()]);
  }
  
  public boolean isUserInGroup(SecurityUser user, SecurityGroup group)
  {
    if (this.userService.isUserInGroup(user, group)) {
      return true;
    }
    if (this.userService.isUserInDynamicGroup(user, group)) {
      return true;
    }
    return false;
  }
  
  private DynaGroupExtdService getService(SecurityGroup group)
  {
    if ((!group.isEntityDyna()) || (group == null)) {
      throw new SecurityException("invalidate dyna group");
    }
    String clazz = group.getDynaAccessClass();
    DynaGroupExtdService dynaGroupService = null;
    try
    {
      dynaGroupService = (DynaGroupExtdService)this.context.getBean(clazz);
    }
    catch (BeansException e)
    {
      e.printStackTrace();
    }
    return dynaGroupService;
  }
}
