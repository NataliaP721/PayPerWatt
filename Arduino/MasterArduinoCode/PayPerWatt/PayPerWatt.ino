// include the library code:
#include <SoftwareSerial.h>
#include <LiquidCrystal.h>
#include "PayPerWatt.h"
#include "CurrentTransformer.h"

boolean charging = false; // Start with charging is false
double costPerWattHour = 0.000055; // $0.055/kWh - From ENMAX in calgary the price was 5.2 in February
double cost = 0;

// Initialize LCD
const int rs = 12, en = 11, d4 = 5, d5 = 4, d6 = 3, d7 = 2;
LiquidCrystal lcd(rs, en, d4, d5, d6, d7);

int ctPin = 1; 
int btRxPin = 7;
int btTxPin = 8;
int relayPin = 13;
int ledPin = 10;

SoftwareSerial mySerial(btTxPin, btRxPin); // TX-8, RX-7

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

/* The app does not read and write data simultaneously because there is only one RFCOMM channel/socket
 * It is not possible to have multiple communication channels with the same Bluetooth Module
 * If the read starts first, write will be blocked until the read finishes, and vice versa
 * The reliability of data transfer is compromised (i.e: the message recieved is not the same as the message sent)
 * So make sure to only do one thing at a time, either read or write
  */
void loop() {
  // put your main code here, to run repeatedly:
  LED();  // Turn LED on if device is charging, otherwise off
  CurrentTransformer(); // Collect CT measurement values and output to LCD
  // Bluetooth receive/send
  charging = recieveDataFromApp();
  sendDataToApp(costPerWattHour);  
}
