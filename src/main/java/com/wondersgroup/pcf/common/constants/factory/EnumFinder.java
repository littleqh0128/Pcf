package com.wondersgroup.pcf.common.constants.factory;

import java.util.Map;

public interface EnumFinder<K, V> {

  /** 根据指定的ID，取得对应的枚举值 */
  V getName(K id);

  /** 取得枚举中所有的值，并放到Map中 */
  Map<K, V> getMap();

  /** 根据ID取得枚举 */
  IEnum<K, V> byId(K k);

  /** 根据枚举值取得枚举 */
  IEnum<K, V> byName(V v);

  /** 根据指定的ID，取得对应的枚举值. Null的场合返回default值 */
  IEnum<K, V> byIdOrDefault(K k, IEnum<K, V> defaultEnum);

  /** 根据枚举值取得枚举, Null的场合返回default值 */
  IEnum<K, V> byNameOrDefault(V v, IEnum<K, V> defaultEnum);
}