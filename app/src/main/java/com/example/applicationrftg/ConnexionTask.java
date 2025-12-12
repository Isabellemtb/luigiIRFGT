package com.example.applicationrftg;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.*;

/**
 * AsyncTask pour la connexion via l'API /customers/verify
 */
@SuppressWarnings("deprecation")
public class ConnexionTask extends AsyncTask<String, Integer, String> {

    private volatile MainActivity screen;

    public ConnexionTask(MainActivity s) {
        this.screen = s;
    }

    @Override
    protected void onPreExecute() {
        Log.d("mydebug", "Début de la connexion");
    }

    @Override
    protected String doInBackground(String... params) {
        String email = params[0];
        String password = params[1];

        try {
            URL url = new URL("http://10.0.2.2:8180/customers/verify");
            return appelerServiceRestHttp(url, email, password);
        } catch (Exception e) {
            Log.e("mydebug", "Erreur connexion: " + e.toString());
            return "{\"customerId\": -1}";
        }
    }

    @Override
    protected void onPostExecute(String resultat) {
        screen.onConnexionTerminee(resultat);
    }

    /**
     * Appel HTTP POST pour vérifier les identifiants
     */
    private String appelerServiceRestHttp(URL urlAAppeler, String email, String password) {
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
            urlConnection.setDoOutput(true);

            // Créer le JSON avec email et password crypté en MD5
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("email", email);
            String passwordCrypte = encypterChaineMD5(password);
            jsonParam.put("password", passwordCrypte);

            // LOG POUR VOIR LE MOT DE PASSE CRYPTÉ
            //Log.d("mydebug", "========================================");
            //Log.d("mydebug", "EMAIL : " + email);
            //Log.d("mydebug", "MOT DE PASSE ORIGINAL : " + password);
            //Log.d("mydebug", "MOT DE PASSE CRYPTÉ MD5 : " + passwordCrypte);
            //Log.d("mydebug", "========================================");

            Log.d("mydebug", "JSON envoyé : " + jsonParam.toString());

            // Envoyer le JSON
            OutputStream os = urlConnection.getOutputStream();
            os.write(jsonParam.toString().getBytes("UTF-8"));
            os.close();

            responseCode = urlConnection.getResponseCode();
            Log.d("mydebug", "Response code : " + responseCode);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            // Lecture du résultat
            int codeCaractere = -1;
            while ((codeCaractere = in.read()) != -1) {
                sResultatAppel = sResultatAppel + (char) codeCaractere;
            }
            in.close();

            Log.d("mydebug", "Réponse reçue : " + sResultatAppel);

        } catch (IOException ioe) {
            Log.d("mydebug", ">>>IOException : " + ioe.toString());
        } catch (Exception e) {
            Log.d("mydebug", ">>>Exception : " + e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return sResultatAppel;
    }

    // ENCRYPTAGE EN MD5
    private String encypterChaineMD5(String chaine) {
        byte[] chaineBytes = chaine.getBytes();
        byte[] hash = null;
        try {
            hash = MessageDigest.getInstance("MD5").digest(chaineBytes);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        StringBuffer hashString = new StringBuffer();
        for (int i=0; i<hash.length; ++i ) {
            String hex = Integer.toHexString(hash[i]);
            if (hex.length() == 1) {
                hashString.append('0');
                hashString.append(hex.charAt(hex.length()-1));
            }
            else {
                hashString.append(hex.substring(hex.length()-2));
            }
        }
        return hashString.toString();
    }
}