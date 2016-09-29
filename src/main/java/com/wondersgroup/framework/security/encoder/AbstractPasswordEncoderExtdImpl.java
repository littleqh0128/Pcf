package com.wondersgroup.framework.security.encoder;

public abstract class AbstractPasswordEncoderExtdImpl
  implements PasswordEncoderExtd
{
  private boolean encodeHashAsBase64 = false;
  
  public boolean isEncodeHashAsBase64()
  {
    return this.encodeHashAsBase64;
  }
  
  public void setEncodeHashAsBase64(boolean encodeHashAsBase64)
  {
    this.encodeHashAsBase64 = encodeHashAsBase64;
  }
  
  protected abstract String encodeInternal(String paramString);
  
  public boolean isPasswordValid(String encPassword, String rawPassword, Object salt)
  {
    String pass1 = "" + encPassword;
    String pass2 = encodeInternal(mergePasswordAndSalt(rawPassword, salt, false));
    return pass1.equals(pass2);
  }
  
  public String encodePassword(String rawPassword, Object salt)
  {
    return encodeInternal(mergePasswordAndSalt(rawPassword, salt, false));
  }
  
  public String decodePassword(String encPassword)
  {
    return null;
  }
  
  private String mergePasswordAndSalt(String password, Object salt, boolean strict)
  {
    if (password == null) {
      password = "";
    }
    if ((strict) && (salt != null) && (
      (salt.toString().lastIndexOf("{") != -1) || (salt.toString().lastIndexOf("}") != -1))) {
      throw new IllegalArgumentException("Cannot use { or } in salt.toString()");
    }
    if ((salt == null) || ("".equals(salt))) {
      return password;
    }
    return password + "{" + salt.toString() + "}";
  }
}
