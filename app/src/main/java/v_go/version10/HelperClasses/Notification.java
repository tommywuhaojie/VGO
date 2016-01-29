package v_go.version10.HelperClasses;

public class Notification {

    private int req_id;
    private int trip_id;
    private int type;
    /** int type:
     * 0 -> request as passenger
     * 1 -> request as driver
     * 2 -> respond as rejected
     * 3 -> respond as accepted
     * 4 -> respond in pending
     */
    private int receive_flag;
    /** receive_flag:
     * 0 -> receiver of the request
     * 1 -> sender of the request
     */

    private String start_loc;
    private String end_loc;
    private String name;

    public Notification(int req_id, int trip_id, int type, int receive_flag, String start_loc, String end_loc, String name){
        this.req_id = req_id;
        this.trip_id = trip_id;
        this.type = type;
        this.receive_flag = receive_flag;
        this.start_loc = start_loc;
        this.end_loc = end_loc;
        this.name = name;
    }

    public int getRequestId(){
        return req_id;
    }
    public int getTripId(){
        return trip_id;
    }
    public int getType(){
        return type;
    }
    public int getRecFlag() {
        return receive_flag;
    }
    public String getStartLocation(){
        return start_loc;
    }
    public String getEndLocation(){
        return end_loc;
    }
    public String getFirstName(){
        return name;
    }
}
