// Since the current going through the relay in the power circuit cannot exceed 10A, a higher burden resistor was used to allow for more accurate measurements on smaller voltage measurements.
// The maximum votage range allowed in the power circuit for the Arduino not to get damaged is 16A. 10A in the power circuit will give a Vpp of 3.11V, ranging from 0.944V to 4.05V. 

// CT code adapted from the Arduino code at http://www.instructables.com/id/Simple-Arduino-Home-Energy-Meter/
// and LCD code adapted from https://www.arduino.cc/en/Tutorial/HelloWorld
// include the library code:
#include <LiquidCrystal.h>

// initialize the library by associating any needed LCD interface pin
// with the arduino pin number it is connected to
const int rs = 12, en = 11, d4 = 5, d5 = 4, d6 = 3, d7 = 2;
LiquidCrystal lcd(rs, en, d4, d5, d6, d7);

int ctPin = 1;                                   // Assign CT input to Analog pin 1
double kWh = 0;
int peakPower = 0;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);                                 // Start serial communication
  Serial.println("Running");
  // set up the LCD's number of columns and rows:
  lcd.begin(16, 2);
  // Print a message to the LCD.
  lcd.print("   PayPerWatt");
  delay(2000);
}

void loop() {
  // put your main code here, to run repeatedly:
  int analogV = 0;
  int Vpp = 0;

  for(int i = 0; i<=300; i++)                         // Monitors and logs the current input for 300 cycles to determine current
  {
    analogV = analogRead(ctPin);                 // Reads current input and records maximum current
    if(analogV >= Vpp)
      Vpp = analogV;
  }
  
  // The Arduino ADC reads the analog voltage as a number from 0 to 1024. 
  //2.5137 was used as the mid-point because when nothing is plugged in, 0.137V was the measured value. 
  //R = 220, but 250 gave better experimental results.
  double RMSCurrent = ((Vpp*4.9/1000)-2.5137)/250/2*0.707*2000; 
  // if the current is below 0, set the measurement to 0. 
  if(RMSCurrent<0){
    RMSCurrent = 0;
  }
  
  int RMSPower = 110*RMSCurrent;  // Calculates RMS Power Assuming Voltage 110VAC. This is approximately the RMS voltage on the AC power circuit. 
  if(RMSPower > peakPower)
  {
    peakPower = RMSPower;
  }
  
  kWh = kWh + (RMSPower * (2.05/60/60/1000));    // Calculate kWh used

    delay(500);
    lcd.clear();
    lcd.print("   PayPerWatt");
    lcd.setCursor(1,1);   
    lcd.print(RMSCurrent);
    lcd.print(" A");
    lcd.setCursor(11,1);
    lcd.print(RMSPower); 
    lcd.print(" W"); 
  

//    delay(2000);
//    Serial.print(Vpp);
//    Serial.println("A");
//  Serial.print(RMSPower);
//  Serial.println("W");
//  Serial.print(kWh);
//  Serial.println("kWh");
//  Serial.print(peakPower);
//  Serial.println("W");
}
