import serial
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

#initialise the serial port where the arduino will be connected
ser = serial.Serial('/dev/ttyUSB0',9600)

#path to the authentication file for the database
cred = credentials.Certificate('/home/pi/Desktop/smart-door-lock-5c58d-firebase-adminsdk-iaanw-5e952088fc.json')

#initialise the database
firebase_admin.initialize_app(cred, {
    	'databaseURL': 'https://smart-door-lock-5c58d.firebaseio.com/'
})

#create the database reference
#database has lock as the root
ref = db.reference('lock')

#program will keep running until stopped by the user
while True:

        #read the input of the arduino which is either correct code or wrong code
	read_serial=ser.readline()

        #Due to problems with formatting just check for the first letter of the serial input
        #as there are only two/three different inputs
	if id(str(read_serial)[0]) == id("C"):
    
                #change the value in the database for the key state
                #1 -> open door, 0 -> closed door
		ref.update({
			'state': 1
		})
	else:
		ref.update({
			'state': 0 
		})
