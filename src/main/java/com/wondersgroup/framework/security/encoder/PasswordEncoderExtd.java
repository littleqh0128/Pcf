package com.wondersgroup.framework.security.encoder;

public abstract interface PasswordEncoderExtd
{
  public abstract boolean isPasswordValid(String paramString1, String paramString2, Object paramObject);
  
  public abstract String encodePassword(String paramString, Object paramObject);
  
  public abstract void setEncodeHashAsBase64(boolean paramBoolean);
  
  public abstract String decodePassword(String paramString);
}
