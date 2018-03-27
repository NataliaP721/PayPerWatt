#define CLOSE LOW
#define OPEN HIGH

#include "PayPerWatt.h"
#include "CurrentTransformer.h"

// Closes relay to start charging
void relayClose() {
  timeElapsed = 0;   // Start timer when device starts charging
  digitalWrite(relayPin, CLOSE);
}

// Opens relay to stop charging
void relayOpen() {
  digitalWrite(relayPin, OPEN);
  Wh = 0;   // When user finishes charging, reset Wh
  cost = 0;   // WHen user finishes charging, reset cost
}
