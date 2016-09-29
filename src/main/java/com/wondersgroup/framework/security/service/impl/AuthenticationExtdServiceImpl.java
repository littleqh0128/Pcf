package com.wondersgroup.framework.security.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondersgroup.framework.security.authentication.connector.impl.AbstractAuthenticationConnectorExtdImpl;
import com.wondersgroup.framework.security.authentication.identity.impl.UsernamePasswordAuthenticIdentityExtdImpl;
import com.wondersgroup.framework.security.authentication.provider.AuthenticationProviderExtd;
import com.wondersgroup.framework.security.service.AuthenticationExtdService;

@Service("AuthenticationExtService")
@Transactional
public class AuthenticationExtdServiceImpl extends AbstractAuthenticationConnectorExtdImpl implements AuthenticationExtdService
{
	@Autowired
	AuthenticationProviderExtd authenticationProviderExt;
	
	public boolean authentication(String loginName, String password)
	{
		return authenticationProviderExt.isAuthentication(new UsernamePasswordAuthenticIdentityExtdImpl(loginName, password));
				
				//authentication(new UsernamePasswordAuthenticIdentityExtImpl(loginName, password));
	}
}
