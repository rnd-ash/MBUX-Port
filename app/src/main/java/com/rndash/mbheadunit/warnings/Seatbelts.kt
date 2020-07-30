package com.rndash.mbheadunit.warnings

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.rndash.mbheadunit.R

class Seatbelts(a: Activity) : MBUXDialog(
    R.drawable.belt,
    R.raw.belt_phase1,
    true,
    WarnLevel.WARNING,
    "Fasten seat belt",
    a
)