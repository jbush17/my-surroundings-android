package com.example.jbush.mysurroundings

import android.view.View
import android.view.ViewGroup

val ViewGroup.views : List<View>
    get () = (0..this.childCount-1).map { getChildAt(it) }

val ViewGroup.viewsExceptFirst : List<View>
    get () {
        if (this.childCount > 0) {
            return (1..this.childCount-1).map { getChildAt(it) }
        }
        return listOf()
    }
