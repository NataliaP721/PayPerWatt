/**
 * In final code I can use Serial instead of SoftwareSerial if we run out of pins
 * Also, address all TODO statements
 */
#include <SoftwareSerial.h>
SoftwareSerial mySerial(8, 7); // TX-8, RX-7
/**
 * The value for max_authorized_cost will be send by the app 
 */
double max_authorized_cost = 0.00; 
/**
 * Stores the payment status sent from the app - either true or false
 */
boolean paymentStatus = false; 
/**
 * Controls what is being sent to the app. Final cost or current power/cost/other values
 */
boolean send_final_cost = false;
/**
 * True when max_authorized cost has been recieved and false otherwise
 */
boolean start_charging = false;
/**
 * Stores the data read from the app
 */
String inputFromApp = "";

int count = 0;//for testing only

void setup() {
  // Open serial communications for printing to the serial monitor
  //Serial.begin(9600); //for testing only
  //Serial.println("Type AT commands!"); //for configuring bluetooth module
  
  // The HC-06 defaults to 9600 according to the datasheet.
  mySerial.begin(9600);
}
/* The app does not read and write data simultaneously because there is only one RFCOMM channel/socket
 * It is not possible to have multiple communication channels with the same Bluetooth Module
 * If the read starts first, write will be blocked until the read finishes, and vice versa
 * The reliability of data transfer is compromised (i.e: the message recieved is not the same as the message sent)
 * So make sure to only do one thing at a time, either read or write
 */
void loop() {
  recieveDataFromApp(); // this is a write operation for the app
  sendDataToApp();  
  count++;
}
/**
 * Reads info send by the app. Checks if the string recieved says "payment_confirmed\n"
 * (Note: the newline is important)
 * If it does, paymentStatus is set to true; else paymentStatus = false.
 */
void recieveDataFromApp(){
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
    else if(inputFromApp == "stop_charging"){
      send_final_cost = true;
      //TODO call method to turn off the realy here
      start_charging = false;
      sendDataToApp();
    }
    else if(inputFromApp.charAt(0) == '$'){ //means max authorized price is being sent
      max_authorized_cost = inputFromApp.substring(1).toDouble();
      //TODO call method to turn on the relay here
      start_charging = true;
    }
 
    //Serial.println(inputFromApp);//for testing
    //Serial.println((String)paymentStatus);//for testing
    //Serial.println((String)send_final_cost);//for testing
    //Serial.println((String)max_authorized_cost+ "\n");//for testing

    inputFromApp = "";
  } 
}
/**
 * Sends the values to the app - also sends the final cost to the app when requested
 */
void sendDataToApp(){
    String to_send = "";
    if(!send_final_cost && start_charging){
        double cost = count; //TODO call the cost function here to get the correct cost value
        double power_consumed = count; //TODO call the power_consumed function here - in Watts
        int time_hours = count; //TODO call the charging time function here 
        int time_minutes = count; //TODO call the charging time function here
        int time_seconds = count; //TODO call the charging time function here
        double rate_of_power_consumed = count; //TODO call the power consumed function here - in kWh
        
        to_send = formatData(cost,power_consumed,time_hours,time_minutes,
                      time_seconds,rate_of_power_consumed);  
    }
    else if(send_final_cost){
        double cost = 50; //TODO call the cost function here to get the correct cost value
       
        if(cost > max_authorized_cost)
            to_send = "*" + (String)max_authorized_cost +"\t";
        else        
            to_send = "*" + (String)cost +"\t";

        send_final_cost = false;
    }
    //if (Serial.available()){ //this is only required if I want to send data from the serial monitor
    //  delay(2); 
    mySerial.print(to_send);
    delay(500);
}
/**
 * Formats the data into the format that will be displayed on the app
 */
String formatData(double cost, double power_consumed, int time_hours,
                    int time_minutes, int time_seconds, 
                    double rate_of_power_consumed){
  String temp = "Cost\n " + (String)cost + " $CAD\n";
  temp += "\nTotal Power Consumed\n " + (String)power_consumed + " W\n";
  temp += "\nTotal Charging Time\n " + (String)time_hours +":" + (String)time_minutes +
            ":" + (String)time_seconds + " (HH:MM:SS)\n";
  temp += "\nRate of Power Consumption\n" + (String)rate_of_power_consumed + " kWh\t";
  return temp;
}
