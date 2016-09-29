package com.wondersgroup.framework.security.authentication.provider;

import com.wondersgroup.framework.security.authentication.identity.AuthenticIdentityExtd;

public abstract interface AuthenticationProviderExtd
{
	public abstract boolean isAuthentication(AuthenticIdentityExtd paramAuthenticIdentity);
}
