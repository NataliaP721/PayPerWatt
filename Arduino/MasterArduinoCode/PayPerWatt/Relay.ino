#define CLOSE LOW
#define OPEN HIGH

#include "PayPerWatt.h"

void relayClose() {
  digitalWrite(relayPin, CLOSE);
}
void relayOpen() {
  digitalWrite(relayPin, OPEN);
}
