# MBUX-Port

## Project
A hobby project of mine to get MBUX working on my W203 Mercedes, using an android tablet made by XTRONS to run the UI, with an Arduino linked to both CAN C + B as a decoder and encoder box, encoding can frames as Serial and sending them to the android tablet over USB Serial

## What cars are supported?
I've only tested on my W203 C200 CDI, but in theory, any W203/211/219/209 should work as their underlying CANBUS architecture is the same

## Getting started
Look at the first video in my series [here](https://youtu.be/4CgKVs9Fzt8)

# Directory overview
* UNO_CODE - Code for Arduino uno. See README within the folder for more details
* app - Code for android application
