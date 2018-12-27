package me.machao.silknavigator

import android.app.Activity
import android.content.Intent
import android.util.Log
import me.machao.silknavigator.anno.RouteBinding
import java.lang.reflect.Constructor

/**
 * Date  2018/12/6
 * @author charliema
 */
class Navigator(modulePackageNames: List<String>) {

    companion object {
        const val PACKAGE_NAME = "me.machao.silknavigator"

        @Volatile
        private var instance: Navigator? = null

        fun init(modulePackageNames: List<String>): Navigator {
            if (instance == null) {
                synchronized(Navigator::class) {
                    if (instance == null) {
                        instance = Navigator(modulePackageNames)
                    }
                }
            }
            return instance!!
        }

        fun getInstance(): Navigator {
            if (instance == null) {
                throw IllegalStateException("must call init first")
            } else {
                return instance!!
            }
        }
    }

    private val modulePackageNames = mutableListOf<String>()
    private val routeMap = mutableMapOf<String, RouteBinding>()

    private var interceptors = mutableSetOf<(source: Activity, destination: String) -> Boolean>()
//    private var interceptors = mutableSetOf<Interceptor>()

    init {
        modulePackageNames.map {
            "$PACKAGE_NAME.RouteSet_$it"
        }.forEach {
            val clz = Class.forName(it)
//        val constructor = clz.getConstructor(routeMap::class.java)
            val constructors = clz.constructors
            val constructor: Constructor<*>
            if (constructors[0] != null) {
                constructor = constructors[0]
                constructor.newInstance(routeMap)
            }
        }
    }

    fun addModules(modulePackageNames: List<String>) {
        this.modulePackageNames.addAll(modulePackageNames)
    }


    fun from(source: Activity): DesBuilder {
        return DesBuilder(source)
    }

    fun go(desBuilder: DesBuilder) {
        if (interceptors.any { it.invoke(desBuilder.source, desBuilder.des) }) {
            return
        }
        val routeBinding = routeMap[desBuilder.des]
        if (routeBinding == null) {
            Log.e("me.machao.silknavigator", "Can not find route binding for:" + desBuilder.des)
            return
        }
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