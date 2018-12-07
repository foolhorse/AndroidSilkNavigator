package me.machao.silknavigator

import android.app.Activity
import android.content.Intent
import me.machao.silknavigator.anno.RouteBinding
import java.lang.reflect.Constructor

/**
 * Date  2018/12/6
 * @author charliema
 */
class Navigator {

    private object SingletonHolder {
        val instance = Navigator()
    }

    companion object {
        fun getInstance() = SingletonHolder.instance
    }

    private val routeMap = mutableMapOf<String, RouteBinding>()

    private var interceptors = mutableSetOf<(source: Activity, destination: String) -> Boolean>()
//    private var interceptors = mutableSetOf<Interceptor>()

    init {
        val routeSetClassName = "me.machao.silknavigator" + "." + "RouteSet"
        val clz = Class.forName(routeSetClassName)
//        val constructor = clz.getConstructor(routeMap::class.java)
        val constructors = clz.constructors
        val constructor: Constructor<*>
        if (constructors[0] != null) {
            constructor = constructors[0]
            constructor.newInstance(routeMap)
        }
    }

    fun from(source: Activity): DesBuilder {
        return DesBuilder(source)
    }

    fun go(desBuilder: DesBuilder) {
        if (interceptors.any { it.invoke(desBuilder.source, desBuilder.des) }) {
            return
        }
        val routeBinding = routeMap[desBuilder.des] ?: return
        val intent = Intent(desBuilder.source, routeBinding.clz)
        desBuilder.source.startActivity(intent)
    }

    fun addInterceptor(interceptor: Interceptor) {
        addInterceptor { s, d -> interceptor.onIntercept(s, d) }
    }

    fun addInterceptor(callback: (source: Activity, destination: String) -> Boolean) {
        interceptors.add(callback)
    }

    fun removeInterceptor(interceptor: Interceptor) {
        removeInterceptor { s, d -> interceptor.onIntercept(s, d) }
    }

    fun removeInterceptor(callback: (source: Activity, destination: String) -> Boolean) {
        interceptors.remove(callback)
    }

    fun removeAllInterceptor() {
        interceptors.clear()
    }

}