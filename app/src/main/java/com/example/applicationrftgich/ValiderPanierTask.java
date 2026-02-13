package com.example.applicationrftgich;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * AsyncTask pour valider le panier via l'API
 * Appelle POST /cart/checkout
 */
@SuppressWarnings("deprecation")
public class ValiderPanierTask extends AsyncTask<Integer, Integer, String> {

    private volatile PanierActivity screen;

    public ValiderPanierTask(PanierActivity s) {
        this.screen = s;
    }

    @Override
    protected void onPreExecute() {
        Log.d("mydebug", "Validation du panier...");
    }

    @Override
    protected String doInBackground(Integer... params) {
        int customerId = params[0];

        try {
            URL url = new URL(UrlManager.getURLConnexion() + "/cart/checkout");
            return appelerServiceRestHttp(url, customerId);
        } catch (Exception e) {
            Log.e("mydebug", "Erreur validation: " + e.toString());
            return "ERREUR";
        }
    }

    @Override
    protected void onPostExecute(String resultat) {
        screen.onPanierValide(resultat);
    }

    /**
     * Appel HTTP POST pour valider le panier
     */
    private String appelerServiceRestHttp(URL urlAAppeler, int customerId) {
        HttpURLConnection urlConnection = null;
        String sResultatAppel = "";
        String jwt = screen.getString(R.string.jwt_token);

        try {
            Log.d("mydebug", "URL appelée : " + urlAAppeler.toString());

            urlConnection = (HttpURLConnection) urlAAppeler.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + jwt);
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            urlConnection.setDoOutput(true);

            // Créer le JSON avec customerId
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("customerId", customerId);

            Log.d("mydebug", "JSON envoyé : " + jsonParam.toString());

            // Envoyer le JSON
            OutputStream os = urlConnection.getOutputStream();
            os.write(jsonParam.toString().getBytes("UTF-8"));
            os.close();

            int responseCode = urlConnection.getResponseCode();
            Log.d("mydebug", "Response code : " + responseCode);

            if (responseCode == 200) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                int codeCaractere = -1;
                while ((codeCaractere = in.read()) != -1) {
                    sResultatAppel = sResultatAppel + (char) codeCaractere;
                }
                in.close();

                Log.d("mydebug", "Réponse reçue : " + sResultatAppel);
                return "OK";
            }

        } catch (Exception e) {
            Log.d("mydebug", ">>>Exception : " + e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return "ERREUR";
    }
}