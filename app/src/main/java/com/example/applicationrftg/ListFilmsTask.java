package com.example.applicationrftg;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;

// Classe permet de faire un appel HTTP pour récupérer la liste des films
public class ListFilmsTask extends AsyncTask<URL,Integer,String> {
    private volatile ListefilmsActivity screen;  //référence à l'écran

    // Constructeur qui reçoit l'activité afin de pouvoir la mettre à jour après récupération des données
    public ListFilmsTask(ListefilmsActivity s) {
        this.screen = s ;

    }
    // Méthode appelée avant le début de la tâche en arrière-plan
    @Override
    protected void onPreExecute() {

    }
    // Cette méthode s'exécute en arrière-plan pour ne pas bloquer l'interface utilisateur
    @Override
    protected String doInBackground(URL... urls) {
        String sResultatAppel = null;
        URL urlAAppeler = urls[0];
        sResultatAppel = appelerServiceRestHttp(urlAAppeler);
        return sResultatAppel;
    }
    @Override
    protected void onPostExecute(String resultat) {
        screen.mettreAJourActivityApresAppelRest(resultat);


    }
    // Cette méthode effectue la connexion HTTP, envoie la requête et récupère la réponse
    private String appelerServiceRestHttp(URL urlAAppeler ) {
        HttpURLConnection urlConnection = null;
        int responseCode = -1;
        String sResultatAppel = "";
        String jwt ="eyJhbGciOiJIUzI1NiJ9.e30.jg2m4pLbAlZv1h5uPQ6fU38X23g65eXMX8q-SXuIPDg";
        try {

            urlConnection = (HttpURLConnection) urlAAppeler.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Authorization","Bearer " + jwt);
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));

            responseCode = urlConnection.getResponseCode();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            //lecture du résulat de l'appel et alimentation de la chaine de caractères à renvoyer vers l'appelant
            int codeCaractere = -1;
            while ((codeCaractere = in.read()) != -1) {
                sResultatAppel = sResultatAppel + (char) codeCaractere;
            }

            in.close();
        } catch (IOException ioe) {
            Log.d("mydebug", ">>>Pour appelerServiceRestHttp - IOException ioe =" + ioe.toString());
        } catch (Exception e) {
            Log.d("mydebug", ">>>Pour appelerServiceRestHttp - Excep-tion=" + e.toString());
        } finally {
            urlConnection.disconnect();
        }
        return sResultatAppel;
    }


}