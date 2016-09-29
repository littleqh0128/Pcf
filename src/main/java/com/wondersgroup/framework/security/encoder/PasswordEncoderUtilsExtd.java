package com.wondersgroup.framework.security.encoder;

import com.wondersgroup.pcf.common.utils.SpringContextUtil;
import com.wondersgroup.framework.security.encoder.impl.SHAPasswordEncoderExtdImpl;

public class PasswordEncoderUtilsExtd
{
  public static final String USERORG_ENCRYPTTYPE = "userorg_encrypttype";
  public static final String DEFAULT_PASSWORD_ENCODER = "defaultPasswordEncoder";
  private static PasswordEncoderExtd passwordEncoder = null;
  
  private static PasswordEncoderExtd getInstance()
  {
    if (passwordEncoder == null)
    {
      //String beanName = SystemParameter.getString("userorg_encrypttype", "defaultPasswordEncoder");
      passwordEncoder = (PasswordEncoderExtd) SpringContextUtil.getBean(SHAPasswordEncoderExtdImpl.class);
    }
    return passwordEncoder;
  }
  
  public static boolean isPasswordValid(String encPassword, String rawPassword, Object salt)
  {
    getInstance();
    return passwordEncoder.isPasswordValid(encPassword, rawPassword, salt);
  }
  
  public static String encodePassword(String rawPassword, Object salt)
  {
    return getInstance().encodePassword(rawPassword, salt);
  }
  
  public static String decodePassword(String encPassword)
  {
    return getInstance().decodePassword(encPassword);
  }
}
