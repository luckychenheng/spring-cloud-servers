package com.spring.cloud.gateway.filter;

import com.spring.cloud.auth.client.EnableAceAuthClient;
import com.spring.cloud.auth.client.config.UserAuthConfig;
import com.spring.cloud.auth.client.jwt.UserAuthUtil;
import com.spring.cloud.auth.common.util.jwt.IJWTInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author wangmj
 * @since 2018/10/25
 */
@Configuration
@EnableAceAuthClient
@Slf4j
public class AccessGatewayFilter implements GlobalFilter {

    private static final String GATE_WAY_PREFIX = "/api";
    private final UserAuthConfig userAuthConfig;
    private final UserAuthUtil userAuthUtil;

    @Autowired
    public AccessGatewayFilter(UserAuthConfig userAuthConfig, UserAuthUtil userAuthUtil) {
        this.userAuthConfig = userAuthConfig;
        this.userAuthUtil = userAuthUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("check token and user permission....");
        LinkedHashSet requiredAttribute = exchange.getRequiredAttribute(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
        ServerHttpRequest request = exchange.getRequest();
        String requestUri = request.getPath().pathWithinApplication().value();
        if (requiredAttribute != null) {
            Iterator<URI> iterator = requiredAttribute.iterator();
            while (iterator.hasNext()) {
                URI next = iterator.next();
                if (next.getPath().startsWith(GATE_WAY_PREFIX)) {
                    requestUri = next.getPath().substring(GATE_WAY_PREFIX.length());
                }
            }
        }
        final String method = request.getMethod().toString();
        ServerHttpRequest.Builder mutate = request.mutate();
        IJWTInfo user = null;
        try {
            user = getJWTUser(request, mutate);
        } catch (Exception e) {
            log.error("用户Token过期异常", e);
            return null;
        }
        ServerHttpRequest build = mutate.build();
        return chain.filter(exchange.mutate().request(build).build());
    }

    /**
     * 返回session中的用户信息
     *
     * @param request
     * @param ctx
     * @return
     */
    private IJWTInfo getJWTUser(ServerHttpRequest request, ServerHttpRequest.Builder ctx) throws Exception {
        List<String> strings = request.getHeaders().get(userAuthConfig.getTokenHeader());
        String authToken = null;
        if (strings != null) {
            authToken = strings.get(0);
        }
        if (StringUtils.isBlank(authToken)) {
            strings = request.getQueryParams().get("token");
            if (strings != null) {
                authToken = strings.get(0);
            }
        }
        ctx.header(userAuthConfig.getTokenHeader(), authToken);
        return userAuthUtil.getInfoFromToken(authToken);
    }
}
