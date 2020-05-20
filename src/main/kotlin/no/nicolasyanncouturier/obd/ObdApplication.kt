package no.nicolasyanncouturier.obd

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ObdApplication

fun main(args: Array<String>) {
    runApplication<ObdApplication>(*args)
}
