package com.spring.cloud.gateway.filter;

import com.spring.cloud.auth.client.EnableAceAuthClient;
import com.spring.cloud.auth.client.config.UserAuthConfig;
import com.spring.cloud.auth.client.exception.JwtIllegalArgumentException;
import com.spring.cloud.auth.client.exception.JwtSignatureException;
import com.spring.cloud.auth.client.exception.JwtTokenExpiredException;
import com.spring.cloud.auth.client.jwt.UserAuthUtil;
import com.spring.cloud.auth.common.util.jwt.IJWTInfo;
import com.spring.cloud.gateway.feign.AuthServiceFeign;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
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
    @Value("${jwt.refreshMinSecond}")
    private int refreshMinSecond;
    private final UserAuthConfig userAuthConfig;
    private final UserAuthUtil userAuthUtil;
    private final AuthServiceFeign authServiceFeign;

    @Autowired
    public AccessGatewayFilter(UserAuthConfig userAuthConfig, UserAuthUtil userAuthUtil, AuthServiceFeign authServiceFeign) {
        this.userAuthConfig = userAuthConfig;
        this.userAuthUtil = userAuthUtil;
        this.authServiceFeign = authServiceFeign;
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
        } catch (ExpiredJwtException ex){
            log.error("User token expired!");
        }catch (SignatureException ex){
            log.error("User token signature error!");
        }catch (IllegalArgumentException ex){
            log.error("User token is null or empty!");
        }catch (Exception e) {
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
        if (StringUtils.isEmpty(authToken)) {
            strings = request.getQueryParams().get("token");
            if (strings != null) {
                authToken = strings.get(0);
            }
        }
        IJWTInfo ijwtInfo = userAuthUtil.getInfoFromToken(authToken);
        int exp = ijwtInfo.getExp();
        if (!StringUtils.isEmpty(exp)) {
            long differenceMillis = Long.valueOf(exp) * 1000L - DateTime.now().getMillis() ;
            long refreshMinMillis = Integer.valueOf(refreshMinSecond).longValue() * 1000L;
            if( 0 < differenceMillis && differenceMillis < refreshMinMillis) {
                String refreshToken = authServiceFeign.refresh(authToken);
                authToken = refreshToken;
            }
        }
        ctx.header(userAuthConfig.getTokenHeader(), authToken);
        return ijwtInfo;
    }
}
