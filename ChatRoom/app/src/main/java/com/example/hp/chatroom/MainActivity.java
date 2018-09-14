package com.example.hp.chatroom;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Message> messageList = new ArrayList<>();

    private void initializeMessage(){
        messageList.add(new Message("hello",0));
        messageList.add(new Message("hello,who is that ?",1));
        messageList.add(new Message("I'm kafm.Nice to meet you.",0));
    }

    private Button btn_send;
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_chatRoom);
        initializeMessage();
        final MessageAdapter adapter = new MessageAdapter(messageList);
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        btn_send = (Button) findViewById(R.id.btn_send);
        editText = (EditText) findViewById(R.id.input_text);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editText.getText().toString();
                if(message!=null){
                    messageList.add(new Message(message,1));
                    //adapter.notifyItemInserted(messageList.size());
                    adapter.notifyItemInserted(messageList.size()-1);
                }
                editText.setText("");
                recyclerView.smoothScrollToPosition(messageList.size());
                //recyclerView.scrollToPosition(messageList.size());
            }
        });
    }


}
