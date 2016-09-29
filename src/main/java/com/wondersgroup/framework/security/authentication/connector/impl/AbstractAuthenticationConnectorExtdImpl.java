package com.wondersgroup.framework.security.authentication.connector.impl;

import com.wondersgroup.framework.security.authentication.connector.AuthenticationConnectorExtd;
import com.wondersgroup.framework.security.authentication.identity.AuthenticIdentityExtd;
import com.wondersgroup.framework.security.authentication.provider.AuthenticationProviderExtd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AbstractAuthenticationConnectorExtdImpl implements AuthenticationConnectorExtd
{
  private List authenticationProviders;
  
  public void setAuthenticationProviders(List authenticationProviders)
  {
    List result = new ArrayList();
    if ((authenticationProviders == null) || (authenticationProviders.size() == 0)) {
      return;
    }
    for (Iterator i = authenticationProviders.iterator(); i.hasNext();)
    {
      Object authenticationProvider = i.next();
      if (AuthenticationProviderExtd.class.isInstance(authenticationProvider)) {
        result.add((AuthenticationProviderExtd)authenticationProvider);
      }
    }
    this.authenticationProviders = result;
  }
  
  public boolean authentication(AuthenticIdentityExtd authenticIdentity)
  {
    boolean result = true;
    for (Iterator i = this.authenticationProviders.iterator(); i.hasNext();)
    {
      AuthenticationProviderExtd ap = (AuthenticationProviderExtd)i.next();
      if (result) {
        result = ap.isAuthentication(authenticIdentity);
      }
    }
    return result;
  }
}
