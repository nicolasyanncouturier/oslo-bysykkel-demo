package no.nicolasyanncouturier.obd

import no.nicolasyanncouturier.gbfs.OfflineSource
import no.nicolasyanncouturier.gbfs.OnlineSource
import no.nicolasyanncouturier.gbfs.Source
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class ObdConfiguration {

    @Profile("!offline")
    @Bean
    fun onlineSource(): Source {
        return OnlineSource("https://gbfs.urbansharing.com/oslobysykkel.no/gbfs.json", DefaultRegion.locale, "")
    }

    @Profile("offline")
    @Bean
    fun offlineSource(): Source {
        return OfflineSource()
    }

}