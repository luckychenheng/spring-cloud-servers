package com.spring.cloud.auth.server.service.impl;

import com.spring.cloud.auth.common.util.jwt.JWTInfo;
import com.spring.cloud.auth.server.service.AuthService;
import com.spring.cloud.auth.server.util.JwtAuthenticationRequest;
import com.spring.cloud.auth.server.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wangmj
 * @since 2018/10/27
 */
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public String login(JwtAuthenticationRequest authenticationRequest) throws Exception {
        return jwtTokenUtil.generateToken(new JWTInfo(authenticationRequest.getUsername(), 1L + "", "MR.WANG"));
    }
}
