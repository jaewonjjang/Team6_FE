package com.kaspi.backend.util.cookie;

import java.net.MalformedURLException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * 쿠키 samesite 설정 none
 * 추후 배포시 삭제 예정
 */
@Configuration
public class CookieSerialize {
    @Bean
    public CookieSerializer cookieSerializer() throws MalformedURLException {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setSameSite("None");
        return serializer;
    }
}
