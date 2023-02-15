package uz.kmax.levelup.memory

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Build
import java.util.*

class SharedHelper(var context: Context) {

    private var preferences: SharedPreferences

    private lateinit var editor: SharedPreferences.Editor

    init {
        preferences = context.getSharedPreferences("PICS_GAME", MODE_PRIVATE)
    }

    fun setLeveCount(levelCount : Int){
        editor = preferences.edit()
        editor.putInt("COUNT_LEVEL",levelCount)
        editor.apply()
    }

    fun getLevelCount() = preferences.getInt("COUNT_LEVEL",1)

    fun setLeveViewCount(levelCount : Int){
        editor = preferences.edit()
        editor.putInt("COUNT_LEVEL_VIEW",levelCount)
        editor.apply()
    }

    fun getLevelViewCount() = preferences.getInt("COUNT_LEVEL_VIEW",0)
}