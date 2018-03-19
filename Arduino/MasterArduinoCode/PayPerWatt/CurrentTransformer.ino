// Test the numbers + LCD

// Since the current going through the relay in the power circuit cannot exceed 10A, a higher burden resistor was used to allow for more accurate measurements on smaller voltage measurements.
// The maximum votage range allowed in the power circuit for the Arduino not to get damaged is 45A. 10A in the power circuit will give a Vpp of 3.11V, ranging from 0.944V to 4.05V. 

// CT code adapted from the Arduino code at http://www.instructables.com/id/Simple-Arduino-Home-Energy-Meter/
// and LCD code adapted from https://www.arduino.cc/en/Tutorial/HelloWorld

// include the library code:
//#include <LiquidCrystal.h>
#include <elapsedMillis.h>
#include "PayPerWatt.h"

elapsedMillis timeElapsed; //declare global if you don't want it reset every time loop runs

// initialize the library by associating any needed LCD interface pin
// with the arduino pin number it is connected to

double Wh = 0;
double RMSPower = 0;
double RMSCurrent = 0;

void CurrentTransformer(bool charging){
  if(charging == true)
  {
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
    //R = 220, but 270 gave better experimental results.
    RMSCurrent = ((Vpp*4.9/1000)-2.5137)/270/2*0.707*2000; 
    // if the current is below 0, set the measurement to 0. 
    if(RMSCurrent<0){
      RMSCurrent = 0;
    }
    
    RMSPower = 110*RMSCurrent;  // Calculates RMS Power Assuming Voltage 110VAC. This is approximately the RMS voltage on the AC power circuit. 
    
    Wh = Wh + (RMSPower * (timeElapsed/1000/60/60));    // Calculate kWh used

    // Remove current and add cost
    delay(500);
    lcd.clear();
    lcd.print("PayPerWatt");
    lcd.setCursor(11,0);   
    lcd.print(RMSCurrent);
    lcd.print("A");
    lcd.setCursor(0,1);
    lcd.print(RMSPower); 
    lcd.print("W"); 
    lcd.setCursor(9,1);
    lcd.print(Wh); 
    lcd.print("Wh");
  }
  else{
    lcd.clear();
    lcd.print("PayPerWatt");
  }
}
int getMil(){
  return timeElapsed;
}
double getWh(){
  return Wh;
}
double getW() {
  return RMSPower;
}

