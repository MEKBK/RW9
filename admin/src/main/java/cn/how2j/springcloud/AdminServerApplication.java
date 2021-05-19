package cn.how2j.springcloud;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;


//@SpringBootApplication 来标注一个主程序类
//说明这是一个Spring Boot应用
@SpringBootApplication
@EnableAdminServer
@EnableEurekaClient

public class AdminServerApplication {
    public static void main( String[] args )
    {
        //启动整个项目
        SpringApplication.run(AdminServerApplication.class, args);
    }
}
