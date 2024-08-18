package com.example.duocapp

import android.content.Context
import android.content.SharedPreferences

class UserManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveUser(username: String, password: String) {
        val editor = sharedPreferences.edit()
        val users = getUsers().toMutableList()
        users.add("$username:$password")
        editor.putString("users", users.joinToString(","))
        editor.apply()
    }

    fun getUsers(): List<String> {
        val usersString = sharedPreferences.getString("users", "") ?: ""
        return if (usersString.isNotEmpty()) {
            usersString.split(",")
        } else {
            emptyList()
        }
    }
    fun isValidUser(username: String, password: String): Boolean {
        return getUsers().any { it == "$username:$password" }
    }

}
