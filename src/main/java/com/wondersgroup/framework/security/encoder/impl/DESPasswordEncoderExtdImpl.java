package com.wondersgroup.framework.security.encoder.impl;

import com.wondersgroup.framework.security.encoder.AbstractPasswordEncoderExtdImpl;
import com.wondersgroup.framework.security.encoder.PasswordEncoder;
import com.wondersgroup.framework.security.exception.SecurityException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service("DESPasswordEncoderExtd")
public class DESPasswordEncoderExtdImpl
  extends AbstractPasswordEncoderExtdImpl
  implements PasswordEncoder
{
  private static final String ALGORITHM_NAME = "DES";
  private static final String SECRET_KEY_FILENAME = "DesPrivateKey.dat";
  private static SecretKey secretKey = null;
  
  protected String encodeInternal(String input)
  {
    String result = null;
    if (secretKey == null) {
      secretKey = readKeyFromFile();
    }
    try
    {
      Cipher cipher = Cipher.getInstance("DES");
      cipher.init(1, secretKey);
      byte[] encoded = cipher.doFinal(input.getBytes());
      if (isEncodeHashAsBase64()) {
        result = new String(Base64.encodeBase64(encoded));
      } else {
        result = new String(Hex.encodeHex(encoded));
      }
    }
    catch (GeneralSecurityException ex)
    {
      throw new SecurityException(ex);
    }
    return result;
  }
  
  public String decodePassword(String endPassword)
  {
    String result = null;
    if (secretKey == null) {
      secretKey = readKeyFromFile();
    }
    try
    {
      Cipher cipher = Cipher.getInstance("DES");
      cipher.init(2, secretKey);
      byte[] decodeHex;
      if (isEncodeHashAsBase64()) {
        decodeHex = Base64.decodeBase64(endPassword.getBytes());
      } else {
        decodeHex = Hex.decodeHex(endPassword.toCharArray());
      }
      byte[] decoded = cipher.doFinal(decodeHex);
      result = new String(decoded);
    }
    catch (GeneralSecurityException ex)
    {
      throw new SecurityException(ex);
    }
    catch (DecoderException ex)
    {
      ex.printStackTrace();
    }
    return result;
  }
  
  private SecretKey readKeyFromFile()
  {
    SecretKey key = null;
    try
    {
      ObjectInputStream inputStream = new ObjectInputStream(new ClassPathResource("DesPrivateKey.dat").getInputStream());
      
      key = (SecretKey)inputStream.readObject();
      inputStream.close();
    }
    catch (FileNotFoundException e)
    {
      key = generatorKeyFile();
    }
    catch (IOException ex)
    {
      throw new SecurityException(ex);
    }
    catch (ClassNotFoundException ex)
    {
      key = generatorKeyFile();
    }
    return key;
  }
  
  private SecretKey generatorKeyFile()
  {
    SecretKey generateKey = null;
    try
    {
      KeyGenerator generator = KeyGenerator.getInstance("DES");
      generator.init(new SecureRandom());
      generateKey = generator.generateKey();
      ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("DesPrivateKey.dat"));
      outputStream.writeObject(generateKey);
      outputStream.close();
    }
    catch (NoSuchAlgorithmException ex)
    {
      throw new SecurityException(ex);
    }
    catch (FileNotFoundException ex)
    {
      throw new SecurityException(ex);
    }
    catch (IOException ex)
    {
      throw new SecurityException(ex);
    }
    return generateKey;
  }
}
