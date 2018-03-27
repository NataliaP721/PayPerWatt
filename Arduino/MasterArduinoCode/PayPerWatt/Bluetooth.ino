/**
 * In final code I can use Serial instead of SoftwareSerial if we run out of pins
 * Also, address all TODO statements
 * Does it stop charging when max_authorized_cost is reached?-----------------------------------------------------------------------------------------------TEST THIS
 */
#include "CurrentTransformer.h"
#include "PayPerWatt.h"

/**
 * The value for max_authorized_cost will be send by the app 
 */
double max_authorized_cost = 0; 
/**
 * Stores the payment status sent from the app - either true or false
 */
boolean paymentStatus = false; 
/**
 * Controls what is being sent to the app. Final cost or current power/cost/other values
 */
boolean send_final_cost = false;
/**
 * Stores the data read from the app
 */
String inputFromApp = "";

/**
 * Reads info send by the app. Checks if the string recieved says "payment_confirmed\n"
 * (Note: the newline is important)
 * If it does, paymentStatus is set to true; else paymentStatus = false.
 */
boolean recieveDataFromApp(){
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
//      relayOpen();
//      charging = false;
      sendDataToApp(charging);
    }
    else if(inputFromApp.charAt(0) == '$'){ //means max authorized price is being sent
      max_authorized_cost = inputFromApp.substring(1).toDouble();
      //TODO call method to turn on the relay here
      relayClose();
      charging = true;
    }
    inputFromApp = "";
  } 
  return charging;
}
/**
 * Sends the values to the app - also sends the final cost to the app when requested
 */
void sendDataToApp(double costPerWatt){
    String to_send = "";
    if(!send_final_cost && charging){
        double power_consumed = RMSPower; // Instantaneous Power
        // Split millis into HH:MM:SS or 0 if not charging
        unsigned long time_hours, time_minutes, time_seconds;
        if(charging == true){
          unsigned long mils = timeElapsed;
          time_hours = mils/1000/60/60; 
          time_minutes = mils/1000/60-time_hours*60;
          time_seconds = mils/1000-time_minutes*60; 
        }
        else {
           time_hours = 0; 
           time_minutes = 0;
           time_seconds = 0; 
        }
        double rate_of_power_consumed = Wh; //TODO call the power consumed function here - in Wh
//        double cost = costPerWattHour*rate_of_power_consumed; //TODO call the cost function here to get the correct cost value -----------------------------------

        to_send = formatData(cost,power_consumed,time_hours,time_minutes,
                      time_seconds,rate_of_power_consumed);  
    }
    else if(send_final_cost){
        double rate_of_power_consumed = Wh; //TODO call the power consumed function here - in Wh
       // double cost = costPerWattHour*rate_of_power_consumed;
        //double cost = 50; //TODO call the cost function here to get the correct cost value ------------------------------------------------------------
       
        if(cost > max_authorized_cost){
//            charging = false;
//            relayOpen();
            to_send = "*" + (String)max_authorized_cost +"\t";
        }
        else        
            to_send = "*" + (String)cost +"\t";

        send_final_cost = false;
    }
    mySerial.print(to_send);
    delay(500);
}
/**
 * Formats the data into the format that will be displayed on the app
 */
String formatData(double cost, double power_consumed, unsigned long time_hours,
                    unsigned long time_minutes, unsigned long time_seconds, 
                    double rate_of_power_consumed){
                      
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

  String temp = "Cost\n " + (String)cost + " $CAD\n";
  temp += "\nTotal Power Consumed\n " + (String)power_consumed + " W\n";
  temp += "\nTotal Charging Time\n " + hours +":" + minutes +
            ":" + seconds + " (HH:MM:SS)\n";
  temp += "\nRate of Power Consumption\n" + (String)rate_of_power_consumed + " Wh\t";
  return temp;
}
