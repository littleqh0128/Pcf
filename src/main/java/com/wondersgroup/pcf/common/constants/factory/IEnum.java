package com.wondersgroup.pcf.common.constants.factory;


public interface IEnum<K, V> {

    /**
     * Value
     * 
     * @return K
     */
    K getValue();

    /**
     * 名称
     * 
     * @return V
     */
    V getName();

}
