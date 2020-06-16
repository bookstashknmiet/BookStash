package com.blogspot.zone4apk.bookstash;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.blogspot.zone4apk.bookstash.recyclerViewChatSupport.Chat;
import com.blogspot.zone4apk.bookstash.recyclerViewChatSupport.ChatViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ChatSupportActivity extends AppCompatActivity {

    private static final String TAG = "UpdateChatMessage";
    FirebaseRecyclerAdapter adapter;
    FirebaseAuth mAuth;
    private String userKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_support);

        //Initializing auth
        mAuth = FirebaseAuth.getInstance();

        //Setting recyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerview_chat_support);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("ChatSupport");
        reference.keepSynced(true);
        Query query = reference.child(mAuth.getCurrentUser().getUid()).limitToLast(50).orderByChild("timestamp");
        FirebaseRecyclerOptions<Chat> options = new FirebaseRecyclerOptions.Builder<Chat>().setQuery(query, Chat.class).build();
        adapter = new FirebaseRecyclerAdapter<Chat, ChatViewHolder>(options) {
            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
                return new ChatViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull Chat model) {
                holder.setChatMessage(model.getMessage());
                holder.setChatSender(model.getSender(), getApplicationContext());
                holder.setChatTimeStamp(model.getTimestamp());
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null)
            userKey = mAuth.getCurrentUser().getUid();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void msendChatMessage(View view) {
        //if user is logged in
        if (userKey != null) {
            final EditText editTextChatMessage = findViewById(R.id.editText_chat_message);
            String chatMessage = editTextChatMessage.getText().toString();
            //Adding chat to DataBase
            //Checking if the details are valid or not
            if (TextUtils.isEmpty(chatMessage))
                //if user have not entered anything in the editTextChatMessage
                return;

            String currentTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime());


            DatabaseReference mChatDatabase = FirebaseDatabase.getInstance().getReference().child("ChatSupport").child(userKey);
            //creating Hashmap
            HashMap<String, String> chat = new HashMap<String, String>();
            chat.put("message", chatMessage);
            chat.put("sender", "other");
            chat.put("timestamp", currentTime);
            mChatDatabase.push().setValue(chat).addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "updateMessage:success");
                        editTextChatMessage.clearFocus();
                        editTextChatMessage.getText().clear();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "updateMessage:failure", task.getException());
                        Toast.makeText(getApplicationContext(), "Please try again later.",
                                Toast.LENGTH_SHORT).show();
                    }
                }

            });

        }
    }
}
