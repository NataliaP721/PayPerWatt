#define CLOSE LOW
#define OPEN HIGH

#include "PayPerWatt.h"
#include "CurrentTransformer.h"

// Closes relay to start charging
void relayClose() {
  timeElapsed = 0;   // Start timer when device starts charging
  Wh = 0;
  cost = 0; // When user finishes charging, reset cost
  digitalWrite(relayPin, CLOSE);
}

// Opens relay to stop charging
void relayOpen() {
  digitalWrite(relayPin, OPEN);
  Wh = 0;   // When user finishes charging, reset Wh
}
