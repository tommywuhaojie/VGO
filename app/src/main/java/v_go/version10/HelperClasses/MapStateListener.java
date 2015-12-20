package v_go.version10.HelperClasses;

public abstract class MapStateListener {

    private boolean mMapTouched = false;

    public MapStateListener(TouchableMapFragment touchableMapFragment) {

        touchableMapFragment.setTouchListener(new TouchableWrapper.OnTouchListener() {
            @Override
            public void onTouch() {
                touchMap();
            }
            @Override
            public void onRelease() {
                releaseMap();
            }
            @Override
            public void onDrag() {
                dragMap();
            }
        });
    }
    private synchronized void releaseMap() {
        if(mMapTouched) {
            mMapTouched = false;
            onMapReleased();
        }
    }
    private void touchMap() {
        if(!mMapTouched) {
            mMapTouched = true;
            onMapTouched();
        }
    }
    private void dragMap(){
        onMapDragged();
    }
    public abstract void onMapTouched();
    public abstract void onMapReleased();
    public abstract void onMapDragged();
}
