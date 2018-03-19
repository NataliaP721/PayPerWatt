// include the library code:
#include <SoftwareSerial.h>
#include <LiquidCrystal.h>

boolean charging = true;
double costPerWattHour;

const int rs = 12, en = 11, d4 = 5, d5 = 4, d6 = 3, d7 = 2;
LiquidCrystal lcd(rs, en, d4, d5, d6, d7);

int ctPin = 1; 
int btRxPin = 7;
int btTxPin = 8;
int relayPin = 13; // NOT SURE WHICH PIN
int ledPin = 10; // NOT SURE WHICH PIN

SoftwareSerial mySerial(btTxPin, btRxPin); // TX-8, RX-7

void setup() {
  // put your setup code here, to run once:
  pinMode(relayPin, OUTPUT);
  pinMode(ledPin, OUTPUT);
  LED();  // Turn LED on to indicate device is ON
  
  mySerial.begin(9600);   // The HC-06 defaults to 9600 according to the datasheet.

  // set up the LCD's number of columns and rows:
  lcd.begin(16, 2);
  // Print a message to the LCD.
  lcd.clear();
  lcd.print("PayPerWatt");
  delay(10000);   // Delay placed here so the initial CT readings are 0.
}

void loop() {
  // put your main code here, to run repeatedly:
  CurrentTransformer(charging);
  charging = recieveDataFromApp(); // this is a write operation for the app
  sendDataToApp(costPerWattHour);  
}
