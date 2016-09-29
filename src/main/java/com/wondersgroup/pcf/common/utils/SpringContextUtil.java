package com.wondersgroup.pcf.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Find Spring a Bean
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

  /*-------------------------------------------
  |    I N S T A N C E   V A R I A B L E S    |
  ============================================*/
  private static ApplicationContext applicationContext;

  /*-------------------------------------------
  |               M E T H O D S               |
  ============================================*/

  public static <T> T getBean(Class<T> clazz) throws BeansException {
    return applicationContext.getBean(clazz);
  }

  public static Object getBean(String clazzName) throws BeansException {
    return applicationContext.getBean(clazzName);
  }
  
  @SuppressWarnings("unchecked")
  public static <T> T getBean(Class<T> clazz, String qualifier) throws BeansException {
    return (T) applicationContext.getBean(StringUtils.uncapitalize(qualifier));
  }

  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    SpringContextUtil.applicationContext = applicationContext;
  }
}
