package org.example

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

class forRef(
    private val first: Int
) {
    private val Pvalue: Int = 2

    private var Pvariable: Int = 3

    fun plus(second: Int): Int {
        return first + second
    }

    private fun secretMake(): Int {
        return Pvalue + Pvariable
    }
}

fun main(){

    val kClass: KClass<forRef> = forRef::class
    val primaryConst = kClass.primaryConstructor
    val inst2 = primaryConst?.call(0)
    val name = "Pvariable"
    val a = kClass.memberProperties.find {it.name == name}
    a?.isAccessible = true
    println(a?.get(inst2!!))
    if(a is KMutableProperty<*>){
        a.setter.call(inst2,66)
    }
    println(a?.get(inst2!!))


}