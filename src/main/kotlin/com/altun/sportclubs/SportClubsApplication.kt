package com.altun.sportclubs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class SportClubsApplication

fun main(args: Array<String>) {
    runApplication<SportClubsApplication>(*args)
}
