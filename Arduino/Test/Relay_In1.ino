// CLOSE and OPEN were defined to make things easier.
// The relay input is pin 7. 

#define CLOSE LOW
#define OPEN HIGH

int in1 = 7;
void setup() {
  pinMode(in1, OUTPUT);
  digitalWrite(in1, CLOSE);
}
void loop() {

}
