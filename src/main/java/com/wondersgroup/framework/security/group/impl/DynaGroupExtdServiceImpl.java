package com.wondersgroup.framework.security.group.impl;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.security.bo.SecurityUser;
import com.wondersgroup.framework.security.group.DynaGroupExtdService;
import com.wondersgroup.framework.security.service.UserExtdService;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("DynaGroupExtService")
@Transactional
public class DynaGroupExtdServiceImpl implements DynaGroupExtdService
{
  @Autowired
  private UserExtdService userService;
 
  public Page getAllUsersByPage(Map filter, Map sort, int pageNo, int pageSize)
  {
    return this.userService.getAllUsersByPage(filter, sort, pageNo, pageSize);
  }
  
  public SecurityUser[] getAllUsersInGroup(Map filter)
  {
    return this.userService.getAllUsers();
  }
}
