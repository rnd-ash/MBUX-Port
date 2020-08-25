package com.rndash.mbheadunit.car

internal class KeyState {
    var lastPressTime: Long = System.currentTimeMillis()
    var isPressed: Boolean = false

    fun press(pg: KeyManager.PAGE) {
        if (!isPressed) { // First press register
            lastPressTime = System.currentTimeMillis()
        }
        isPressed = true // Key is held down
    }

    fun release(pg: KeyManager.PAGE) {
        if (isPressed) { // Key has just been released
            listener?.let {// Do we have a registered listener?
                // Check if long press or short press
                if (System.currentTimeMillis() - lastPressTime >= KeyManager.LONG_PRESS_THRESHOLD_MS) {
                    it.onLongPress(pg)
                } else {
                    it.onShortPress(pg)
                }
            }
        }
        isPressed = false
    }

    private var listener: KeyManager.KeyListener? = null
    // Register a key listener for the key
    fun registerListener(listener: KeyManager.KeyListener) {
        this.listener = listener
    }
}