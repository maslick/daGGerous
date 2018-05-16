package com.maslick.danger

import org.koin.Koin
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import org.koin.log.EmptyLogger
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject

interface Api {
    fun getWeather(city: String): String
}

class RealApi(val client: HttpClient) : Api {
    override fun getWeather(city: String) = "sunny"
}

class FakeApi : Api {
    override fun getWeather(city: String) = "rainy"
}

class Context {
    init { println("initializing context") }
}


class SharedPrefs(val context: Context) {
    init { println("initializing shared sharedPrefs") }
    val map = mutableMapOf<String, String>()
}

class File(val context: Context) {
    init { println("initializing file system") }
}

class Cache(val file: File) {
    init { println("initializing cache") }
}

class LoggingInterceptor {
    init { println("initializing logging interceptor") }
}

class HttpClient(val cache: Cache, val logging: LoggingInterceptor) {
    init { println("initializing http client") }
}


val contextModule: Module = applicationContext {
    bean { Context() }
}

val httpModule = applicationContext {
    bean { HttpClient(get(), get()) }
    bean { Cache(get()) }
    bean { File(get()) }
    bean { LoggingInterceptor() }
}

val apiModule: Module = applicationContext {
    bean("real") { RealApi(get()) as Api }
    bean("fake") { FakeApi() as Api }
}

val sharedPrefsModule: Module = applicationContext {
    bean { SharedPrefs(get()) }
}

class MyApplication : KoinComponent {
    private val realApi : Api by inject("real")
    private val fakeApi : Api by inject("fake")

    init {
        println("weather in Ljubjana: ${realApi.getWeather("Ljubljana")}")
        println("weather in Spb: ${fakeApi.getWeather("saint petersburg")}")
    }
}


fun main(args: Array<String>) {
    Koin.logger = EmptyLogger()
    startKoin(listOf(
                contextModule,
                httpModule,
                apiModule,
                sharedPrefsModule
    ))
}