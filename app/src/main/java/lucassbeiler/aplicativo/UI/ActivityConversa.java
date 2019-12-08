//package lucassbeiler.aplicativo.UI;
//
//import android.os.Bundle;
//import androidx.appcompat.app.AppCompatActivity;
//import android.view.WindowManager;
//
//import lucassbeiler.aplicativo.R;
//
//
//public class ActivityConversa extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
//        setContentView(R.layout.activity_conversa);
//    }
//}

package lucassbeiler.aplicativo.UI;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import lucassbeiler.aplicativo.R;
import lucassbeiler.aplicativo.adapter.AdapterMensagem;
import lucassbeiler.aplicativo.models.Message;
import lucassbeiler.aplicativo.utilitarias.ClasseApplication;

public class ActivityConversa extends AppCompatActivity{
    private RecyclerView mMessagesView;
    private EditText mInputMessageView;
    private List<Message> mMessages = new ArrayList<Message>();
    private RecyclerView.Adapter mAdapter;
    private Socket mSocket;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);
        ClasseApplication app = (ClasseApplication) getApplication();
        mSocket = app.getSocket();
        mSocket.on("receiveMessage", onNewMessage);
        mSocket.connect();

        final String idMatch = getIntent().getStringExtra("idMatch");
        mAdapter = new AdapterMensagem(this, mMessages);


        mMessagesView = (RecyclerView) findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(this));
        mMessagesView.setAdapter(mAdapter);

        mInputMessageView = (EditText) findViewById(R.id.message_input);
        mInputMessageView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                if (id == R.id.send || id == EditorInfo.IME_NULL) {
                    attemptSend(idMatch);
                    return true;
                }
                return false;
            }
        });

        ImageButton sendButton = (ImageButton) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSend(idMatch);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();

    }

    private void addMessage(String username, String message) {
        mMessages.add(new Message.Builder(Message.TYPE_MESSAGE)
                .username(username).message(message).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
    }


    private void attemptSend(String idMatch) {
        try{
            if(!mSocket.connected()) return;

            String mensagem = mInputMessageView.getText().toString().trim();
            if(TextUtils.isEmpty(mensagem)){
                mInputMessageView.requestFocus();
                return;
            }

            JSONObject jSONO = new JSONObject();
            jSONO.put("message", mensagem);
            jSONO.put("id", Integer.valueOf(idMatch));
            mInputMessageView.setText("");
            addMessage("", mensagem);

            mSocket.emit("sendMessage", jSONO.toString());
        }catch(JSONException jex){
            //
        }
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = new JSONObject(args[0].toString());
                        String message;
                        message = data.getString("message");
                        addMessage(".", message);
                    } catch (JSONException e) {
                        //
                    }
                }
            });
        }
    };
}


