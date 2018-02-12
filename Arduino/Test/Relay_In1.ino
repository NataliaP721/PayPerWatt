// This code is untested. I think the relay is Active Low (unsure of this). 
// If this is the case, when HIGH is written, the relay will be open
// and when LOW is written, the relay will be closed.

int in1 = 7;
void setup() {
  pinMode(in1, OUTPUT);
  digitalWrite(in1, HIGH);
}
void loop() {
  digitalWrite(in1, LOW);
  delay(3000);
  digitalWrite(in1, HIGH);
  delay(3000);
}
