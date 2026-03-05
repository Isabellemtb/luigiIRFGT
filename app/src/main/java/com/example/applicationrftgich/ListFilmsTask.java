package com.example.applicationrftgich;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// Classe permet de faire un appel HTTP pour récupérer la liste des films
public class ListFilmsTask extends AsyncTask<URL, Integer, String> {
    private volatile ListefilmsActivity screen;

    public ListFilmsTask(ListefilmsActivity s) {
        this.screen = s;
    }

    @Override
    protected void onPreExecute() {
        screen.afficherChargement(true);
    }

    @Override
    protected String doInBackground(URL... urls) {
        URL urlAAppeler = urls[0];
        return appelerServiceRestHttp(urlAAppeler);
    }

    @Override
    protected void onPostExecute(String resultat) {
        screen.afficherChargement(false);
        screen.mettreAJourActivityApresAppelRest(resultat);
    }

    private String appelerServiceRestHttp(URL urlAAppeler) {
        HttpURLConnection urlConnection = null;
        int responseCode = -1;
        String sResultatAppel = "";
        String jwt = screen.getString(R.string.jwt_token);
        try {
            urlConnection = (HttpURLConnection) urlAAppeler.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + jwt);
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));

            responseCode = urlConnection.getResponseCode();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            int codeCaractere = -1;
            while ((codeCaractere = in.read()) != -1) {
                sResultatAppel = sResultatAppel + (char) codeCaractere;
            }
            in.close();
        } catch (IOException ioe) {
            Log.d("mydebug", ">>>Pour appelerServiceRestHttp - IOException ioe =" + ioe.toString());
        } catch (Exception e) {
            Log.d("mydebug", ">>>Pour appelerServiceRestHttp - Exception=" + e.toString());
        } finally {
            urlConnection.disconnect();
        }
        return sResultatAppel;
    }
}