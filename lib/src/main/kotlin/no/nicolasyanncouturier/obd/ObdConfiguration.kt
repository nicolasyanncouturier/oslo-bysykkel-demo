package no.nicolasyanncouturier.obd

import no.nicolasyanncouturier.gbfs.OfflineSource
import no.nicolasyanncouturier.gbfs.OnlineSource
import no.nicolasyanncouturier.gbfs.Source
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.util.concurrent.TimeUnit

@Configuration
class ObdConfiguration {

    @Profile("!offline")
    @Bean
    fun onlineSource(): Source {
        return OnlineSource("https://gbfs.urbansharing.com/oslobysykkel.no/gbfs.json",
            DefaultRegion.locale,
            "",
            OkHttpClient.Builder().callTimeout(1, TimeUnit.SECONDS).build())
    }

    @Profile("offline")
    @Bean
    fun offlineSource(): Source {
        return OfflineSource()
    }

}