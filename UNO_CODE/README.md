# Code for Ardunio UNO

## Requirments
* 2x CANBUS shields - IMPORTANT: Clock freq must be 16Mhz on BOTH modules. 8Mhz will not work
* Arduino Uno
* USB Cable from uno to android tablet

## Connections
I have Set the CS Pins on my CAN C Module to Pin 5, and CAN B Module to Pin 4. Feel free to change these in code:


```cpp
...
// Change these 2 defines if your CS pins are different
// They CANNOT be the same
#define CANB_CS 4
#define CANC_CS 5
...
```

* CAN B Wires terminate at X30/6 connection (Under steering wheel CAN Hub)
* CAN C Wires terminate at Instrument cluster
