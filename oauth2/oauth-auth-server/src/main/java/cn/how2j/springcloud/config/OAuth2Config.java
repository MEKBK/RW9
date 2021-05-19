package cn.how2j.springcloud.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.builders.JdbcClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableAuthorizationServer
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {

    @Autowired
    //PasswordEncoder是Security提供的一个接口，目前这个接口提供了三个方法
    public PasswordEncoder passwordEncoder;

    @Autowired
    //设置用户验证服务
    public UserDetailsService kiteUserDetailsService;

    @Autowired
    //调用此方法才能支持 password 模式
    private AuthenticationManager authenticationManager;

    @Autowired
    //指定 token 的存储方式
    private TokenStore redisTokenStore;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TokenStore jwtTokenStore;

    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Autowired
    private TokenEnhancer jwtTokenEnhancer;

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        /**
         * 普通 jwt 模式
         */
         endpoints.tokenStore(jwtTokenStore)
                .accessTokenConverter(jwtAccessTokenConverter)
                .userDetailsService(kiteUserDetailsService)
                /**
                 * 支持 password 模式
                 */
                .authenticationManager(authenticationManager);

//        /**
//         * jwt 增强模式
//         */
//        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
//        List<TokenEnhancer> enhancerList = new ArrayList<>();
//        enhancerList.add(jwtTokenEnhancer);
//        enhancerList.add(jwtAccessTokenConverter);
//        enhancerChain.setTokenEnhancers(enhancerList);
//        endpoints.tokenStore(jwtTokenStore)
//                .userDetailsService(kiteUserDetailsService)
//                /**
//                 * 支持 password 模式
//                 */
//                .authenticationManager(authenticationManager)
//                .tokenEnhancer(enhancerChain)
//                .accessTokenConverter(jwtAccessTokenConverter);

//        /**
//         * redis token 方式
//         */
//        endpoints.authenticationManager(authenticationManager)
//                .tokenStore(redisTokenStore)
//                .userDetailsService(kiteUserDetailsService);

    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

        JdbcClientDetailsServiceBuilder jcsb = clients.jdbc(dataSource);
        jcsb.passwordEncoder(passwordEncoder);

        //使用 inMemory 方式存储的，将配置保存到内存中，相当于硬编码了
//        clients.inMemory()
//                .withClient("order-client")
//                //
//                .secret(passwordEncoder.encode("order-secret-8888"))
//                //授权码类型
//                .authorizedGrantTypes("refresh_token", "authorization_code", "password")
//                //token 的有效期
//                .accessTokenValiditySeconds(3600)
//                .scopes("all")
//                .and()
//                .withClient("user-client")
//                .secret(passwordEncoder.encode("user-secret-8888"))
//                .authorizedGrantTypes("refresh_token", "authorization_code", "password")
//                .accessTokenValiditySeconds(3600)
//                .scopes("all");
    }


        /**
         *  配置：安全检查流程,用来配置令牌端点（Token Endpoint）的安全与权限访问
         *  默认过滤器：BasicAuthenticationFilter
         *  1、oauth_client_details表中clientSecret字段加密【ClientDetails属性secret】
         *  2、CheckEndpoint类的接口 oauth/check_token 无需经过过滤器过滤，默认值：denyAll()
         * 对以下的几个端点进行权限配置：
         * /oauth/authorize：授权端点
         * /oauth/token：令牌端点
         * /oauth/confirm_access：用户确认授权提交端点
         * /oauth/error：授权服务错误信息端点
         * /oauth/check_token：用于资源服务访问的令牌解析端点
         * /oauth/token_key：提供公有密匙的端点，如果使用JWT令牌的话
         **/
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //允许客户端访问 OAuth2 授权接口，否则请求 token 会返回 401
        security.allowFormAuthenticationForClients();
        //允许已授权用户访问 checkToken 接口
        security.checkTokenAccess("isAuthenticated()");
        //获取 token 接口
        security.tokenKeyAccess("isAuthenticated()");
    }
}