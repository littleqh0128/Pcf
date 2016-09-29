package com.wondersgroup.framework.security.authentication.identity;

public abstract interface UsernamePasswordAuthenticIdentityExtd extends AuthenticIdentityExtd
{
  public abstract void setLoginName(String paramString);
  
  public abstract void setPassword(String paramString);
  
  public abstract String getLoginName();
  
  public abstract String getPassword();
}
