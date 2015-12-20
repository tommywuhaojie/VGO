package v_go.version10.HelperClasses;
import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TouchableWrapper extends FrameLayout {

    public TouchableWrapper(Context context) {
        super(context);
    }

    public void setTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    private OnTouchListener onTouchListener;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onTouchListener.onTouch();
                break;
            case MotionEvent.ACTION_UP:
                onTouchListener.onRelease();
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchListener.onDrag();
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    public interface OnTouchListener {
        void onTouch();
        void onRelease();
        void onDrag();
    }
}
