/**
 * 
 */
package com.db.awmd.challenge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import static springfox.documentation.builders.PathSelectors.regex;
/**
 * @author GunjanKumar
 *
 */
@EnableSwagger2
@Configuration
public class SwaggerConfig {
	@Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.db.awmd.challenge.web"))
                .paths(regex("/v1.*"))
                .build()
                .apiInfo(metaInfo());
    }

    @SuppressWarnings("deprecation")
	private ApiInfo metaInfo() {

        ApiInfo apiInfo =  new ApiInfoBuilder().title("Transfer API")
				.description("Fund Transfer API reference for developers")
				.termsOfServiceUrl("http://github.com")
				.contact("gunjandac@gmail.com").license("Gunjan License")
				.licenseUrl("gunjandac@gmail.com").version("1.0").build();

        return apiInfo;
    }
}
