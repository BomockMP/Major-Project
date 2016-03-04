Instructions for libraries: 

Make sure you have the Kinect SDK installed if you want to use Kinect2. 

To use the StructureIO scanner, you will need to install OpenNI and configure the library like so:

1. Download and install openni from here: 

http://structure.io/openni

2. Change the USB settings in this file: C:\Program Files\OpenNI2\Redist\OpenNI2\Drivers\PS1080.ini so that you have:

; USB interface to be used. 0 - FW Default, 1 - ISO endpoints (default on Windows), 2 - BULK endpoints (default on Linux/Mac/Android machines), 3 - ISO endpoints for low-bandwidth depth
UsbInterface=0

(Note how there is no ; in front of UsbInterface=0)

3. Right click the slow robotics project and select Configure Build Path. Update the userlibrary for the openNi.jar in C:\Program Files\OpenNI2\Redist\

4. Go into Run-> Run Configurations and add the following to the VM settings

-Djava.library.path=C:\Progra~1\OpenNI2\Redist\