package com.example.applicationrftgich;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * AsyncTask pour vider le panier via l'API
 * Appelle DELETE /cart/clear/{customerId}
 */
@SuppressWarnings("deprecation")
public class ViderPanierApiTask extends AsyncTask<Integer, Void, Boolean> {

    private volatile PanierActivity screen;

    public ViderPanierApiTask(PanierActivity s) {
        this.screen = s;
    }

    @Override
    protected Boolean doInBackground(Integer... params) {
        int customerId = params[0];

        try {
            URL url = new URL(UrlManager.getURLConnexion() + "/cart/clear/" + customerId);
            return appelerDeleteHttp(url);
        } catch (Exception e) {
            Log.e("mydebug", "Erreur vidage panier API: " + e.toString());
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean succes) {
        screen.onPanierVide(succes);
    }

    private Boolean appelerDeleteHttp(URL urlAAppeler) {
        HttpURLConnection urlConnection = null;
        String jwt = screen.getString(R.string.jwt_token);

        try {
            Log.d("mydebug", "DELETE clear : " + urlAAppeler.toString());

            urlConnection = (HttpURLConnection) urlAAppeler.openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setRequestProperty("Authorization", "Bearer " + jwt);
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));

            int responseCode = urlConnection.getResponseCode();
            Log.d("mydebug", "Response code DELETE clear : " + responseCode);

            return responseCode == 200;

        } catch (Exception e) {
            Log.d("mydebug", ">>>Exception DELETE clear : " + e.toString());
            return false;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}
