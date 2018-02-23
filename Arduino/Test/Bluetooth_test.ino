/**
 * This code is for testing only - need to use Serial instead of SoftwareSerial in final code
 * Also need to integrate with the code that has the actual current,power and cost values
 */
#include <SoftwareSerial.h>
SoftwareSerial mySerial(4, 2); // RX-2, TX-4
boolean paymentStatus = false; // Stores the payment status from the app - either true or false
String inputFromApp = "";
int count = 0;//for testing only

void setup() {
  // Open serial communications for printing to the serial monitor
  Serial.begin(9600); 
  //Serial.println("Type AT commands!"); //for configuring bluetooth module
  
  // The HC-06 defaults to 9600 according to the datasheet.
  mySerial.begin(9600);
}
/*The app does not read and write data simultaneously because there is only one RFCOMM channel/socket
 * t is not possible to have multiple communication channels with the same Bluetooth Module
 * If the read starts first, write will be blocked until the read finishes, and vice versa
 * The reliability of data transfer is compromised (i.e: the message recieved is not the same as the message sent)
 * So make sure to only do one thing at a time, either read or write
 */
void loop() {
  //checkPaymentStatus(); // this is a write operation for the app
  while (count < 100){ //this is a read operation for the app
    sendReadings(count,count,count);  //current,power,cost
    //modify this function to make a call to getCurrent, getPower, getCost functions
    count++;
  }
}
/**
 * Reads info send by the app. Checks if the string recieved says "payment_confirmed\n"
 * (Note: the newline is important)
 * If it does, paymentStatus is set to true; else paymentStatus = false.
 */
void checkPaymentStatus(){
  if (mySerial.available()) {
    while(mySerial.available()) { // While there is more to be read, keep reading.
      char char_read = (char)mySerial.read();
      if(char_read == '\n')
        break;
      inputFromApp += char_read;
      delay(2);
    }
    if(inputFromApp == "payment_confirmed"){ 
      paymentStatus = true;
    }
    else {paymentStatus = false;}
    Serial.println(inputFromApp);//for testing
    Serial.println((String)paymentStatus);//for testing
    inputFromApp = "";
  } 
}
/**
 * Sends the values to the app
 */
void sendReadings(double current, double power, double cost){
  //if (Serial.available()){ //this is only required if I want to send data from the serial monitor
   // delay(2); 
    mySerial.println(formatData(current,power,cost));
    delay(100);
 // }
}
/**
 * Formats the data into the format that will be displayed on the app
 */
String formatData(double current, double power, double cost){
  String temp = "Current Flow\n " + (String)current + " A\n";
  temp += "\nPower Consumption\n " + (String)power + " kWh\n";
  temp += "\nCost\n " + (String)cost + " $CAD\t";
  return temp;
}
