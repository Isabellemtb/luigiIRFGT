package com.example.applicationrftg;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * AsyncTask pour vérifier la disponibilité d'un film
 * Appelle GET /cart/available/{filmId}
 */
@SuppressWarnings("deprecation")
public class CheckDisponibiliteTask extends AsyncTask<Integer, Integer, Boolean> {

    private volatile PanierActivity screen;
    private int position;

    public CheckDisponibiliteTask(PanierActivity s, int position) {
        this.screen = s;
        this.position = position;
    }

    @Override
    protected void onPreExecute() {
        Log.d("mydebug", "Vérification disponibilité...");
    }

    @Override
    protected Boolean doInBackground(Integer... params) {
        int filmId = params[0];

        try {
            URL url = new URL(UrlManager.getURLConnexion() + "/cart/available/" + filmId);
            return appelerServiceRestHttp(url);
        } catch (Exception e) {
            Log.e("mydebug", "Erreur disponibilité: " + e.toString());
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean disponible) {
        screen.onDisponibiliteVerifiee(position, disponible);
    }

    /**
     * Appel HTTP GET pour vérifier la disponibilité
     */
    private Boolean appelerServiceRestHttp(URL urlAAppeler) {
        HttpURLConnection urlConnection = null;
        String sResultatAppel = "";
        String jwt = "eyJhbGciOiJIUzI1NiJ9.e30.jg2m4pLbAlZv1h5uPQ6fU38X23g65eXMX8q-SXuIPDg";

        try {
            Log.d("mydebug", "URL appelée : " + urlAAppeler.toString());

            urlConnection = (HttpURLConnection) urlAAppeler.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + jwt);
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));

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

                // Si le tableau est vide [] = indisponible, sinon disponible
                return !sResultatAppel.trim().equals("[]");
            }

        } catch (Exception e) {
            Log.d("mydebug", ">>>Exception : " + e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return false;
    }
}
