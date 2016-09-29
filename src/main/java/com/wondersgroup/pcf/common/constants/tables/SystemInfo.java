package com.wondersgroup.pcf.common.constants.tables;

import com.wondersgroup.pcf.common.constants.factory.EnumFinder;
import com.wondersgroup.pcf.common.constants.factory.EnumFinderFactory;
import com.wondersgroup.pcf.common.constants.factory.IEnum;

public interface SystemInfo {

  /**
   * 用户状态
   */
  enum UserStatus implements IEnum<String, String> {

	  NORMAL("0", "正常"), 
	  UNNORMAL("-1", "非正常");

	  private String name;
	  private String value;

	  /** 构建EnumFinder类 */
	  public static EnumFinder<String, String> finder = EnumFinderFactory.newEnumFinder(UserStatus.class);

	  /** 构造器 */
	  private UserStatus(String value, String name) {
		  this.name = name;
		  this.value = value;
	  }

	  public String getValue() {
		  return value;
	  }

	  public String getName() {
		  return name;
	  }
	}
}
