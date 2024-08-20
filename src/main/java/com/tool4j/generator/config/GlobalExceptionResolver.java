package com.tool4j.generator.config;

import com.tool4j.generator.entity.vo.Result;
import com.tool4j.generator.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import org.apache.velocity.exception.ParseErrorException;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常拦截
 *
 * @since 0.0.1
 */
@Configuration
@Slf4j
public class GlobalExceptionResolver implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) {
        log.error("系统异常：URI={}", request.getRequestURI(), e);
        response.setStatus(200);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
        try {
            Result result;
            if (e instanceof MethodArgumentNotValidException) {
                result = Result.error("参数校验不通过");
            } else if (e instanceof JSQLParserException) {
                result = Result.error("解析SQL出错，如有疑问请联系管理员", e.getMessage());
            } else if (e instanceof ParseErrorException) {
                result = Result.error("渲染模板失败，请检查您的模板内容", e.getMessage());
            } else {
                result = Result.error("出错啦！请通过系统反馈告知管理员", e.getMessage());
            }
            response.getWriter().write(JsonUtil.obj2JsonString(result));
        } catch (Exception ex) {
            return null;
        }
        return new ModelAndView();
    }

}
