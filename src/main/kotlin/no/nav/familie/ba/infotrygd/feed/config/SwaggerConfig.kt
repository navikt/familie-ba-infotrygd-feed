package no.nav.familie.ba.infotrygd.feed.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.ApiKey
import springfox.documentation.service.AuthorizationScope
import springfox.documentation.service.SecurityReference
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc

@Configuration
@EnableSwagger2WebMvc
class SwaggerConfig {


    private val basePackage = "no.nav.familie.ba.infotrygd.feed"

    private val bearer = "Bearer"
    /**
     * Builder and primary interface of swagger-spring framework.
     */
    @Bean
    fun customImplementation(): Docket {

        return Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContext())
                .apiInfo(apiInfo())
    }

    private fun securitySchemes(): List<ApiKey> {
        return listOf(ApiKey(bearer, "Authorization", "header"))
    }

    private fun securityContext(): List<SecurityContext> {
        return listOf(SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/api.*"))
                .build())
    }

    private fun defaultAuth(): List<SecurityReference> {
        val authorizationScope = AuthorizationScope("global", "accessEverything")
        val authorizationScopes = arrayOfNulls<AuthorizationScope>(1)
        authorizationScopes[0] = authorizationScope
        return listOf(SecurityReference(bearer, authorizationScopes))
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfoBuilder().build()
    }
}
