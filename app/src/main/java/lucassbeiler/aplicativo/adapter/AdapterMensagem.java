package lucassbeiler.aplicativo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.library.bubbleview.BubbleTextView;

import java.util.List;

import lucassbeiler.aplicativo.R;
import lucassbeiler.aplicativo.models.Message;


public class AdapterMensagem extends RecyclerView.Adapter<AdapterMensagem.ViewHolder> {

    private List<Message> mMessages;

    public AdapterMensagem(Context context, List<Message> messages) {
        mMessages = messages;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Message message = mMessages.get(position);
        viewHolder.setMessage(message.getMessage());
        viewHolder.setUsername(message.getUsername());
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mMessages.get(position).getType();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mUsernameView;
        private BubbleTextView mMessageView;

        public ViewHolder(View itemView) {
            super(itemView);

            mMessageView = (BubbleTextView) itemView.findViewById(R.id.message);
        }

        public void setUsername(String username) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                    mMessageView.getLayoutParams();
            if(username.isEmpty()){
                params.addRule(RelativeLayout.ALIGN_PARENT_END);
            }else{
                params.addRule(RelativeLayout.ALIGN_PARENT_START);
            }
            mMessageView.setLayoutParams(params);
            }

        public void setMessage(String message) {
            if (null == mMessageView) return;
            mMessageView.setText(message);
        }

    }
}
