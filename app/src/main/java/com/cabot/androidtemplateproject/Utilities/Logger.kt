package com.cabot.androidtemplateproject.Utilities

import android.util.Log
import com.cabot.androidtemplateproject.BuildConfig

/**
 * Created by rahul on 10/10/17.
 *
 * Android Log wrapper class that can use to logging message on DEBUG build versions
**/

object Logger {

    private var writeLogs = BuildConfig.DEBUG

    private fun showLogs(tag: String): Boolean {
        return writeLogs && !tag.isEmpty()
    }

    /**
     * Send a VERBOSE log message.
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    @JvmStatic
    fun v(tag: String, msg: String) {
        if (showLogs(tag)) {
            Log.v(tag, msg)
        }
    }

    /**
     * Send a DEBUG log message.
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    @JvmStatic
    fun d(tag: String, msg: String) {
        if (showLogs(tag)) {
            Log.d(tag, msg)
        }
    }

    /**
     * Send a INFO log message.
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    @JvmStatic
    fun i(tag: String, msg: String) {
        if (showLogs(tag)) {
            Log.i(tag, msg)
        }
    }

    /**
     * Send a WARN log message.
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    @JvmStatic
    fun w(tag: String, msg: String) {
        if (showLogs(tag)) {
            Log.w(tag, msg)
        }
    }

    /**
     * Send a ERROR log message.
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    @JvmStatic
    fun e(tag: String, msg: String) {
        if (showLogs(tag)) {
            Log.e(tag, msg)
        }
    }
}