package com.example.PAP_API.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class AppProperties {

    @Value("${app.portal.login-url}")
    private String loginUrl;

    @Value("${spring.mail.from}")
    private String emailFrom;

    @Value("app.frontend.origin")
    private String allowedFrontendOrigin;
}
