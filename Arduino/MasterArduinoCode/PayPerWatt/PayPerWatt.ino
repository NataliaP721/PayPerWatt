// include the library code:
#include <SoftwareSerial.h>
#include <LiquidCrystal.h>
#include "PayPerWatt.h"
#include "CurrentTransformer.h"

/**
 * Controls if the device is charging or not.
 */
boolean charging = false; // Start with charging is false

/**
 * Determines the chosen cost per Wh. We chose this value because it increments quickly enough to illustrate it works for the demo without being too high.
 */
double costPerWattHour = 0.05; // The price of electricity is $0.052/kWh - From ENMAX in calgary the price in February

/**
 * The total cost the user will be charged. 
 */
double cost = 0;

/**  
 * Sets up the LCD. Initialize the library by associating any needed LCD interface pin with the arduino pin number it is connected to.
 */
const int rs = 12, en = 11, d4 = 5, d5 = 4, d6 = 3, d7 = 2;
LiquidCrystal lcd(rs, en, d4, d5, d6, d7);

/**
 * The Current transformer pin
 */
int ctPin = 1; 

/**
 * THe Bluetooth Rx pin
 */
int btRxPin = 7;

/**
 * The Bluetooth Tx pin
 */
int btTxPin = 8;

/**
 * The relay pin
 */
int relayPin = 13;

/**
 * The LED pin
 */
int ledPin = 10;

/**
 * Sets the Tx and Rx pins for the SOfftwareSerial, which allows UART serial communication with the Bluetooth module.  
 */
SoftwareSerial mySerial(btTxPin, btRxPin); // TX-8, RX-7

/**
 * Sets the initial state of the code. Only runs once at the beggining. 
 */
void setup() {
  // put your setup code here, to run once:
  pinMode(relayPin, OUTPUT);  // Set relay pin for output
  pinMode(ledPin, OUTPUT);  // Set LED pin for output
  relayOpen();  // Open the relay initially to stop charging
  
  mySerial.begin(9600);   // The HC-06 defaults to 9600 according to the datasheet.

  // set up the LCD's number of columns and rows:
  lcd.begin(16, 2);
  // Print a message to the LCD.
  lcd.clear();
  lcd.print("PayPerWatt");
  delay(10000);   // Delay placed here so the initial CT readings are 0.
}

/**
 * This loop runs continously until the program terminates.
 */
void loop() {
  // put your main code here, to run repeatedly:
  
  LED();  // Turn LED on if device is charging, otherwise off
  CurrentTransformer(); // Collect CT measurement values and output to LCD
  
  // Bluetooth receive/send
  charging = recieveDataFromApp();
  sendDataToApp(costPerWattHour); 

  // Stops charging by opening relay if cost exceeds maximum cost
  if(cost>=max_authorized_cost) {
    charging = false;
    relayOpen();
  } 
}
