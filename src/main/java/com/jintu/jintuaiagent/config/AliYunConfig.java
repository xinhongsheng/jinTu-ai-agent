package com.jintu.jintuaiagent.config;

import lombok.Data;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Author: 小辛同学
 * @CreateTime: 2026-01-05
 * @Description: 阿里云配置类
 * @Version: 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.key")
public class AliYunConfig {
    private String apiKey;
}
