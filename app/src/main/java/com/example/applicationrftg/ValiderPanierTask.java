package com.example.applicationrftg;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * AsyncTask pour valider le panier en appelant l'API
 *
 * Pour chaque film du panier, fait un appel POST /commanderFilm
 */
@SuppressWarnings("deprecation")
public class ValiderPanierTask extends AsyncTask<ArrayList<Integer>, Integer, String> {

    private volatile PanierActivity screen;  // Référence à l'écran
    private ArrayList<Integer> filmIds;      // Liste des IDs de films à commander

    // Constructeur qui reçoit l'activité
    public ValiderPanierTask(PanierActivity s) {
        this.screen = s;
    }

    // Méthode appelée avant le début de la tâche en arrière-plan
    @Override
    protected void onPreExecute() {
        Log.d("mydebug", "Début de la validation du panier");
    }

    // Cette méthode s'exécute en arrière-plan
    @Override
    protected String doInBackground(ArrayList<Integer>... params) {
        filmIds = params[0];
        StringBuilder resultat = new StringBuilder();

        Log.d("mydebug", "Validation de " + filmIds.size() + " film(s)");

        // Pour chaque film, faire un appel API
        for (int filmId : filmIds) {
            try {
                // Construire l'URL avec le paramètre film_id
                URL url = new URL("http://10.0.2.2:8180/commanderFilm?film_id=" + filmId);
                String response = appelerServiceRestHttp(url);
                resultat.append(response).append("\n");

                Log.d("mydebug", "Film " + filmId + " commandé");

            } catch (Exception e) {
                Log.e("mydebug", "Erreur lors de la commande du film " + filmId + ": " + e.toString());
                return "ERREUR";
            }
        }

        return "OK";
    }

    @Override
    protected void onPostExecute(String resultat) {
        // Appeler la méthode de l'activity pour gérer le résultat
        screen.onPanierValide(resultat);
    }

    /**
     * Méthode pour appeler le service REST HTTP
     */
    private String appelerServiceRestHttp(URL urlAAppeler) {
        HttpURLConnection urlConnection = null;
        int responseCode = -1;
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

            responseCode = urlConnection.getResponseCode();
            Log.d("mydebug", "Response code : " + responseCode);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            // Lecture du résultat de l'appel et alimentation de la chaîne de caractères
            int codeCaractere = -1;
            while ((codeCaractere = in.read()) != -1) {
                sResultatAppel = sResultatAppel + (char) codeCaractere;
            }
            in.close();

            Log.d("mydebug", "Réponse reçue : " + sResultatAppel);

        } catch (IOException ioe) {
            Log.d("mydebug", ">>>Pour appelerServiceRestHttp - IOException ioe =" + ioe.toString());
        } catch (Exception e) {
            Log.d("mydebug", ">>>Pour appelerServiceRestHttp - Exception=" + e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return sResultatAppel;
    }
}