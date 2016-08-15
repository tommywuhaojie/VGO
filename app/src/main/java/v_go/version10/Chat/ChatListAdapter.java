package v_go.version10.Chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import v_go.version10.Chat.model.ChatMessage;
import v_go.version10.Chat.model.Status;
import v_go.version10.Chat.model.UserType;
import v_go.version10.R;


public class ChatListAdapter extends BaseAdapter {

    private ArrayList<ChatMessage> chatMessages;
    private Context context;
    private Bitmap my_avatar_bitmap;
    private Bitmap other_avatar_bitmap;

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("HH:mm");

    public ChatListAdapter(ArrayList<ChatMessage> chatMessages, Bitmap my_avatar_bitmap, Bitmap other_avatar_bitmap, Context context) {
        this.chatMessages = chatMessages;
        this.context = context;
        this.my_avatar_bitmap = my_avatar_bitmap;
        this.other_avatar_bitmap = other_avatar_bitmap;
    }


    @Override
    public int getCount() {
        return chatMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return chatMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = null;
        ChatMessage message = chatMessages.get(position);
        ViewHolder1 holder1;
        ViewHolder2 holder2;

        if (message.getUserType() == UserType.SELF) {
            if (convertView == null) {
                v = LayoutInflater.from(context).inflate(R.layout.chat_user1_item, null, false);
                holder1 = new ViewHolder1();

                holder1.avatar = (ImageView) v.findViewById(R.id.avatar);

                holder1.messageTextView = (TextView) v.findViewById(R.id.message_text);
                holder1.timeTextView = (TextView) v.findViewById(R.id.time_text);

                v.setTag(holder1);
            } else {
                v = convertView;
                holder1 = (ViewHolder1) v.getTag();

            }
            if(other_avatar_bitmap != null) {
                holder1.avatar.setImageBitmap(other_avatar_bitmap);
            }

            holder1.messageTextView.setText(message.getMessageText());
            holder1.timeTextView.setText(SIMPLE_DATE_FORMAT.format(message.getMessageTime()));

        } else if (message.getUserType() == UserType.OTHER) {

            if (convertView == null) {
                v = LayoutInflater.from(context).inflate(R.layout.chat_user2_item, null, false);

                holder2 = new ViewHolder2();

                holder2.avatar = (ImageView) v.findViewById(R.id.avatar);

                holder2.messageTextView = (TextView) v.findViewById(R.id.message_text);
                holder2.timeTextView = (TextView) v.findViewById(R.id.time_text);
                holder2.messageStatus = (ImageView) v.findViewById(R.id.user_reply_status);
                v.setTag(holder2);

            } else {
                v = convertView;
                holder2 = (ViewHolder2) v.getTag();

            }

            if(my_avatar_bitmap != null) {
                holder2.avatar.setImageBitmap(my_avatar_bitmap);
            }
            holder2.messageTextView.setText(message.getMessageText());
            holder2.timeTextView.setText(SIMPLE_DATE_FORMAT.format(message.getMessageTime()));

            if (message.getMessageStatus() == Status.DELIVERED) {
                holder2.messageStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_double_tick));
            } else if (message.getMessageStatus() == Status.SENT) {
                holder2.messageStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_single_tick));
            } else if(message.getMessageStatus() == Status.PENDING){
                holder2.messageStatus.setImageDrawable(null);
            }
        }
        return v;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = chatMessages.get(position);
        return message.getUserType().ordinal();
    }

    private class ViewHolder1 {
        public TextView messageTextView;
        public TextView timeTextView;
        public ImageView avatar;
    }

    private class ViewHolder2 {
        public ImageView messageStatus;
        public ImageView avatar;
        public TextView messageTextView;
        public TextView timeTextView;

    }
    public void recycleBitmaps(){
        if (my_avatar_bitmap != null) {
            if(!my_avatar_bitmap.isRecycled()) {
                my_avatar_bitmap.recycle();
                my_avatar_bitmap = null;
            }
        }
        if (other_avatar_bitmap != null) {
            if(!other_avatar_bitmap.isRecycled()) {
                other_avatar_bitmap.recycle();
                other_avatar_bitmap = null;
            }
        }
    }
}
