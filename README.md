# Taxi Service

## Ride booking system for a day 
Author: Choi Ian Leong, Taylor

***About This Application*** \
The application provides a ride booking system for customers within 5 zones. 
The bookings can only be made on the same day and fees are charged according to the distance between zones. 
Distance and duration of ride is determined by the difference of zone number.
A ride within one zone takes 1 hour, ride between zones takes (1 + difference) hours. 
For example: duration from zone 4 to zone 4 is 1 and distance between is 0, duration from zone 5 to zone 3 is 3 and distance between is 2.
Bookings can only be cancelled if the driver is already in the starting zone. 
In other words, if the user is booking drivers from another zone to drive him/her, that ride cannot be cancelled.
This application is provided for all individuals living within the 10 zones. 
Zones are not specified to any location in real life in this application just to make the application portable.
Meaning that the application is applicable to any places that can be divided into 5 zones. 

This application is different from other ride service, customers get to choose the drivers on their own according to the ranking of drivers.
If there's no drivers available in the zone, customer can choose to book again later or choose a driver from another zone. 
However, choosing drivers from other zone will cost additional fee and cannot be cancelled. 

***Interest*** \
I'm interested in making this taxi service application because we don't get to choose the drivers in most of the ride booking system nowadays.
Sometimes, we may be matched with relatively worse drivers, the driver could be bad in driving or in attitude.
And I understand as a customer, I don't want to pay for an unpleasant ride, it's better to choose the drivers we want.
This system can also force the drivers to improve their skills or attitude, which makes unpleasant rides avoidable.


## user stories
As a user, I want to be able to book a ride on the day.\
As a user, I want to be able to book multiple rides on the day.\
As a user, I want to be able to cancel the booking(s) of my ride(s).\
As a user, I want to be able to give a review to my ride(s).\
As a user, I want to be able to choose my own choice of driver.\
As a user, I want to be able to look at the price table of the service.\


