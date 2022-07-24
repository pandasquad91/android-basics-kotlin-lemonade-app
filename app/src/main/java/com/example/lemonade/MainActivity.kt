/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lemonade

import android.os.Build.VERSION_CODES.P
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    /**
     * DO NOT ALTER ANY VARIABLE OR VALUE NAMES OR THEIR INITIAL VALUES.
     *
     * Anything labeled var instead of val is expected to be changed in the functions but DO NOT
     * alter their initial values declared here, this could cause the app to not function properly.
     */
    private val LEMONADE_STATE = "LEMONADE_STATE"
    private val LEMON_SIZE = "LEMON_SIZE"
    private val SQUEEZE_COUNT = "SQUEEZE_COUNT"
    // SELECT represents the "pick lemon" state
    private val SELECT = "select"
    // SQUEEZE represents the "squeeze lemon" state
    private val SQUEEZE = "squeeze"
    // DRINK represents the "drink lemonade" state
    private val DRINK = "drink"
    // RESTART represents the state where the lemonade has been drunk and the glass is empty
    private val RESTART = "restart"
    // Default the state to select
    private var lemonadeState = "select"
    // Default lemonSize to -1
    private var lemonSize = -1
    // Default the squeezeCount to -1
    private var squeezeCount = -1

    private var lemonTree = LemonTree()
    private var lemonImage: ImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // === DO NOT ALTER THE CODE IN THE FOLLOWING IF STATEMENT ===
        if (savedInstanceState != null) {
            lemonadeState = savedInstanceState.getString(LEMONADE_STATE, "select")
            lemonSize = savedInstanceState.getInt(LEMON_SIZE, -1)
            squeezeCount = savedInstanceState.getInt(SQUEEZE_COUNT, -1)
        }
        // === END IF STATEMENT ===

        lemonImage = findViewById(R.id.image_lemon_state)
        setViewElements()
        lemonImage!!.setOnClickListener { clickLemonImage() }
        lemonImage!!.setOnLongClickListener { showSnackbar() }
    }

    /**
     * === DO NOT ALTER THIS METHOD ===
     *
     * This method saves the state of the app if it is put in the background.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(LEMONADE_STATE, lemonadeState)
        outState.putInt(LEMON_SIZE, lemonSize)
        outState.putInt(SQUEEZE_COUNT, squeezeCount)
        super.onSaveInstanceState(outState)
    }

    /**
     * Clicking will elicit a different response depending on the state.
     * This method determines the state and proceeds with the correct action.
     */
    private fun clickLemonImage() {
        // Perform an action based on current state
        lemonadeState = when (lemonadeState) {
            SELECT -> selectAction()
            SQUEEZE -> squeezeAction()
            DRINK -> drinkAction()
            RESTART -> restartAction()
            else -> unknownAction()
        }

        Log.d(TAG, "Current state: ${lemonadeState}")

        // Update our view
        setViewElements()
    }

    /**
     * The select action is performed when selecting a lemon from the tree
     *
     * Pick an unsqueezed lemon from the tree and move to the squeeze state
     */
    private fun selectAction(): String {
        // Pick a new lemon; should be a random size
        lemonSize = lemonTree.pick()

        // It is unsqueezed when first picked
        squeezeCount = 0

        // Move us to the squeeze state
        return SQUEEZE
    }

    /**
     * The squeeze action is performed when we click a picked lemon to squeeze it
     *
     * Increment a squeeze out of this lemon
     * If the lemon still has juice, we stay in the squeeze state
     * If the lemon is out of juice, we move on to the drink state
     */
    private fun squeezeAction(): String {
        // Each squeeze reduces the lemon's size by 1
        squeezeCount++
        lemonSize--

        Log.d(TAG, "Squeeze! There are ${lemonSize} squeezes left to juice this lemon!")

        // When the lemon's size has reached 0, it has been fully squeezed
        // So, move us to the drink state
        if (lemonSize == 0) { return DRINK }

        // Otherwise, the lemon is not fully squeezed, so stay here
        return SQUEEZE
    }

    /**
     * The drink action is performed when we click the freshly squeezed lemonade
     *
     * Reset our current lemon and move to the restart state
     */
    private fun drinkAction(): String {
        // Reset our lemon by giving it a nonsense size
        lemonSize = -1
        return RESTART
    }

    /**
     * All gone! The glass is empty. Would you like to play again?
     *
     * Restart the action by moving back to the select state
     */
    private fun restartAction(): String {
        return SELECT
    }

    /**
     * Error / debug action - call me if you get lost
     *
     * Logs the error and returns to the beginning (select state)
     */
    private fun unknownAction(): String {
        Log.e(TAG, "Got into an unknown state somehow!")
        return SELECT
    }

    /**
     * Set up the view elements according to the state.
     */
    private fun setViewElements() {
        val textAction: TextView = findViewById(R.id.text_action)

        val lemonText = when (lemonadeState) {
            SELECT -> selectView()
            SQUEEZE -> squeezeView()
            DRINK -> drinkView()
            RESTART -> restartView()
            else -> unknownView()
        }

        Log.d(TAG, "For current state ${lemonadeState}, we are displaying: \n${lemonText}")

        textAction.setText(lemonText)
    }

    /**
     * Sets the image for the select state and returns the text for this view
     */
    private fun selectView(): String {
        lemonImage!!.setImageResource(R.drawable.lemon_tree)
        return getString(R.string.lemon_select)
    }

    /**
     * Sets the image for the squeeze state and returns the text for this view
     */
    private fun squeezeView(): String {
        lemonImage!!.setImageResource(R.drawable.lemon_squeeze)
        return getString(R.string.lemon_squeeze)
    }

    /**
     * Sets the image for the drink state and returns the text for this view
     */
    private fun drinkView(): String {
        lemonImage!!.setImageResource(R.drawable.lemon_drink)
        return getString(R.string.lemon_drink)
    }

    /**
     * Sets the image for the restart state and returns the text for this view
     */
    private fun restartView(): String {
        lemonImage!!.setImageResource(R.drawable.lemon_restart)
        return getString(R.string.lemon_empty_glass)
    }

    /**
     * Returns text for an unknown state
     */
    private fun unknownView(): String {
        val errorViewText = "An unknown state was entered!"
        Log.e(TAG, errorViewText)
        return errorViewText
    }

    /**
     * === DO NOT ALTER THIS METHOD ===
     *
     * Long clicking the lemon image will show how many times the lemon has been squeezed.
     */
    private fun showSnackbar(): Boolean {
        if (lemonadeState != SQUEEZE) {
            return false
        }
        val squeezeText = getString(R.string.squeeze_count, squeezeCount)
        Snackbar.make(
            findViewById(R.id.constraint_Layout),
            squeezeText,
            Snackbar.LENGTH_SHORT
        ).show()
        return true
    }
}

/**
 * A Lemon tree class with a method to "pick" a lemon. The "size" of the lemon is randomized
 * and determines how many times a lemon needs to be squeezed before you get lemonade.
 */
class LemonTree {
    fun pick(): Int {
        return (2..4).random()
    }
}
