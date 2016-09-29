package com.wondersgroup.framework.core5.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class GenericsExtUtils
{
  private static final Log LOGGER = LogFactory.getLog(GenericsUtils.class);
  
  public static Class getSuperClassGenricType(Class clazz)
  {
    return getSuperClassGenricType(clazz, 0);
  }
  
  public static Class getSuperClassGenricType(Class clazz, int index)
  {
    Class superClass = clazz.getSuperclass();
    if ((Object.class.equals(superClass)) || (superClass == null))
    {
      Class[] interfaces = clazz.getInterfaces();
      if ((interfaces != null) && (interfaces.length > 0)) {
        for (Class inf : interfaces) {
          try
          {
            return getSuperClassGenericType(clazz, inf, index);
          }
          catch (GenericTypeNotFoundException e) {}
        }
      }
      throw new GenericTypeNotFoundException(clazz.getName() + " never parameterized from any generic type");
    }
    return getSuperClassGenericType(clazz, clazz.getSuperclass(), index);
  }
  
  public static Class<?> getSuperClassGenericType(Class<?> targetType, Class<?> superGenericType, int paramIndex)
  {
    if (targetType == null) {
      throw new IllegalArgumentException("Target type should not be null");
    }
    if (superGenericType == null) {
      throw new IllegalArgumentException("Super generic type should not be null");
    }
    if (paramIndex < 0) {
      throw new IllegalArgumentException("Parameter index should not be less than 0");
    }
    TypeVariable<?>[] typeVariables = superGenericType.getTypeParameters();
    if (typeVariables.length == 0) {
      throw new IllegalArgumentException(superGenericType.getName() + " isn't a generic type");
    }
    if (typeVariables.length <= paramIndex) {
      throw new IllegalArgumentException(superGenericType.getName() + " has " + typeVariables.length + " type parameter(s),but request " + paramIndex);
    }
    if (!superGenericType.isAssignableFrom(targetType)) {
      throw new IllegalArgumentException("Target type " + targetType.getName() + " is not subclass of generic type " + superGenericType.getName());
    }
    if (superGenericType.equals(targetType)) {
      throw new IllegalArgumentException("Target type cann't same as super generic type");
    }
    Map<Class<?>, GenericInfo> map = getGenericInfos(targetType);
    GenericInfo genericInfo = (GenericInfo)map.get(superGenericType);
    if (genericInfo == null) {
      throw new GenericTypeNotFoundException(superGenericType.getName() + " isn't super generic type of " + targetType.getName());
    }
    if ((genericInfo.genericTypes[paramIndex] instanceof TypeVariable)) {
      throw new GenericTypeNotFoundException(superGenericType.getName() + " is super generic type of " + targetType.getName() + ",but never parameterized!");
    }
    return (Class)genericInfo.genericTypes[paramIndex];
  }
  
  public static class GenericInfo
  {
    public Type type;
    public Type[] genericTypes;
    public boolean parameterized = true;
    public Type ownerType;
    public boolean parsed = false;
    public TypeVariable<?>[] typeVariables;
  }
  
  public static Map<Class<?>, GenericInfo> getGenericInfos(Class<?> type)
  {
    List<GenericInfo> result = new ArrayList();
    
    TypeVariable<?>[] typeVariables = type.getTypeParameters();
    if (typeVariables.length > 0)
    {
      GenericInfo genericInfo = new GenericInfo();
      genericInfo.parameterized = false;
      genericInfo.parsed = true;
      genericInfo.type = type;
      genericInfo.typeVariables = typeVariables;
      genericInfo.genericTypes = typeVariables;
      result.add(genericInfo);
    }
    getGenericInfos(type, result, type);
    
    parseGenericInfo(result, result.size() - 1);
    if (typeVariables.length > 0) {
      result.remove(0);
    }
    Map<Class<?>, GenericInfo> map = new HashMap();
    for (GenericInfo genericInfo : result) {
      map.put((Class)genericInfo.type, genericInfo);
    }
    return map;
  }
  
  private static void getGenericInfos(Class<?> type, List<GenericInfo> result, Class<?> ownerType)
  {
    Type[] genTypes = type.getGenericInterfaces();
    if (genTypes.length > 0) {
      for (Type genType : genTypes) {
        if ((genType instanceof ParameterizedType))
        {
          ParameterizedType pType = (ParameterizedType)genType;
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Parameterized type found " + ((Class)pType.getRawType()).getName());
          }
          GenericInfo genericInfo = new GenericInfo();
          genericInfo.type = pType.getRawType();
          genericInfo.genericTypes = pType.getActualTypeArguments();
          genericInfo.ownerType = ownerType;
          for (Type actualArg : pType.getActualTypeArguments()) {
            if ((actualArg instanceof TypeVariable)) {
              genericInfo.parameterized = false;
            }
          }
          if (genericInfo.parameterized) {
            genericInfo.parsed = true;
          }
          genericInfo.typeVariables = ((Class)pType.getRawType()).getTypeParameters();
          result.add(genericInfo);
          
          getGenericInfos((Class)pType.getRawType(), result, (Class)pType.getRawType());
        }
        else if ((genType instanceof Class))
        {
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Recursive find generic type " + ((Class)genType).getName());
          }
          getGenericInfos((Class)genType, result, (Class)genType);
        }
      }
    }
    Type genType = type.getGenericSuperclass();
    if ((genType != null) && (!genType.equals(Object.class))) {
      if ((genType instanceof ParameterizedType))
      {
        ParameterizedType pType = (ParameterizedType)genType;
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Parameterized type found " + ((Class)pType.getRawType()).getName());
        }
        GenericInfo genericInfo = new GenericInfo();
        genericInfo.type = pType.getRawType();
        genericInfo.genericTypes = pType.getActualTypeArguments();
        genericInfo.ownerType = ownerType;
        for (Type actualArg : pType.getActualTypeArguments()) {
          if ((actualArg instanceof TypeVariable)) {
            genericInfo.parameterized = false;
          }
        }
        if (genericInfo.parameterized) {
          genericInfo.parsed = true;
        }
        genericInfo.typeVariables = ((Class)pType.getRawType()).getTypeParameters();
        result.add(genericInfo);
        
        getGenericInfos((Class)pType.getRawType(), result, (Class)pType.getRawType());
      }
      else if ((genType instanceof Class))
      {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Recursive find generic type " + ((Class)genType).getName());
        }
        getGenericInfos((Class)genType, result, (Class)genType);
      }
    }
  }
  
  protected static void parseGenericInfo(List<GenericInfo> result, int index)
  {
    for (int i = index; i >= 0; i--)
    {
      GenericInfo genericInfo = (GenericInfo)result.get(i);
      if ((!genericInfo.parameterized) && (genericInfo.ownerType != null) && (!genericInfo.parsed))
      {
        if (index > 0) {
          parseGenericInfo(result, index - 1);
        }
        boolean parameterized = true;
        for (int j = 0; j < genericInfo.typeVariables.length; j++) {
          if ((genericInfo.genericTypes[j] instanceof TypeVariable))
          {
            TypeVariable<?> typeVariable = (TypeVariable)genericInfo.genericTypes[j];
            for (int k = index - 1; k >= 0; k--)
            {
              GenericInfo ownerInfo = (GenericInfo)result.get(k);
              if (genericInfo.ownerType.equals(ownerInfo.type))
              {
                for (int l = 0; l < ownerInfo.typeVariables.length; l++) {
                  if (typeVariable.getName().equals(ownerInfo.typeVariables[l].getName()))
                  {
                    if ((ownerInfo.genericTypes[l] instanceof Class))
                    {
                      genericInfo.genericTypes[j] = ownerInfo.genericTypes[l];
                      break;
                    }
                    parameterized = false;
                    
                    break;
                  }
                }
                break;
              }
            }
          }
        }
        if (parameterized) {
          genericInfo.parameterized = true;
        }
        genericInfo.parsed = true;
      }
    }
  }
}
