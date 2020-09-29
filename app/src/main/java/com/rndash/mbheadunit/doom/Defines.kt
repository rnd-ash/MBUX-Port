package com.rndash.mbheadunit.doom

const val TICKRATE = 35
const val SCREENWIDTH = 320
const val SCREENHEIGHT = 200
const val SCREEN_MUL = 1

enum class GameState {
    Level,
    Intermission,
    Finale,
    DemoScreen
}

enum class Skill {
    Baby,
    Easy,
    Medium,
    Hard,
    Nightmare
}

enum class Card {
    BlueCard,
    YellowCard,
    RedCard,
    BlueSkull,
    YellowSkull,
    RedSkull,
    None
}

enum class AmmoType {
    Clip,
    Shell,
    Cell,
    Missile,
    Unlimited // Unlimited ammo for chainsaw and fists
}

enum class PowerType {
    Invulerability,
    Strength,
    Invisibility,
    IronFeet,
    AllMap,
    Infrared,
}

enum class PowerDuration(val seconds: Int) {
    INVULNTICS(30* TICKRATE),
    INVISTICS(60 * TICKRATE),
    INFRATICS(120 * TICKRATE),
    IRONTICS(60 * TICKRATE)
}

