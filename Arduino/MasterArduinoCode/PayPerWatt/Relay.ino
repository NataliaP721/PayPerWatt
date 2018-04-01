/**
 * Since the relay is active low, macros were used for relay states. The relay is CLOSED when the relay pin is set to LOW, which causes the device to start charging. The relay is OPEN when the relay pin
 * is set to HIGH, which causes the device to stop charging.  
 */
#define CLOSE LOW
#define OPEN HIGH

#include "PayPerWatt.h"
#include "CurrentTransformer.h"

/**
 * Closes relay to start charging
 */
void relayClose() {
  timeElapsed = 0;  // Start timer when device starts charging
  Wh = 0; // When user finishes charging, reset Wh
  cost = 0;   // When user finishes charging, reset cost
  digitalWrite(relayPin, CLOSE);
}

/**
 * Opens relay to stop charging
 */
void relayOpen() {
  digitalWrite(relayPin, OPEN);
}
