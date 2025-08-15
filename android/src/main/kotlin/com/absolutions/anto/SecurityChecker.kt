package com.absolutions.anto

import android.content.Context
import android.os.Build
import com.scottyab.rootbeer.RootBeer
import android.os.Debug
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class SecurityChecker(private val context: Context) {

    fun isRooted(): Boolean {
        val rootBeer = RootBeer(context)
        val isRooted = rootBeer.isRooted
        return isRooted || checkFridaLibraries() || checkFridaProcesses() || checkSuspiciousFiles()
    }

    private fun isDebuggerAttached(): Boolean {
        return Debug.isDebuggerConnected()
    }

    private fun checkFridaProcesses(): Boolean {
        val suspiciousProcesses = arrayOf(
                "frida", "frida-server", "frida-helper",
                "frida-agent", "magisk", "magiskd"
        )

        return try {
            val process = Runtime.getRuntime().exec("ps")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                for (processName in suspiciousProcesses) {
                    if (line?.lowercase()?.contains(processName) == true) {
                        return true
                    }
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    private fun checkSuspiciousFiles(): Boolean {
        val suspiciousFiles = arrayOf(
                "/system/app/Superuser.apk",
                "/system/etc/init.d/99SuperSUDaemon",
                "/dev/com.koushikdutta.superuser.daemon/",
                "/system/xbin/daemonsu",
                "/sbin/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su",
                "/su/bin/su"
        )

        for (path in suspiciousFiles) {
            if (File(path).exists()) {
                return true
            }
        }
        return false
    }

    private fun checkSuPaths(): Boolean {
        val paths = System.getenv("PATH")?.split(":") ?: return false
        for (path in paths) {
            if (File(path + "/su").exists()) {
                return true
            }
        }
        return false
    }

    private fun checkFridaLibraries(): Boolean {
        val libraries = arrayOf(
                "frida-agent",
                "frida-gadget",
                "frida"
        )

        try {
            val maps = File("/proc/self/maps").readLines()
            for (line in maps) {
                for (lib in libraries) {
                    if (line.contains(lib)) {
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            // Ignore exceptions
        }
        return false
    }

    private fun detectHooks(): Boolean {
        return try {
            val runtime = Runtime.getRuntime()
            val field = runtime.javaClass.getDeclaredField("nativeLoad")
            field.isAccessible = true
            false
        } catch (e: Exception) {
            true
        }
    }

    fun developmentModeCheck(context: Context): Boolean {
        return if (Integer.valueOf(Build.VERSION.SDK_INT) == 16) {
            android.provider.Settings.Secure.getInt(
                    context.contentResolver,
                    android.provider.Settings.Secure.DEVELOPMENT_SETTINGS_ENABLED, 0
            ) != 0
        } else if (Integer.valueOf(Build.VERSION.SDK_INT) >= 17) {
            android.provider.Settings.Secure.getInt(
                    context.contentResolver,
                    android.provider.Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
            ) != 0
        } else false
    }


    fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.MODEL.startsWith("sdk_")
                || Build.DEVICE.startsWith("emulator")) || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith(
                "generic"
        ) || "google_sdk" == Build.PRODUCT
    }

}