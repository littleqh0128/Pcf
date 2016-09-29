package com.wondersgroup.framework.security.encoder.impl;

import com.wondersgroup.framework.security.encoder.AbstractPasswordEncoderExtdImpl;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

@Service("SHAPasswordEncoderExtd")
public class SHAPasswordEncoderExtdImpl
  extends AbstractPasswordEncoderExtdImpl
{
  protected String encodeInternal(String input)
  {
    if (!isEncodeHashAsBase64()) {
      return DigestUtils.shaHex(input);
    }
    byte[] encoded = Base64.encodeBase64(DigestUtils.sha(input));
    return new String(encoded);
  }
}
