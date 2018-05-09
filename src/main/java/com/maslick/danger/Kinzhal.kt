package com.maslick.danger

import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton


// Business logic
interface Api {
    fun getWeather(city: String): String
}

class RealApi(val httpClient: HttpClient): Api {
    override fun getWeather(city: String) = "sunny"
}

class FakeApi : Api {
    override fun getWeather(city: String) = "rainy"
}

class Context {
    init { println("initializing context") }
}

class SharedPrefs(val context: Context) {
    init { println("initializing shared prefs") }
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


// Dagger config (qualifiers, modules, components)
@Qualifier annotation class Real
@Qualifier annotation class Fake

@Module
class ContextModule(private val context: Context) {
    @Provides
    fun context() = context
}

@Module(includes = [ContextModule::class])
class HttpModule {
    @Singleton
    @Provides
    fun httpClient(cache: Cache, logging: LoggingInterceptor)= HttpClient(cache, logging)

    @Singleton
    @Provides
    fun cache(file: File) = Cache(file)

    @Singleton
    @Provides
    fun file(context: Context) = File(context)

    @Singleton
    @Provides
    fun logging() = LoggingInterceptor()
}

@Module(includes = [HttpModule::class])
class ApiModule {
    @Real
    @Provides
    fun api(httpClient: HttpClient): Api = RealApi(httpClient)
}

@Module
class FakeApiModule {
    @Fake
    @Provides
    fun api(): Api = FakeApi()
}

@Module(includes = [ContextModule::class])
class SharedPrefsModule {
    @Singleton
    @Provides
    fun prefs(context: Context) = SharedPrefs(context)
}

@Singleton
@Component(modules = [ApiModule::class, FakeApiModule::class, SharedPrefsModule::class])
interface ApiComponent {
    @Real fun api(): Api
    @Fake fun fakeApi(): Api
    fun prefs(): SharedPrefs
    fun inject(app: App)
}

// Application
fun main(args: Array<String>) {
    val component = DaggerApiComponent.builder()
            .contextModule(ContextModule(Context()))
            .build()

    val realApi = component.api()
    val stubApi = component.fakeApi()
    val prefs = component.prefs()

    println()

    val city1 = "Ljubljana"
    val weather1 = realApi.getWeather(city1)
    println("weather in $city1: $weather1")

    val city2 = "St. Pete"
    val weather2 = stubApi.getWeather(city2)
    println("weather in $city2: $weather2")

    // testing the singletons
    val realApi2 = component.api()
    val stubApi2 = component.fakeApi()
}

// Application (using inject)
class App {
    @Inject @field:Real lateinit var realApi: Api
    @Inject @field:Fake lateinit var fakeApi: Api

    init {
        DaggerApiComponent
                .builder()
                .contextModule(ContextModule(Context()))
                .build()
                .inject(this)
    }
}

object Epp {
    @JvmStatic
    fun main(args: Array<String>) {
        val app = App()
        val w1 = app.fakeApi.getWeather("St Pete")
        val w2 = app.realApi.getWeather("Lj")
        println("$w1, $w2")
    }
}