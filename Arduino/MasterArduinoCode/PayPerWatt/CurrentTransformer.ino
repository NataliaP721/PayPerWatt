// Since the current going through the relay in the power circuit cannot exceed 10A, a higher burden resistor was used to allow for more accurate measurements on smaller voltage measurements.
// The maximum votage range allowed in the power circuit for the Arduino not to get damaged is 16A. 10A in the power circuit will give a Vpp of 3.11V, ranging from 0.944V to 4.05V. 

// CT code adapted from the Arduino code at http://www.instructables.com/id/Simple-Arduino-Home-Energy-Meter/
// and LCD code adapted from https://www.arduino.cc/en/Tutorial/HelloWorld

// include the library code:
#include "PayPerWatt.h"

elapsedMillis timeElapsed; //declare global if you don't want it reset every time loop runs
unsigned long previousTime = 0;

// initialize the library by associating any needed LCD interface pin
// with the arduino pin number it is connected to

double Wh = 0;
double RMSPower = 0;
double RMSCurrent = 0;

void CurrentTransformer(){
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
    //R = 220, but the experimentally deterined 270 was used from calibration procedures. 
    RMSCurrent = ((Vpp*4.9/1000)-2.5437)/270/2*0.707*2000; 
    
    // if the current is below 0, set the measurement to 0. 
    if(RMSCurrent<0){
      RMSCurrent = 0;
    }
    
    RMSPower = 110*RMSCurrent;  // Calculates RMS Power Assuming Voltage 110VAC. This is approximately the RMS voltage on the AC power circuit. 
    if(charging == true) {
       Wh = RMSPower * ((timeElapsed-previousTime)/60/60);    // Calculate Wh used
       cost = cost+Wh*costPerWattHour;
    }
    previousTime = timeElapsed;

    // Remove current and add cost -----------------------------------------------------------------------------------------------------------------------------
    delay(50);
    lcd.clear();
    lcd.print("PayPerWatt");
    delay(1000);
    lcd.clear();
    lcd.setCursor(11,0);   
    lcd.print(RMSCurrent);
    lcd.print("A");
//    lcd.setCursor(0,1);
//    lcd.print(RMSPower); 
//    lcd.print("W"); 
//    lcd.setCursor(9,1);
//    lcd.print(Wh); 
//    lcd.print("Wh");
    delay(50);
  }
  else{
    lcd.clear();
    lcd.print("Connect to");
    lcd.setCursor(0,1);
    lcd.print("Bluetooth");
    delay(5000);
    lcd.clear();
    lcd.print("PayPerWatt");
    delay(5000);
  }
}

