package edu.ynu.se.xiecheng.achitectureclass.common.system;


import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {
    // 创建Docket存入容器，Docket代表一个接口文档
    @Bean
    public Docket defaultApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                // 创建接口文档的具体信息
                .apiInfo(apiInfo())
                // 创建选择器，控制哪些接口被加入文档
                .select()
                // 指定@ApiOperation标注的接口被加入文档
                .apis(RequestHandlerSelectors.basePackage("edu.ynu.se.xiecheng.achitectureclass"))
                .paths(PathSelectors.any())
                .build();
    }

    // 创建接口文档的具体信息，会显示在接口文档页面中
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                // 文档标题
                .title("软件设计与体系结构课程项目")
                // 文档描述
                .description("分层架构模型设计")
                // 版本
                .version("1.0")
                // 联系人信息
                .contact(new Contact("Xie Cheng", "http://www.sei.ynu.edu.cn/info/1023/1166.htm", "xiecheng@ynu.edu.cn"))
                // 版权
                .license("XC")
                // 版权地址
                .licenseUrl("http://www.sei.ynu.edu.cn/info/1023/1166.htm")
                .build();
    }
}

