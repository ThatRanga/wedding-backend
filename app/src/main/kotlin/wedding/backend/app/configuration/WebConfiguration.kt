package wedding.backend.app.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InjectionPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope

@Configuration
class WebConfiguration {
    @Bean
    @Scope("prototype")
    fun produceLogger(injectionPoint: InjectionPoint): Logger {
        val classBeingWired = injectionPoint.member.declaringClass
        return LoggerFactory.getLogger(classBeingWired)
    }

    @Bean
    fun objectMapper(): ObjectMapper = jacksonObjectMapper()
}