#include "PayPerWatt.h"

void LED(){  // put your main code here, to run repeatedly:
  if(charging==true)
    digitalWrite(ledPin, HIGH);   // turn the LED on (HIGH is the voltage level)
  else
    digitalWrite(ledPin, LOW);   // turn the LED on (HIGH is the voltage level)
}

