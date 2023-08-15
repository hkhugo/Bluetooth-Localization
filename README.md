# Bluetooth-based Localization System
A student attendance records mobile application to automatically locate the student's position. The application uses an enhanced trilateration algorithm, Random Forest algorithm to improve the accuracy of Bluetooth localization.  

## Project Methodology   
---------------------------------------------------------------
### Received Signal Strength Indicator
The indoor position can be determined by utilizing the received signal strength (RSS), which involves estimating the distance between a transmitter (Tx) and a receiver (Rx) device based on the number of signal power strengths received by the receiver. The relative value of RSS is represented by the Received Signal Strength Indicator (RSSI), which is usually measured in decibel-milliwatts (dBm) or milliwatts (mW). 

### Trilateration Algorithm
The RSSI only estimate the absolute distance between a transmitter (Tx) and a receiver (Rx) device, but it cannot determine the precise location of the device based solely on this information. To address this issue, the Trilateration Algorithm is utilized. This algorithm can combine the data of the absolute distance as well as the three positions of the transmitter to determine the position of the receiver device. To achieve the algorithm, at least three RN Standard stations are required. 

### Random Forest and Bluetooth fingerprint-based indoor location recognition 
The random forest algorithm as an ensemble learning method for classification and regression analysis. It involves the random selection of data from the dataset, and the resultant predictions are combined to form a prediction model. The process of creating a decision tree involves partitioning the dataset into learning and evaluation data, where the former is used to build the tree, and the latter is used to evaluate its performance. Furthermore, the bootstrap method is applied to resample and extract data during the partitioning process when utilizing the random forest technique.

## App Function 
---------------------------------------------------------------
### Algorithm calculator 
This function allows the user to get the accurate position calculated from different Algorithm by inputting the needed value and show the result in the map

### Take Attendance System 
This function allows the user to take attendance with getting position from different Algorithm and show the result in the map 

### Beacon List  
This function allows the user to detect the beacon information after the device is in the beacon detection range. 

## Usage of software and programming tool for development 
---------------------------------------------------------------

### Android Studio 

Integrated Development Environment for developing Android application 

### Android-Proximity-SDK 

Library for signal-processing technology powered by Estimote Monitoring 

### Estimote 

Android mobile application for setting Beacon’s Environment Variables 

### Estimote Cloud 

A platform for beacon management and API handler 

## Usage of hardware for development 
---------------------------------------------------------------

### Estimote proximity beacons 

Development kid to provide Bluetooth connection with the devices 

### Development computer 

Development tool for compile development software or tool 

### Android mobile phone 

Including Bluetooth function to connected with Estimote proximity beacons 

##  The random forest algorithm implementation
---------------------------------------------------------------
In order to achieve the algorithm, a docker application is needed to build, for details pls refer to:

[Random-forest-algorithm-python-flask](https://github.com/hkhugo/Random-forest-algorithm-python-flask.git)  
