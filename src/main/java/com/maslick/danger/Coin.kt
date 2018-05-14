package com.maslick.danger

import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import sun.rmi.runtime.Log

interface Repository {
    fun giveHello(): String
}

class MyRepository() : Repository {
    override fun giveHello() = "Hello Koin"
}

class MyPresenter(val repository : Repository){
    fun sayHello() = repository.giveHello()
}


val myModule: Module = applicationContext {
    factory { MyPresenter(get()) }
    bean { MyRepository() as Repository }
}


class MyApplication : KoinComponent {
    // Inject MyPresenter
    val presenter : MyPresenter by inject()

    init {
        // Let's use our presenter
        println("presenter : ${presenter.sayHello()}")
    }
}


fun main(args: Array<String>) {
    startKoin(listOf(myModule))
    MyApplication()
}