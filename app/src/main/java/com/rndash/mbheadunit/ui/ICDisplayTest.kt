package com.rndash.mbheadunit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rndash.mbheadunit.FullscreenActivity
import com.rndash.mbheadunit.R
import com.rndash.mbheadunit.canData.CanBusB
import com.rndash.mbheadunit.canData.canB.kombiDisplay.ICDefines
import com.rndash.mbheadunit.canData.canB.kombiDisplay.ICDisplay
import java.util.*

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class ICDisplayTest : Fragment() {
    var isInPage = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.ic_test, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val input_header = view.findViewById<EditText>(R.id.input_header)
        val input_body = view.findViewById<EditText>(R.id.input_body)
        val btn_header = view.findViewById<Button>(R.id.header_send)
        val btn_body = view.findViewById<Button>(R.id.send_body)

        btn_body.setOnClickListener {
            ICDisplay().sendHeader(ICDefines.Page.AUDIO, ICDefines.TextFormat.CENTER_JUSTIFICATION, input_body.text.toString())
        }

        btn_header.setOnClickListener {
            ICDisplay().initPage(ICDefines.Page.AUDIO, ICDefines.TextFormat.RIGHT_JUSTIFICATION, input_header.text.toString(), ICDefines.AudioSymbol.UP_ARROW, ICDefines.AudioSymbol.DOWN_ARROW)
        }

        val test_text : TextView = view.findViewById(R.id.test_frame)
        Timer().schedule(object: TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    test_text.text = ICDisplay.log
                }
            }
        },0, 100)

    }

    override fun onPause() {
        super.onPause()
        isInPage = false
    }

    override fun onResume() {
        super.onResume()
        isInPage = true
    }
}