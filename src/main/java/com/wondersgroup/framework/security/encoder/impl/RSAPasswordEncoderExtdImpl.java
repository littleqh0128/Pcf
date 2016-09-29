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
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service("RSAPasswordEncoderExtd")
public class RSAPasswordEncoderExtdImpl
  extends AbstractPasswordEncoderExtdImpl
  implements PasswordEncoder
{
  private static final String ALGORITHM_NAME = "RSA";
  private static final String PUBLIC_KEY_FILE = "RSAPublicKey.dat";
  private static final String PRIVATE_KEY_FILE = "RSAPrivateKey.dat";
  private static KeyPair secretKey = null;
  
  protected String encodeInternal(String input)
  {
    String result = null;
    if (secretKey == null) {
      secretKey = readKeyFromFile();
    }
    try
    {
      Cipher cipher = Cipher.getInstance("RSA");
      cipher.init(1, secretKey.getPublic());
      byte[] encoded = cipher.doFinal(input.getBytes());
      result = new String(Hex.encodeHex(encoded));
    }
    catch (GeneralSecurityException ex) {}
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
      Cipher cipher = Cipher.getInstance("RSA");
      cipher.init(2, secretKey.getPrivate());
      byte[] decodeHex = Hex.decodeHex(endPassword.toCharArray());
      byte[] decoded = cipher.doFinal(decodeHex);
      result = new String(decoded);
    }
    catch (GeneralSecurityException ex) {
    	
    }
    catch (DecoderException ex) {
    	
    }
    return result;
  }
  
  public boolean isPasswordValid(String encPassword, String rawPassword, Object salt)
  {
    String decodePassword = decodePassword(encPassword);
    return rawPassword.equals(decodePassword);
  }
  
  private KeyPair readKeyFromFile()
  {
    KeyPair key = null;
    try
    {
      ObjectInputStream inputStream1 = new ObjectInputStream(new ClassPathResource("RSAPublicKey.dat").getInputStream());
      
      PublicKey publicKey = (PublicKey)inputStream1.readObject();
      inputStream1.close();
      ObjectInputStream inputStream2 = new ObjectInputStream(new ClassPathResource("RSAPrivateKey.dat").getInputStream());
      
      PrivateKey privateKey = (PrivateKey)inputStream2.readObject();
      inputStream2.close();
      key = new KeyPair(publicKey, privateKey);
    }
    catch (FileNotFoundException ex)
    {
      key = generatorKeyFile();
    }
    catch (IOException ex)
    {
      throw new SecurityException(ex);
    }
    catch (ClassNotFoundException ex)
    {
      throw new SecurityException(ex);
    }
    return key;
  }
  
  public KeyPair generatorKeyFile()
  {
    KeyPair generateKey = null;
    try
    {
      KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
      generator.initialize(1024, new SecureRandom());
      generateKey = generator.generateKeyPair();
      ObjectOutputStream outputStreamPublic = new ObjectOutputStream(new FileOutputStream("RSAPublicKey.dat"));
      ObjectOutputStream outputStreamPrivate = new ObjectOutputStream(new FileOutputStream("RSAPrivateKey.dat"));
      outputStreamPublic.writeObject(generateKey.getPublic());
      outputStreamPrivate.writeObject(generateKey.getPrivate());
      outputStreamPublic.close();
      outputStreamPrivate.close();
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
