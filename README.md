# MBUX-Port

## Project
A hobby project of mine to get MBUX working on my W203 Mercedes, using an android tablet made by XTRONS to run the UI, 
with an Arduino linked to both CAN C + B as a decoder and encoder box, encoding can frames as Serial and sending them to the 
android tablet over USB Serial. It can even make the car run [DOOM](https://www.youtube.com/watch?v=5mmiPT2avrY&list=PLxrw-4Vt7xtstJgl7B1ayPXBFBRulu41J&index=4)

## [Click here for youtube series](https://www.youtube.com/playlist?list=PLxrw-4Vt7xtstJgl7B1ayPXBFBRulu41J)
This series goes through the entire installation progress, as well as docmenting some stages you can go through to add features or change features yourself!

### What works?
Currently, I have been able to process and de-code every can frame on the cars bus, allowing for
reading and writing any possible valid frame to the cars ECU network.

UI Wise, it is still in early development, but allows for showing some interesting metrics
that are not avaliable over ODB2

## What cars are supported?
I've only tested on my W203 C200 CDI, but in theory, any W203/211/219/209 should work as their underlying CANBUS architecture is the same

## What you'll need
* Android headunit - preferably rooted
* Arduino Uno
* Canbus shields (2x). Each MUST have a 16Mhz clock
* Wires to run from the back of the cars IC to the Arduino (Located in the glovebox)
* Serial - USB cable for Arduino

# Directory overview
* UNO_CODE - Code for Arduino uno. See README within the folder for more details
* app - Code for android application

#  Useful files
* parse_dat_v3.py - Processes data files from XSMonitor into CAN descriptor files
* db_converter.py - Converts files generated by parse_dat_v3.py into Kotlin object classes
