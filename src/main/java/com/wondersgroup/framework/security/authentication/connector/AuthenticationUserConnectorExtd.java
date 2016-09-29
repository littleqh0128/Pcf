package com.wondersgroup.framework.security.authentication.connector;

public abstract interface AuthenticationUserConnectorExtd
{
  public abstract AuthenticationUser getAuthenticationUserByLoginName(String paramString);
}
