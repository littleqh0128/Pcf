package com.wondersgroup.framework.security.encoder.impl;

import com.wondersgroup.framework.security.encoder.AbstractPasswordEncoderExtdImpl;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

@Service("MD5PasswordEncoderExtd")
public class MD5PasswordEncoderExtdImpl
  extends AbstractPasswordEncoderExtdImpl
{
  protected String encodeInternal(String input)
  {
    if (!isEncodeHashAsBase64()) {
      return DigestUtils.md5Hex(input);
    }
    byte[] encoded = Base64.encodeBase64(DigestUtils.md5(input));
    return new String(encoded);
  }
}
