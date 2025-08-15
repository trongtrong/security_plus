import 'package:flutter/services.dart';

/// A Flutter plugin to check for security alerts
class SecurityPlus {
  static const MethodChannel _channel = MethodChannel('anto');

  /// Android Root checks
  /// this gets the value from native channel to flutter to know if phone is rooted
  static Future<bool> get isRooted async {
    final bool isRooted = await _channel.invokeMethod('ir');
    return isRooted;
  }

  /// iOS JailBroken checks
  /// this gets the value from native channel to flutter to know if phone is jail broken
  static Future<bool> get isJailBroken async {
    final bool isJailBroken = await _channel.invokeMethod('isJailBroken');
    return isJailBroken;
  }

  /// Android Emulator checks
  /// this gets the value from native channel to flutter to know if app is running on an Emulator
  static Future<bool> get isEmulator async {
    final bool isEmulator = await _channel.invokeMethod('ie');
    return isEmulator;
  }

  /// Android on External Storage check
  /// this gets the value from native channel to flutter to know if app is running on an external storage
  static Future<bool> get isOnExternalStorage async {
    final bool isOnExternalStorage =
        await _channel.invokeMethod('isOnExternalStorage');
    return isOnExternalStorage;
  }

  /// Android Development mode check
  /// this gets the value from native channel to flutter to know if app is running in development environment
  static Future<bool> get isDevelopmentModeEnable async {
    final bool isDevelopmentModeEnable =
        await _channel.invokeMethod('id');
    return isDevelopmentModeEnable;
  }

  static Future<bool> get isMockLocationEnabled async {
    final bool isMockLocationEnabled =
        await _channel.invokeMethod('isMockLocationEnabled');
    return isMockLocationEnabled;
  }
}
