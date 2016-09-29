package com.wondersgroup.pcf.common.constants.factory;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class EnumFinderFactory {

  /**
   * create a new finder to access enum constant values.
   * 
   * @param enumClass
   * @return EnumFinder<K, V>
   */
  @SuppressWarnings("unchecked")
  public static <K, V> EnumFinder<K, V> newEnumFinder(Class<? extends IEnum<K, V>> enumClass) {

    try {
      // get enum values by access enum class static method.
      Method method = enumClass.getDeclaredMethod("values", (Class<?>[]) null);
      IEnum<K, V>[] values = (IEnum<K, V>[]) method.invoke(null, (Object[]) null);

      return new DefaultEnumFinder<K, V>(values);
    } catch (Exception e) {
      return null;
    }

  }

  /**
   * EnumFinder default implementation
   * 
   * @param <K>
   * @param <V>
   */
  static class DefaultEnumFinder<K, V> implements EnumFinder<K, V> {

    private IEnum<K, V>[] values;

    private Map<K, V> map;

    public DefaultEnumFinder(IEnum<K, V>[] values) {
      this.values = values;
    }

    /**
     * @{inheritDoc}
     */
    public V getName(K id) {
      return getMap().get(id);
    }

    /**
     * @{inheritDoc}
     */
    public Map<K, V> getMap() {

      if (null == map) {
        map = new LinkedHashMap<K, V>(values.length);

        for (IEnum<K, V> t : values) {
          map.put(t.getValue(), t.getName());
        }
      }

      return map;
    }

    /**
     * @{inheritDoc}
     */
    public IEnum<K, V> byId(K k) {
      if (k == null)
        return null;
      for (IEnum<K, V> t : values) {
        if (t.getValue().equals(k)) {
          return t;
        }
      }

      return null;
    }

    /**
     * @{inheritDoc}
     */
    public IEnum<K, V> byName(V v) {
      if (v == null)
        return null;
      for (IEnum<K, V> t : values) {
        if (t.getName().equals(v)) {
          return t;
        }
      }
      return null;
    }

    /**
     * @{inheritDoc}
     */
    public IEnum<K, V> byIdOrDefault(K k, IEnum<K, V> e) {
      IEnum<K, V> t = byId(k);
      return t == null ? e : t;
    }

    /**
     * @{inheritDoc}
     */
    public IEnum<K, V> byNameOrDefault(V v, IEnum<K, V> e) {
      IEnum<K, V> t = byName(v);
      return t == null ? e : t;
    }

  }

}
