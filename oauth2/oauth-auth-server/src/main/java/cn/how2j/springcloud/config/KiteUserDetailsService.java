package cn.how2j.springcloud.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component(value = "kiteUserDetailsService")
//UserDetailsService的核心就是 loadUserByUsername方法，它要接收一个字符串参数，也就是传过来的用户名，返回一个 UserDetails对象
public class KiteUserDetailsService implements UserDetailsService {


    @Autowired
    private PasswordEncoder passwordEncoder;

//    private RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();
//
//    @Autowired
//    private TokenStore redisTokenStore;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("username is:" + username);
        // 查询数据库操作
        if(!username.equals("admin")){
            throw new UsernameNotFoundException("the user is not found");
        }else{
            // 用户角色也应在数据库中获取
            String role = "ROLE_ADMIN";
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(role));
            // 线上环境应该通过用户名查询数据库获取加密后的密码，encode对传入的字符串进行加密
            String password = passwordEncoder.encode("123456");
            // 返回默认的 User
            return new org.springframework.security.core.userdetails.User(username,password, authorities);

            // 返回自定义的 KiteUserDetails
//            User user = new User(username,password,authorities);
//            return user;
        }
    }
}
