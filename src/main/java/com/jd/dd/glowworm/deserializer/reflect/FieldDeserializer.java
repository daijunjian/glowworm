/*
 * Copyright 360buy
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.jd.dd.glowworm.deserializer.reflect;

import com.jd.dd.glowworm.PBException;
import com.jd.dd.glowworm.deserializer.PBDeserializer;
import com.jd.dd.glowworm.util.FieldInfo;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public abstract class FieldDeserializer implements Comparable {

    protected final FieldInfo fieldInfo;

    protected final Class<?> clazz;

    public FieldDeserializer(Class<?> clazz, FieldInfo fieldInfo) {
        this.clazz = clazz;
        this.fieldInfo = fieldInfo;
    }

    public Method getMethod() {
        return fieldInfo.getMethod();
    }

    public Class<?> getFieldClass() {
        return fieldInfo.getFieldClass();
    }

    public Type getFieldType() {
        return fieldInfo.getFieldType();
    }

    public abstract void parseField(PBDeserializer parser, Object object, Type objectType,
                                    Map<String, Object> fieldValues);

    public void setValue(Object object, boolean value) {
        setValue(object, Boolean.valueOf(value));
    }

    public void setValue(Object object, int value) {
        setValue(object, Integer.valueOf(value));
    }

    public void setValue(Object object, long value) {
        setValue(object, Long.valueOf(value));
    }

    public void setValue(Object object, String value) {
        setValue(object, (Object) value);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void setValue(Object object, Object value) {
        Method method = fieldInfo.getMethod();
        if (method != null) {
            try {
                if (fieldInfo.isGetOnly()) {
                    Collection collection = (Collection) method.invoke(object);
                    collection.addAll((Collection) value);
                } else {
                    method.invoke(object, value);
                }
            } catch (Exception e) {
                throw new PBException("set property error, " + fieldInfo.getName(), e);
            }
        } else if (fieldInfo.getField() != null) {
            try {
                fieldInfo.getField().set(object, value);
            } catch (Exception e) {
                throw new PBException("set property error, " + fieldInfo.getName(), e);
            }
        }
    }

    public int compareTo(Object o) {
        return this.getFieldInfo().compareTo(((FieldDeserializer) o).getFieldInfo());
    }

    public FieldInfo getFieldInfo() {
        return fieldInfo;
    }
}
