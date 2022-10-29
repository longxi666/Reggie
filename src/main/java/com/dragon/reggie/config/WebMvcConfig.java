package com.dragon.reggie.config;

import com.dragon.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /**
     * TODO 设置静态资源映射
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始静态映射 ...");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    /**
     * TODO 拓展Mvc的消息转换器
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("拓展消息转换器 ...");
        //TODO 1.创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter= new MappingJackson2HttpMessageConverter();
        //TODO 2.使用对象转换器，底层使用JackSon 将Java对象转换为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //TODO 3.将上面的雄安锡转换器对象追加到Mvc框架的转换器集合中
        converters.add(0,messageConverter);
    }
}
