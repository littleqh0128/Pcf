package com.wondersgroup.framework.security.authentication.connector;

import com.wondersgroup.framework.security.authentication.identity.AuthenticIdentityExtd;

public abstract interface AuthenticationConnectorExtd
{
	public abstract boolean authentication(AuthenticIdentityExtd paramAuthenticIdentity);
}
