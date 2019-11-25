package lucassbeiler.aplicativo;

import android.util.Log;
import org.json.JSONObject;

public class FuncoesJSON {
    protected JSONObject geraJSON(String chaves[], String valores[]){
        JSONObject jSONO = new JSONObject();
        try {
            for(int i = 0; i < chaves.length; i++) {
                if(!valores[i].isEmpty())
                jSONO.put(chaves[i], valores[i]);
            }
        } catch (Exception e) {
            Log.d("ERRO", e.toString());
        }
        return jSONO;
    }
}
