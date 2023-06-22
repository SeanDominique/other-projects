#include <Stepper.h>
#include <math.h>
#include <WiFi.h>
#include <WiFiUdp.h>

// WiFi network name and password:
const char * ssid = ""; // fill out
const char * password = "";  // fill out

//WiFiUDP Udp;
unsigned int localUdpPort = 4210;  // local port to listen on
char incomingPacket[255];  // buffer for incoming packets
const int stepsPerRevolution = 2048;
const double wheelDiameter = 7;   // (cm)
const double wheelCircumference = PI * wheelDiameter;   // (cm)
const int rpm = 15;
const double chairWidth = 6.4;    // (cm)
const double radius = 15;
double ratio = (radius - chairWidth) / (radius + chairWidth);
const int stepsLeft = 1;
const int stepsRight = -1;

WiFiUDP Udp;
const int ONBOARD_LED = 2;

// Create the steppers
Stepper stepperLeft = Stepper(stepsPerRevolution, 19, 22, 21, 23);
Stepper stepperRight = Stepper(stepsPerRevolution, 18, 4, 5, 2);


void setup() {
  stepperLeft.setSpeed(rpm);
  stepperRight.setSpeed(rpm);
  Serial.begin(115200);
  connect_to_wifi();
}

// Control WC
// Listen for commands: 2 = Start, 1 = Stop
void loop() {

  char val = receive_packet();
  
  // If no letter was received, do nothing.
  if (!val) {
    return;
  }

  // Move WC if start command is sent by python script
  if ((val == '2') || (val == 2)) {
    Serial.println("Moving...");
    while (true) {
    stepperRight.step(stepsRight);
    stepperLeft.step(ratio * stepsLeft);

    // Listen for a termination signal (ie. 1)
    char val2 = receive_packet();
    if ((val2 == '1') || (val2 == 1)) {
      Serial.println("Stopped");
      break;
      }
    }
  }
}


// ~~~~~~~~~~~~~~~~~~~~~~~ FUNCTIONS ~~~~~~~~~~~~~~~~~~~~~~~

char receive_packet(){
  int packetSize = Udp.parsePacket();
  if (packetSize)
  {
    // receive incoming UDP packets
    Serial.printf("Received %d bytes from %s, port %d\n", packetSize, Udp.remoteIP().toString().c_str(), Udp.remotePort());
    int len = Udp.read(incomingPacket, 255);
    if (len > 0)
    {
      incomingPacket[len] = 0;
    }
    Serial.printf("UDP packet contents: %s\n", incomingPacket);
    return incomingPacket[0];
  }
  return 0;
}

void connect_to_wifi() {
  WiFi.begin(ssid, password);
  WiFi.mode(WIFI_STA);
  Serial.print("Trying to connect to WiFi...");
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }
  Serial.println(" connected");
  Udp.begin(localUdpPort);
  Serial.printf("Now listening at IP %s, UDP port %d\n", WiFi.localIP().toString().c_str(), localUdpPort);
}



// ~~~~~~~~~~~~~~~~ OPTIONAL FUNCTIONS ~~~~~~~~~~~~~~~~~~~~
// "Manual mode": follow commands from serial input. 
void drive_from_serial_input() {
  if (Serial.available() > 0) {
    int letter = Serial.read();
    if (letter == 'w') {
      Serial.println("Going straight.");
      go_straight(5);
    } else if (letter == 'a') {
      Serial.println("Going left.");
      turn_left();
    } else if (letter == 'd') {
      Serial.println("Going right.");
      turn_right();
    } else if (letter == 's') {
      Serial.println("Going back.");
      go_straight(-5);
      } else if ((letter != 'w') && (letter != 'a') && (letter != 's') && (letter != 'd')) {
      Serial.println("Please use w, a or d to move.");
    } 
  }
}


// Motor functions

void go_circle(double radius) {
  float stepRinc = (radius - chairWidth)/radius;
  float stepLinc = radius/radius;
  Serial.println("R step inc:");
  Serial.print(stepRinc);
  Serial.println("L step inc:");
  Serial.print(stepLinc);
  float stepRPos = 0.0;
  float stepLPos = 0.0;

  for(int i = 0; i <= radius;i++){
    stepRPos += stepRinc;
    stepLPos += stepLinc;
    Serial.println(i);
    Serial.println("Right");
    Serial.println(stepRPos);
    Serial.println("Left");
    Serial.println(stepLPos);
  }

    stepperRight.step(stepRPos);
    stepperLeft.step(-stepLPos);
}

void go_straight(double distance) {
  double revolutions = distance / wheelCircumference;
  int steps_needed = (int)floor(stepsPerRevolution * revolutions);
  int steps_taken = 0;
  int stepsRight = 1;
  int stepsLeft = -1;

  while (steps_taken < steps_needed) {
    stepperRight.step(stepsRight);
    stepperLeft.step(stepsLeft);
    steps_taken++;
  }
}

void turn_left() {
  int steps_taken = 0;
  int steps_needed = 700;
  int stepsRight = 1;
  int stepsLeft = 1;
  
  while (steps_taken < steps_needed) {
    stepperRight.step(stepsRight);
    stepperLeft.step(stepsLeft);
    steps_taken++;
  }
}


void turn_right() {
  int steps_taken = 0;
  int steps_needed = 700;
  int stepsRight = -1;
  int stepsLeft = -1;
  
  while (steps_taken < steps_needed) {
    stepperRight.step(stepsRight);
    stepperLeft.step(stepsLeft);
    steps_taken++;
  }
}



// OLD
// circle values
//double outer_path_length = 2 * M_PI * (radius + chairWidth / 2);
//double inner_path_length = 2 * M_PI * (radius - chairWidth / 2);

// Determine how many steps each motor will need to take
//double revolutions_outer = outer_path_length / wheelCircumference;
//double revolutions_inner = inner_path_length / wheelCircumference;

//int steps_needed_outer = (int)floor(stepsPerRevolution * revolutions_outer);
//int steps_needed_inner = (int)floor(stepsPerRevolution * revolutions_inner);
//int steps_taken_inner = 0;
//int steps_taken_outer = 0;
//int stepsInner = 1;

// Take the steps while steps remain.
//while ((steps_taken_inner < steps_needed_inner) && (steps_taken_outer < steps_needed_outer)) {
  //stepperLeft.step(stepsInner);
  //steps_taken_inner += stepsInner;

  // Outer steps are calculated as a ratio
  //double remaining_steps_ratio = (steps_needed_outer - steps_taken_outer) / (steps_needed_inner - steps_taken_inner);
  //int stepsOuter = round(remaining_steps_ratio * stepsInner);
  //stepperRight.step(stepsOuter);
  //steps_taken_outer += stepsOuter;
//}
//}
