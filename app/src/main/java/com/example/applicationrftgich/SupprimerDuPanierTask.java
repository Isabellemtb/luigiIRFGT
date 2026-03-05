package com.example.applicationrftgich;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * AsyncTask pour supprimer un item du panier via l'API
 * Appelle DELETE /cart/{rentalId}
 */
@SuppressWarnings("deprecation")
public class SupprimerDuPanierTask extends AsyncTask<Integer, Void, Boolean> {

    private volatile PanierActivity screen;
    private int position;

    public SupprimerDuPanierTask(PanierActivity s, int position) {
        this.screen = s;
        this.position = position;
    }

    @Override
    protected Boolean doInBackground(Integer... params) {
        int rentalId = params[0];

        try {
            URL url = new URL(UrlManager.getURLConnexion() + "/cart/" + rentalId);
            return appelerDeleteHttp(url);
        } catch (Exception e) {
            Log.e("mydebug", "Erreur suppression panier: " + e.toString());
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean succes) {
        screen.onFilmSupprimeDuPanier(succes, position);
    }

    private Boolean appelerDeleteHttp(URL urlAAppeler) {
        HttpURLConnection urlConnection = null;
        String jwt = screen.getString(R.string.jwt_token);

        try {
            Log.d("mydebug", "DELETE : " + urlAAppeler.toString());

            urlConnection = (HttpURLConnection) urlAAppeler.openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setRequestProperty("Authorization", "Bearer " + jwt);
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));

            int responseCode = urlConnection.getResponseCode();
            Log.d("mydebug", "Response code DELETE : " + responseCode);

            return responseCode == 200;

        } catch (Exception e) {
            Log.d("mydebug", ">>>Exception DELETE : " + e.toString());
            return false;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}
