package com.example.applicationrftg;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * AsyncTask pour ajouter un film au panier via l'API
 * Appelle POST /cart/add et crée un Rental avec status = 3
 */
@SuppressWarnings("deprecation")
public class AjouterAuPanierTask extends AsyncTask<Integer, Integer, String> {

    private volatile DetailfilmActivity screen;

    public AjouterAuPanierTask(DetailfilmActivity s) {
        this.screen = s;
    }

    @Override
    protected void onPreExecute() {
        Log.d("mydebug", "Ajout au panier via API...");
    }

    @Override
    protected String doInBackground(Integer... params) {
        int customerId = params[0];
        int filmId = params[1];

        try {
            URL url = new URL(UrlManager.getURLConnexion() + "/cart/add");
            return appelerServiceRestHttp(url, customerId, filmId);
        } catch (Exception e) {
            Log.e("mydebug", "Erreur ajout panier: " + e.toString());
            return "ERREUR";
        }
    }

    @Override
    protected void onPostExecute(String resultat) {
        screen.onFilmAjouteAuPanier(resultat);
    }

    /**
     * Appel HTTP POST pour ajouter au panier
     */
    private String appelerServiceRestHttp(URL urlAAppeler, int customerId, int filmId) {
        HttpURLConnection urlConnection = null;
        String sResultatAppel = "";
        String jwt = "eyJhbGciOiJIUzI1NiJ9.e30.jg2m4pLbAlZv1h5uPQ6fU38X23g65eXMX8q-SXuIPDg";

        try {
            Log.d("mydebug", "URL appelée : " + urlAAppeler.toString());

            urlConnection = (HttpURLConnection) urlAAppeler.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + jwt);
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            urlConnection.setDoOutput(true);

            // Créer le JSON avec customerId et filmId
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("customerId", customerId);
            jsonParam.put("filmId", filmId);

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
            } else if (responseCode == 404) {
                return "INDISPONIBLE";
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
