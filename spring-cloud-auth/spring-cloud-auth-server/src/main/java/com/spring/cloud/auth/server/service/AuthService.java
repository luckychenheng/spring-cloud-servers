package com.spring.cloud.auth.server.service;

import com.spring.cloud.auth.server.util.JwtAuthenticationRequest;

/**
 * @author wangmj
 * @since 2018/10/27
 */
public interface AuthService {
    String login(JwtAuthenticationRequest authenticationRequest) throws Exception;
}
