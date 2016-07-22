package v_go.version10.Chat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import v_go.version10.ApiClasses.ChatApi;
import v_go.version10.ApiClasses.UserApi;
import v_go.version10.Chat.model.ChatMessage;
import v_go.version10.Chat.model.Status;
import v_go.version10.Chat.model.UserType;
import v_go.version10.FragmentClasses.TabC_1;
import v_go.version10.FragmentClasses.TabC_1_new;
import v_go.version10.R;
import v_go.version10.SocketIo.SocketIoHelper;


public class ChatActivity extends AppCompatActivity {

    private ListView chatListView;
    private EditText chatEditText1;
    private ArrayList<ChatMessage> chatMessages;
    private ImageView enterChatView1;
    private ChatListAdapter listAdapter;
    private String other_user_id;
    private String first_name;
    private String last_message = "";
    private Long last_send_time = 0l;
    private boolean isFirstTime = true;
    private boolean mTyping = false;
    private int messageCount = -1;
    private Handler mTypingHandler = new Handler();
    private Socket socket;

    private Bitmap my_avatar_bitmap;
    private Bitmap other_avatar_bitmap;

    private static final int TYPING_TIMER_LENGTH = 600;
    private static final int NUMBER_OF_MESSAGES_TO_LOAD = 50;

    private static String target_user_id;

    public static boolean isUserIdMatched(String user_id){
        if(user_id != null && target_user_id != null){
            return user_id.equals(target_user_id);
        }
        return false;
    }

