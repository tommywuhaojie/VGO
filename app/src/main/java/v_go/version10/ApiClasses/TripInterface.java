package v_go.version10.ApiClasses;

import org.json.JSONArray;

/**
 * Created by michael on 16-01-08.
 */
public interface TripInterface {
	String getTrip_id();

    String getUser_id();

    String getStarting_date();

    String getStarting_time();

    String getStarting();

    String getDestination();

    String getEst_time();

    String getEst_distance();

    String getDriver_flag();

    String getUpdate();

    double getStart_lat();

    double getStart_lng();

    double getEnd_lat();

    double getEnd_lng();

    String RegisterTrip(String time, double start_latitude, double start_longitude, String start_location,
                        double end_latitude, double end_longitude, String end_location, int estimate_time,
                        double estimate_distance, int type);

	/*
        * Description: return a speicfy month's trips;
        * Input:
        *       yearmonth:          Fortmat:YYYY-MM
        * output
        *       Json array          Format:
        *           ['trip_id']			trip id
                    ['starting_date']	starting date of the trip; 	format YYYY-MM-DD;
                    ['starting_time']	starting time of the trip;	format HH:mm;
                    ['starting']		starting location of the trip (english)
                    ['destination']		destination of the trip (english)
                    ['est_time']		estimate travel time;
                    ['est_distance']	estimate distance;
                    ['driver_flag']		if the session user is a driver or a passanger in this trip; 1: driver; 0: passanger;
                    ['match_flag']		if this trip is already matched with other user. 1: yes; 0: no;(not implement yet)
        * */
    JSONArray ThisMonthTrip(String yearmonth);

	/*
        * 'start_lat'			(double)starting latitude
            'start_lng'			(double)starting longtitude
            'end_lat'			(double)ending latitude
            'end_lng'			(double)ending longtitude
            'time'				(string)format: YYYY-MM-DD HH:mm  starting time
            'register_as'		(int)
                                0 -> passanger
                                1 -> driver
            'multi_allow'		(int)allow multiple passangers;
                                0 -> no;
                                1 -> yes;
        Output:
            Json array:
            'trip_id'
            'start_location'	(string)
            'end_location'		(string)
            'time'				(string) format: YYYY-MM-DD HH:mm
            'start_dif'			(double) starting location distance difference unit km
            'end_dif'			(double) ending location  distance difference unit km
            'register_as' 		(int)
                                0 -> passanger
                                1 -> driver
        Error:
            ouput null pointer
        * */
    JSONArray MatchTripsBy(double start_lat, double start_lng, double end_lat, double end_lng, String start_time, int reg_as, int mult_allow);
}
