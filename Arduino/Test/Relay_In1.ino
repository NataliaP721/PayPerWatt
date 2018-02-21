// The relay will close when control signal is HIGH if wires connected to 
// NO and COM of relay. The relay is open when signal is LOW.

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