    // on send
    private EditText.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER) ) {

                // Perform action on key press
                EditText editText = (EditText) v;

                if(v==chatEditText1)
                {
                    // cannot send empty message
                    if(chatEditText1.getText().toString().trim().equals("")) {
                        chatEditText1.setText("");
                        return true;
                    }
                    sendMessage(editText.getText().toString(), UserType.OTHER);
                }

                chatEditText1.setText("");

                return true;
            }
            return false;

        }
    };
    // on send
    private ImageView.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(v==enterChatView1)
            {
                // cannot send empty message
                if(chatEditText1.getText().toString().trim().equals("")) {
                    chatEditText1.setText("");
                    return;
                }
                sendMessage(chatEditText1.getText().toString(), UserType.OTHER);
            }

            chatEditText1.setText("");

        }
    };

    private EditText.OnClickListener editTextOnclickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollToBottomSharp();
                }
            }, 200);
        }
    };

    private final TextWatcher watcher1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (chatEditText1.getText().toString().equals("")) {

            } else {
                enterChatView1.setImageResource(R.drawable.ic_chat_send);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(editable.length()==0){
                enterChatView1.setImageResource(R.drawable.ic_chat_send);
            }else{
                enterChatView1.setImageResource(R.drawable.ic_chat_send_active);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        SocketIoHelper app = (SocketIoHelper) getApplication();
        socket = app.getSocket();

        // enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // setup actionbar title
        String fullName = getIntent().getStringExtra("full_name");
        getSupportActionBar().setTitle(fullName);

        // setup the other user' id
        other_user_id = getIntent().getStringExtra("user_id");
        target_user_id = other_user_id;

        first_name = fullName.substring(0, fullName.trim().indexOf(" "));

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        chatMessages = new ArrayList<>();

        chatListView = (ListView) findViewById(R.id.chat_list_view);

        my_avatar_bitmap = UserApi.loadMyAvatar(getApplicationContext());
        other_avatar_bitmap = UserApi.loadAvatarFromStorage(other_user_id, getApplicationContext());

        listAdapter = new ChatListAdapter(chatMessages, my_avatar_bitmap, other_avatar_bitmap, this);

        chatListView.setAdapter(listAdapter);

        chatEditText1 = (EditText) findViewById(R.id.chat_edit_text1);

        enterChatView1 = (ImageView) findViewById(R.id.enter_chat1);

        chatEditText1.setOnKeyListener(keyListener);

        chatEditText1.setOnClickListener(editTextOnclickListener);

        enterChatView1.setOnClickListener(clickListener);

        chatEditText1.addTextChangedListener(watcher1);

        chatEditText1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollToBottomSharp();
                        }
                    }, 200);
                }
            }
        });

        // load chat history after view created
        ViewTreeObserver vto = findViewById(R.id.chat_layout).getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Put your code here.
                loadChatHistory();

                findViewById(R.id.chat_layout).getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        // create isTyping text change listener
        chatEditText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length() == 0){
                    return;
                }

                if (!socket.connected()){
                    return;
                }

                if (!mTyping) {
                    mTyping = true;
                    socket.emit("is typing", other_user_id);
                }
                mTypingHandler.removeCallbacks(onTypingTimeout);
                mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();


        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject result = ChatApi.ReadAllMessage(other_user_id);
                try{
                    Log.d("DEBUG", result.getString("msg"));
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
        networkThread.start();


        socket.off("delivery confirmation", onDeliveryConfirm);

        // clean up bitmap to free memory
        listAdapter.recycleBitmaps();
        my_avatar_bitmap.recycle();
        other_avatar_bitmap.recycle();
        unbindDrawables(findViewById(R.id.chat_layout));
        System.gc();
    }
    private void unbindDrawables(View view)
    {
        if (view.getBackground() != null)
        {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup && !(view instanceof AdapterView))
        {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
            {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // turn off isTyping listener when fragment is not visible to user
        socket.off("is typing", onTyping);
        socket.off("stop typing", onStopTyping);

        // update contact row when go back
        if(messageCount == chatMessages.size()){
            TabC_1_new.updateContactRowInfo(last_message, last_send_time);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // clear focus & hide keyboard
        chatEditText1.clearFocus();
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        // register broadcastReceiver only for the first time
        if(isFirstTime) {
            isFirstTime = false;

            socket.on("delivery confirmation", onDeliveryConfirm);

            regBroadcastReceiver();
        }

        // enable isTyping listener
        socket.on("is typing", onTyping);
        socket.on("stop typing", onStopTyping);
    }



    private Emitter.Listener onDeliveryConfirm = new Emitter.Listener(){
        @Override
        public void call(final Object... args) {
            Log.d("DEBUG", args[0].toString());
            Log.d("DEBUG", "delivered");
        }
    };
    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String id = args[0].toString();
                    if (id.equals(other_user_id)) {
                        TextView isTyping = (TextView) findViewById(R.id.is_typing);
                        isTyping.setText(first_name + " is typing...");
                        isTyping.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    };
    private Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String id = args[0].toString();
                    if (id.equals(other_user_id)) {
                        TextView isTyping = (TextView) findViewById(R.id.is_typing);
                        isTyping.setVisibility(View.GONE);
                    }
                }
            });
        }
    };

    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;
            mTyping = false;
            socket.emit("stop typing", other_user_id);
        }
    };

    private void regBroadcastReceiver(){

        IntentFilter filter = new IntentFilter();
        filter.addAction("private message");

        LocalBroadcastManager mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String messageText = intent.getStringExtra("message");
                String sender_user_id = intent.getStringExtra("sender_user_id");

                // display message only the sender matches the sender we are talking to
                if (sender_user_id.equals(other_user_id)) {
                    final ChatMessage message = new ChatMessage();
                    message.setMessageStatus(Status.SENT);
                    message.setMessageText(messageText);
                    message.setUserType(UserType.SELF);
                    message.setMessageTime(new Date().getTime());

                    ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(300);

                    chatMessages.add(message);
                    listAdapter.notifyDataSetChanged();
                    scrollToBottom();
                }

            }
        };
        mLocalBroadcastManager.registerReceiver(broadcastReceiver, filter);
    }

    private void loadChatHistory(){
        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {

                final JSONArray jsonArray = ChatApi.GetChatHistory(other_user_id, NUMBER_OF_MESSAGES_TO_LOAD);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject firstObj = jsonArray.getJSONObject(0);
                            if(firstObj.getString("code").matches("1")){

                                int numberOfMsgGot = firstObj.getInt("number_of_message");

                                for(int i=1; i<(1+numberOfMsgGot); i++){

                                    JSONObject messageObj = jsonArray.getJSONObject(i);
                                    String isYours = messageObj.getString("is_your_message"); //you sent -> 1, you received -> 0
                                    String messageText = messageObj.getString("message");
                                    String dateTime = messageObj.getString("date_time");

                                    Long time = Long.parseLong(dateTime);

                                    if(isYours.equals("0")){
                                        final ChatMessage message = new ChatMessage();
                                        message.setMessageStatus(Status.SENT);
                                        message.setMessageText(messageText);
                                        message.setUserType(UserType.SELF);
                                        message.setMessageTime(time);
                                        chatMessages.add(message);
                                    }else {
                                        final ChatMessage message = new ChatMessage();
                                        message.setMessageStatus(Status.SENT);
                                        message.setMessageText(messageText);
                                        message.setUserType(UserType.OTHER);
                                        message.setMessageTime(time);
                                        chatMessages.add(message);
                                    }

                                    listAdapter.notifyDataSetChanged();
                                    scrollToBottomSharp();
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        networkThread.start();


    }

    private void scrollToBottom(){
        chatListView.smoothScrollToPosition(listAdapter.getCount() - 1);
    }

    private void scrollToBottomSharp(){
        chatListView.setSelection(listAdapter.getCount() - 1);
    }

    private void sendMessage(final String messageText, final UserType userType)
    {
        // send a private message with specific user_id to server
        JSONObject otherUser = new JSONObject();
        try {

            otherUser.put("receiver_user_id", other_user_id);
            otherUser.put("message", messageText);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // sent message!
        socket.emit("private message", otherUser);

        if(messageText.trim().length() == 0)
            return;

        final ChatMessage message = new ChatMessage();
        message.setMessageStatus(Status.SENT);
        message.setMessageText(messageText);
        message.setUserType(userType);
        Long time = new Date().getTime();
        message.setMessageTime(time);
        last_send_time = time;

        chatMessages.add(message);

        if(listAdapter!= null)
            listAdapter.notifyDataSetChanged();
        scrollToBottom();

        message.setMessageStatus(Status.SENT);

        messageCount = chatMessages.size();
        last_message = messageText;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            this.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
