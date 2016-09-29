package com.wondersgroup.framework.security.encoder.impl;

import org.springframework.stereotype.Service;

import com.wondersgroup.framework.security.encoder.AbstractPasswordEncoderExtdImpl;

@Service("PlainTextPasswordEncoderExtd")
public class PlainTextPasswordEncoderExtdImpl
  extends AbstractPasswordEncoderExtdImpl
{
  protected String encodeInternal(String input)
  {
    return input;
  }
}
