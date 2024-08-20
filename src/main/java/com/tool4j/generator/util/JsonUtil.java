package com.tool4j.generator.util;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * 使用此工具可保证内部Map对象的字段顺序
 *
 * @author Deng.Weiping
 * @since 2023/7/31 9:13
 */
@Slf4j
public class JsonUtil {

    public static String obj2JsonString(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String result = objectMapper.writeValueAsString(obj);
            return result;
        } catch (JsonProcessingException e) {
            log.error("转换JSON失败：", e);
        }
        return null;
    }

    public static Object str2Obj(String input) {
        if (StrUtil.isBlank(input)) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(input, Object.class);
        } catch (JsonProcessingException e) {
            log.error("转换JSON失败：", e);
        }
        return null;
    }

}
