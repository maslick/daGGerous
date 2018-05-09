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


// Dagger config (qualifiers, modules, components)
@Qualifier annotation class Real
@Qualifier annotation class Fake

@Module
class ContextModule(private val context: Context) {
    @Singleton
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
    fun inject(app: Activity1)
    fun inject(app: Activity2)
}

// Application
fun main(args: Array<String>) {
    val component = DaggerApiComponent.builder()
            .contextModule(ContextModule(Context()))
            .build()

    val realApi = component.api()
    val fakeApi = component.fakeApi()
    val prefs = component.prefs()

    println()

    val city1 = "Ljubljana"
    val weather1 = realApi.getWeather(city1)
    println("weather in $city1: $weather1")

    val city2 = "St. Pete"
    val weather2 = fakeApi.getWeather(city2)
    println("weather in $city2: $weather2")

    // testing the singletons
    component.api().getWeather("dummy")
    component.fakeApi().getWeather("dummy")

    // testing shared prefs
    prefs.map["hello"] = "world"
    println("hello: ${prefs.map["hello"]}")
}

// Application (using inject)
object Application {
    val component = DaggerApiComponent.builder().contextModule(ContextModule(Context())).build()!!
}

class Activity1 {
    @Inject @field:Real lateinit var realApi: Api
    @Inject @field:Fake lateinit var fakeApi: Api
    @Inject lateinit var sharedPrefs: SharedPrefs
    init { Application.component.inject(this) }
}

class Activity2 {
    @Inject lateinit var sharedPrefs: SharedPrefs
    init { Application.component.inject(this) }
}

object Epp {
    @JvmStatic
    fun main(args: Array<String>) {
        val app = Activity1()
        val w1 = app.fakeApi.getWeather("St Pete")
        val w2 = app.realApi.getWeather("Lj")
        println("$w1, $w2")
        app.sharedPrefs.map["hello"] = "world"
        println("hello: ${app.sharedPrefs.map["hello"]}")

        val app2 = Activity2()
        println("hello: ${app2.sharedPrefs.map["hello"]}")
    }
}