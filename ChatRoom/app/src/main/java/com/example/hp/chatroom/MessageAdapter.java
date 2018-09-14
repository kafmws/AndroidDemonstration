package com.example.hp.chatroom;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout leftLayout;
        private LinearLayout rightLayout;
        private TextView leftMeg;
        private TextView rightMeg;

        public ViewHolder(View itemView) {
            super(itemView);
            leftLayout = itemView.findViewById(R.id.left_layout);
            rightLayout = itemView.findViewById(R.id.ringht_layout);
            leftMeg = itemView.findViewById(R.id.left_message);
            rightMeg = itemView.findViewById(R.id.right_message);
        }
    }

//private static int cnt = 1;
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.msg_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
//        holder.setIsRecyclable(false);    //禁止holder复用
        //Log.d("onCreate",String.valueOf(cnt++));
        return holder;
    }

   // private static int count = 1;
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if(message.getType() == 0){//   0  Received
            holder.rightMeg.setText(message.getMessage());
            //holder.leftMeg.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.GONE);
        }else{//   1  Sent
            holder.leftMeg.setText(message.getMessage());
            //holder.rightMeg.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.GONE);
        }
       // Log.d("onBind",String.valueOf(count++));
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


    @Override public int getItemViewType(int position) { return position; }

}
