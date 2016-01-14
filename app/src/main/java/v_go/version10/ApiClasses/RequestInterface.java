package v_go.version10.ApiClasses;

import org.json.JSONArray;

public interface RequestInterface {
	String TripRequest(String trip_id, String reg_as);
	/*
    *
    Description: user send a request for joining an existing trip. Also send notification to the phone.

    Input:
        $_POST
            'trip_id'		(int) trip_id request to join
            'reg_as'		(int) request to join as: 1->driver ; 0->passanger

    Output:
        1						sucessful

    Error:
        0						failed
        -1						no session
        -3						can not sent request to a trip is create by the same user
        -4						no such trip exist/ trip_id and recive_id not match
        -5						request already exist
        -7						required field not set
    * */
	String RequestResponse(String request_id, String state);
	/*
    * Description: User response to a trip request sent by other users;

    Input:
        $_Get
            'request_id'
            'state'				1 -> Accept the request
                                0 -> Denied the request

    Output:
        1 					Successful

    Error:
        0						failed
        -1						no session
        -3						no right to accept or denied request
        -7						required field not set
    * */
	JSONArray Notification(String last_id);
    /*
    * Description:In the next 10 seconds. if session user recived any trip request for other user,
				instantly repsonse the session with a message containing trip information and the other usr information.
				Otherwise, wait and not reply with any response
Path:	(hong decide)
Input:
	$_Get
		['request_id']		last request id that received before.
Output:
	if there is a request:
		Json object:
			'request_id'
			'trip_id'
			'start_location'	starting location of the session user's trip
			'date_id'           same format as the trip date_id
			'end_location'		ending location of the session user's trip
			'received_flag'		0: recived requests 1:sent requests
			'name'				the full name of the user who sent the request
			'other_id'
			'reg_as'			if its sent to you as a request to join trip
			'accept'			if its sent to noticity you about the decition they made

Error:
		-5 request not set
		-7 input filed not set
    * */
}
