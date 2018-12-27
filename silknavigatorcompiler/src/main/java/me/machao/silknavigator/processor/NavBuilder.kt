package me.machao.silknavigator.processor

import com.squareup.kotlinpoet.*
import me.machao.silknavigator.anno.Route
import javax.lang.model.element.Element
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import me.machao.silknavigator.anno.RouteBinding

/**
 * Date  2018/12/5
 * @author charliema
 */
class NavBuilder {
    private var map: MutableMap<String, Element> = mutableMapOf()

    fun addRoute(e: Element) {
        val routeValue = e.getAnnotation(Route::class.java).value
        map[routeValue] = e
    }

    private val mapType =
        ClassName("kotlin.collections", "MutableMap")
            .parameterizedBy(
                String::class.asClassName(),
                RouteBinding::class.asClassName())

    fun brewKotlin(className:String): FileSpec {
        val constructorBuilder = FunSpec.constructorBuilder()
            .addParameter("map", mapType)

        map.forEach {

            constructorBuilder.addStatement(
                "map[%S] = %T(%S, %T::class.java)",
                it.key,
                RouteBinding::class,
                it.key,
                it.value.asType()
            )
        }

        val classBuilder = TypeSpec.classBuilder(className)
            .primaryConstructor(constructorBuilder.build())

        return FileSpec.builder("me.machao.silknavigator", className )
            .addType(classBuilder.build())
            .build()
    }

}

