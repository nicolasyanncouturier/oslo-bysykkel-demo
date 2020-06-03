package no.nicolasyanncouturier.obd

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class WebConfiguration {

    @Profile("!dogfood")
    @Bean
    fun directStatusViewDataProvider(osloBysykkelService: OsloBysykkelService): StatusViewDataProvider {
        return DirectStatusViewDataProvider(osloBysykkelService);
    }

    @Profile("dogfood")
    @Bean
    fun apiStatusViewDataProvider(): StatusViewDataProvider {
        return ApiStatusViewDataProvider()
    }

}