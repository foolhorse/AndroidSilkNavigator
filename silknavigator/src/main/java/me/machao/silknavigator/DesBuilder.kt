package me.machao.silknavigator

import android.app.Activity

/**
 * Date  2018/12/6
 * @author charliema
 */
class DesBuilder() {

    lateinit var des: String
    lateinit var source: Activity

    constructor(a: Activity): this() {
        source = a
    }

    fun to(des: String): DesBuilder {
        this.des = des
        return this
    }

    fun go() {
        Navigator.getInstance().go(this)
    }
}