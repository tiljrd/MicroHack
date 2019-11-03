#include <Keypad.h>

//keypad consisting of four rows and four columns
const byte rows = 4;
const byte cols = 4;

//the keys on the keypad are positioned in the following way
char keys[rows][cols] = {
  {'1', '2', '3', 'F'},
  {'4', '5', '6', 'E'},
  {'7', '8', '9', 'D'},
  {'A', '0', 'B', 'C'}
};

//pins of the Arduino that the keypad is connected to
byte rowPins[cols] = {6, 7, 8, 9}; 
byte colPins[rows] = {2, 3, 4, 5}; 

//create keypad object using the keypad library
Keypad keypad = Keypad( makeKeymap(keys), rowPins, colPins, rows, cols );

//define led pins
int redLed = 10;
int greenLed = 12;

//define initial pass code
String passCode = "1234";

//attempt of the user to type in a code
String attempt = "";

void setup() {
  
  //set the Led pinmodes to outputs
  pinMode(redLed, OUTPUT);
  pinMode(greenLed, OUTPUT);
  
  //switch of the leds at the beginning
  digitalWrite(redLed, LOW);
  digitalWrite(greenLed, LOW);

  Serial.begin(9600);
}

void loop() {

//code for reading the input of the keypad on the android phone
/*
  if(Serial.available() > 0) {
    String code = Serial.readString();
    if(code == passCode[0]) {
      Serial.println("Correct code!");
      attempt = "";
      digitalWrite(greenLed, HIGH);
    } else {
      digitalWrite(redLed, HIGH);
    }
  }
  */
  
  //constantly get the key that is pressed (even if no key is pressed)
  char key = keypad.getKey();
  
  //only do anything if a key is actually pressed
  if (key != NO_KEY) {
  
    delay(50);
    
    //when starting to type in a code switch off the leds
    digitalWrite(greenLed, LOW);
    digitalWrite(redLed, LOW);
    
    //the C key is to confirm the input
    if (key == 'C') { //enter code
      
      //when the input code was correct switch on the green led and send this state to the raspberry pi
      if (attempt == passCode) {
        Serial.println("Correct code!");
        
        //user can start from the beginning
        attempt = "";
        
        digitalWrite(greenLed, HIGH);
        
      //when input was wrong switch on the red led and send this state to the raspberry pi
      } else {
        Serial.println("Wrong code");
        digitalWrite(redLed, HIGH);
        
        //user can start from the beginning
        attempt = "";
      }
    //reset the input of the keypad (in case of mistyping)  
    } else if (key == 'A') {
      Serial.println("Code reset");
      attempt = "";
      
    //change the pass code by first typing in the correct password followed by the new password  
    } else if (key == 'B') {
      
      //current pass code has to be entered first
      if (attempt.startsWith(passCode)) {
      
        //get the new passcode as the second part of the input
        passCode = attempt.substring(passCode.length());
        Serial.println("New pass code is " + passCode);
         attempt = "";
         
      } else {
        Serial.println(" Wrong initial code. Code is reset");
         attempt = "";
      }
    
    //before A, B or C was entered add the input of the user together to one input 
    } else {
      attempt += key;
    }
    

  }

}
