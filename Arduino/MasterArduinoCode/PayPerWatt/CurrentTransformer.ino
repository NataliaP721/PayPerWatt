/**
 * Since the current going through the relay in the power circuit cannot exceed 10A, a higher burden resistor was used to allow for more accurate measurements on smaller current measurements. 
 * The maximum votage range allowed in the power circuit for the Arduino not to get damaged is 16A. 10A in the power circuit will give a Vpp of 3.11V, ranging from 0.944V to 4.05V. 
 */

// include the library code:
#include "PayPerWatt.h"

/**
 * Measures the elapsed time. 
 */
elapsedMillis timeElapsed; //declare global if you don't want it reset every time loop runs

/**
 * Stores the previous time, so the time interval can be used in the Wh calculation.
 */
unsigned long previousTime = 0;

/**
 * The Wh being used to charge device. 
 */
double Wh = 0;

/**
 * The RMS power being used to charge device.  
 */
double RMSPower = 0;

/**
 * The RMS current flowing through the power circuit. 
 */
double RMSCurrent = 0;

/**
 * temp is the temporary current measurement, 
 * sumRMSCurrent is the sum of RMSCurrent measurements used to calculate average. 
 */
double temp = 0, sumRMSCurrent = 0;

/**
 * Measures the current in the power circuit using a current transformer. Outputs the results to the LCD monitor. 
 */
void CurrentTransformer(){
  if(charging == true)
  {
    temp = 0;
    sumRMSCurrent = 0;
    int analogV = 0;
    int Vpp = 0;

    // Code to average 20 current measurements for higher accuracy and less fluctauations. 
    for(int j = 0; j<20; j++) {
      for(int i = 0; i<=512; i++)                         // Monitors and logs the current input for 300 cycles to determine current
      {
        analogV = analogRead(ctPin);                 // Reads current input and records maximum current
        if(analogV >= Vpp)
          Vpp = analogV;
      }
      
      // The Arduino ADC reads the analog voltage as a number from 0 to 1024. 
      //2.5137 was used as the mid-point because when nothing is plugged in, 0.137V was the measured value. 
      //R = 220, but the experimentally deterined 270 was used from calibration procedures. 
      temp = ((Vpp*4.9/1000)-2.5165)/270/2*0.707*2000; 
      
      // if the current is below 0, set the measurement to 0. 
      if(temp<0){
        temp = 0;
      }
      sumRMSCurrent += temp;
    }
    RMSCurrent = sumRMSCurrent/20;
      
    RMSPower = 119*RMSCurrent;  // Calculates RMS Power Assuming Voltage 110VAC. This is approximately the RMS voltage on the AC power circuit. 
    if(charging == true) {
      Wh = RMSPower*(((double)timeElapsed-(double)previousTime)/1000.0/60.0/60.0);
      cost += Wh*costPerWattHour;
      if(cost<0){
        cost = 0;
      }
    }
    previousTime = timeElapsed;

    delay(5);
    lcd.clear();
    lcd.print("$"); 
    lcd.print(cost);
    unsigned long time_hours = timeElapsed/1000/60/60; 
    unsigned long time_minutes = timeElapsed/1000/60-time_hours*60;
    unsigned long time_seconds = timeElapsed/1000-time_minutes*60;
  
    String hours = (String)time_hours;
    String minutes = (String)time_minutes;
    String seconds = (String)time_seconds;
  
    // Add zero in front for padding
    if(hours.length() < 2)
      hours = "0"+hours;
    if(minutes.length() < 2)
      minutes = "0"+minutes;
    if(seconds.length() < 2)
      seconds = "0"+seconds;
      
    lcd.setCursor(8,0);
    lcd.print(hours);
    lcd.print(":"); 
    lcd.setCursor(11,0);
    lcd.print(minutes);
    lcd.print(":"); 
    lcd.setCursor(14,0);
    lcd.print(seconds);
    lcd.print(":"); 
    lcd.setCursor(11,1); 
    lcd.print(RMSCurrent);
    lcd.print("A");
    lcd.setCursor(0,1);
    lcd.print(RMSPower); 
    lcd.print("W"); 
    delay(500);
  }
  else {
    lcd.clear();
    lcd.print("1. Download App");
    delay(2000);
    lcd.clear();
    lcd.print("2. Connect to");
    lcd.setCursor(0,1);
    lcd.print("Bluetooth");
    delay(2000);
    lcd.clear();
    lcd.print("PayPerWatt");
    delay(2000);
  }
}

