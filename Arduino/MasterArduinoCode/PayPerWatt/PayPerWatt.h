#ifndef PAYPERWATT_H
#define PAYPERWATT_H

// Set the cost per Wh in PayPerWatt.ino based on average Calgary value. 
extern double costPerWattHour;
extern double cost;

// True if charging, false otherwise. Is set in Bluetooth code based on app input.
extern boolean charging;

// Pins on Custom Arduino
extern int ctPin;
extern int btRxPin;
extern int btTxPin;
extern int relayPin;
extern int ledPin;

#endif
