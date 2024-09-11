package com.dti.multiwarehouse.helper;

import org.springframework.data.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class EntityUpdateUtil {
    public static <T, D> void updateEntityFromDto(T entity, D dto) {
        Map<String, Field> entityFields = new HashMap<>();
        for (Field field : entity.getClass().getDeclaredFields()) {
            entityFields.put(field.getName(), field);
        }

        for (Field dtoField : dto.getClass().getDeclaredFields()) {
            dtoField.setAccessible(true);

            try {
                Object newValue = dtoField.get(dto);

                if (newValue != null) {
                    Field entityField = entityFields.get(dtoField.getName());
                    if (entityField != null) {
                        entityField.setAccessible(true);
                        ReflectionUtils.setField(entityField, entity, newValue);
                    } else {
                        System.out.println("Field " + dtoField.getName() + " not found");
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
