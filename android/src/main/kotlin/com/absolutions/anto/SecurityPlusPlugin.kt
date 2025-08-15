package com.absolutions.anto

import android.content.Context
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import androidx.core.content.ContextCompat
import android.Manifest
import securityChecker
import java.io.BufferedReader
import java.io.InputStreamReader

import java.io.File

/** SecurityPlusPlugin */
class SecurityPlusPlugin : FlutterPlugin, MethodCallHandler {
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    private lateinit var securityChecker: SecurityChecker

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext
        securityChecker = SecurityChecker(context)
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "anto")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "ir" -> result.success(securityChecker.isRooted())
            "ie" -> result.success(securityChecker.isEmulator())
            "id" -> result.success(securityChecker.developmentModeCheck(context))
            else -> result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
