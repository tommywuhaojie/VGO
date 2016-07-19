
package v_go.version10.Chat.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.widget.RelativeLayout;

public class SizeNotifierRelativeLayout extends RelativeLayout {

    private Rect rect = new Rect();
    public SizeNotifierRelativeLayoutDelegate delegate;

    public abstract interface SizeNotifierRelativeLayoutDelegate {
        public abstract void onSizeChanged(int keyboardHeight);
    }

    public SizeNotifierRelativeLayout(Context context) {
        super(context);
    }

    public SizeNotifierRelativeLayout(Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    public SizeNotifierRelativeLayout(Context context, android.util.AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
