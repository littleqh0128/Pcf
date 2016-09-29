package com.wondersgroup.framework.security.authentication.identity.impl;

import com.wondersgroup.framework.security.authentication.identity.UsernamePasswordAuthenticIdentityExtd;

public class UsernamePasswordAuthenticIdentityExtdImpl implements UsernamePasswordAuthenticIdentityExtd
{
  private String loginName;
  private String password;
  
  public UsernamePasswordAuthenticIdentityExtdImpl(String loginName, String password)
  {
    this.loginName = loginName;
    this.password = password;
  }
  
  public String getLoginName()
  {
    return this.loginName;
  }
  
  public void setLoginName(String loginName)
  {
    this.loginName = loginName;
  }
  
  public String getPassword()
  {
    return this.password;
  }
  
  public void setPassword(String password)
  {
    this.password = password;
  }
}
