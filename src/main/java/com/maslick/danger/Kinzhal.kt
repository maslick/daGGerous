package com.maslick.danger

import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier
import javax.inject.Singleton


interface Api {
    fun getWeather(city: String): String
}
class MyApi1(val httpClient: HttpClient): Api {
    override fun getWeather(city: String) = "sunny"
}

class MyApi2 : Api {
    override fun getWeather(city: String) = "rainy"
}

class Cache(val file: File)
class File(val context: Context)
class Context
class LoggingInterceptor
class HttpClient(val cache: Cache, val logging: LoggingInterceptor)


@Module
class ContextModule(private val context: Context) {
    @Provides
    fun context() = context
}

@Module(includes = [ContextModule::class])
class HttpModule {
    @Provides
    fun httpClient(cache: Cache, logging: LoggingInterceptor)= HttpClient(cache, logging)

    @Provides
    fun cache(file: File) = Cache(file)

    @Provides
    fun file(context: Context) = File(context)

    @Provides
    fun logging() = LoggingInterceptor()
}

@Module(includes = [HttpModule::class])
class ApiModule {
    @Real
    @Provides
    fun api(httpClient: HttpClient): Api = MyApi1(httpClient)
}

@Module
class FakeApiModule {
    @Stub
    @Provides
    fun api(): Api = MyApi2()
}

@Singleton
@Component(modules = [ApiModule::class, FakeApiModule::class])
interface ApiComponent {
    @Real fun api(): Api
    @Stub fun fakeApi(): Api
}

@Qualifier annotation class Real
@Qualifier annotation class Stub


object App {
    @JvmStatic
    fun main(args: Array<String>) {
        val component = DaggerApiComponent.builder()
                .contextModule(ContextModule(Context()))
                .build()

        val city1 = "Ljubljana"
        val weather1 = component.api().getWeather(city1)
        println("weather in $city1: $weather1")

        val city2 = "St. Pete"
        val weather2 = component.fakeApi().getWeather(city2)
        println("weather in $city2: $weather2")
    }
}