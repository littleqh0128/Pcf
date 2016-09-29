package com.wondersgroup.framework.security.authentication.provider.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wondersgroup.framework.security.authentication.connector.AuthenticationUser;
import com.wondersgroup.framework.security.authentication.connector.AuthenticationUserConnectorExtd;
import com.wondersgroup.framework.security.authentication.identity.AuthenticIdentityExtd;
import com.wondersgroup.framework.security.authentication.identity.UsernamePasswordAuthenticIdentityExtd;
import com.wondersgroup.framework.security.authentication.provider.AuthenticationProviderExtd;
import com.wondersgroup.framework.security.encoder.PasswordEncoderUtilsExtd;
import com.wondersgroup.framework.security.exception.BadCredentialsException;

@Service("AuthenticationProviderExtd")
public class DAOAuthenticationProviderExtdImpl implements AuthenticationProviderExtd
{
  @Autowired
  private AuthenticationUserConnectorExtd securityUserService;
  
  public void setSecurityUserService(AuthenticationUserConnectorExtd securityUserService)
  {
    this.securityUserService = securityUserService;
  }
  
  public void additionalAuthenticationChecks(AuthenticIdentityExtd authenticIdentity) {}
  
  public AuthenticationUser retrieveUser(String loginName)
  {
    AuthenticationUser securityUser = this.securityUserService.getAuthenticationUserByLoginName(loginName);
    if (securityUser == null) {
      throw new BadCredentialsException("user.not.existed");
    }
    return securityUser;
  }
  
  public boolean isAuthentication(AuthenticIdentityExtd authenticIdentity)
  {
    boolean result = false;
    if (UsernamePasswordAuthenticIdentityExtd.class.isInstance(authenticIdentity))
    {
    	UsernamePasswordAuthenticIdentityExtd ai = (UsernamePasswordAuthenticIdentityExtd)authenticIdentity;
    	AuthenticationUser user = retrieveUser(ai.getLoginName());
    	boolean isValid = PasswordEncoderUtilsExtd.isPasswordValid(user.getPassword(), ai.getPassword(), "");
    	if (isValid) {
    		result = true;
    	} else {
    		throw new BadCredentialsException("password.incorrect");
    	}
    }
    return result;
  }
}
