package com.milesight.iab.authentication.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author loong
 * @date 2024/10/16 8:59
 */
public class OAuth2ResponseUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static void response(HttpServletResponse response, String code, String msg, Object data) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.getWriter().write(objectMapper.writeValueAsString(responseResult(code, msg, data)));
    }

    public static Map<String, Object> responseResult(String code, String msg, Object data) {
        Map<String, Object> map = new HashMap<>(3);
        map.put("code", code);
        map.put("msg", msg);
        map.put("data", data);
        return map;
    }

}
