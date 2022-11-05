package com.openjob.web.util;

import org.apache.commons.beanutils.BeanUtilsBean;

import java.lang.reflect.InvocationTargetException;

public class NullAwareBeanUtils extends BeanUtilsBean {
    private static NullAwareBeanUtils _this;
    public static NullAwareBeanUtils getInstance(){
        return _this == null ? new NullAwareBeanUtils() : _this;
    }
    @Override
    public void copyProperty(Object dest, String name, Object value)
            throws IllegalAccessException, InvocationTargetException {
        if(value==null)return;
        super.copyProperty(dest, name, value);
    }
}
