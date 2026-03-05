package com.example.applicationrftgich;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.util.ArrayList;
import com.google.gson.reflect.TypeToken;
import androidx.appcompat.app.AppCompatActivity;
import java.net.MalformedURLException;
import java.net.URL;

public class ListefilmsActivity extends AppCompatActivity {

    private ListView listViewFilms;
    private ArrayList<Film> filmsList;
    private View layoutChargement;
    private TextView textViewChargement;

    private Handler handler = new Handler();
    private int dotsCount = 0;

    private Runnable animateDots = new Runnable() {
        @Override
        public void run() {
            dotsCount = (dotsCount + 1) % 4;
            String dots = new String(new char[dotsCount]).replace("\0", ".");
            textViewChargement.setText("🎬 Chargement des films" + dots);
            handler.postDelayed(this, 400);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listefilms);

        listViewFilms = findViewById(R.id.listViewFilms);
        layoutChargement = findViewById(R.id.layoutChargement);
        textViewChargement = findViewById(R.id.textViewChargement);

        findViewById(R.id.buttonFermer).setOnClickListener(v -> finishAffinity());


        try {
            URL url = new URL(UrlManager.getURLConnexion() + "/films");
            new ListFilmsTask(this).execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void afficherChargement(boolean visible) {
        layoutChargement.setVisibility(visible ? View.VISIBLE : View.GONE);
        listViewFilms.setVisibility(visible ? View.GONE : View.VISIBLE);
        if (visible) {
            handler.post(animateDots);
        } else {
            handler.removeCallbacks(animateDots);
        }
    }

    public void mettreAJourActivityApresAppelRest(String json) {
        if (json == null || json.isEmpty()) {
            Toast.makeText(this, "Impossible de joindre le serveur", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Film>>() {}.getType();
            filmsList = gson.fromJson(json, listType);

            ArrayList<String> titres = new ArrayList<>();
            for (Film f : filmsList) {
                titres.add(f.getTitle() + " (" + f.getReleaseYear() + ")");
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    titres
            );
            listViewFilms.setAdapter(adapter);

            listViewFilms.setOnItemClickListener((parent, view, position, id) -> {
                Film filmSelectionne = filmsList.get(position);
                int filmId = filmSelectionne.getFilmId();
                ouvrirDetailFilm(filmId);
            });

        } catch (Exception e) {
            Log.e("mydebug", "Erreur parsing JSON : " + e.toString());
            Toast.makeText(this, "Impossible de charger les films", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(animateDots);
    }

    private void ouvrirDetailFilm(int filmId) {
        Intent intent = new Intent(this, DetailfilmActivity.class);
        intent.putExtra("film_id", filmId);
        startActivity(intent);
    }

    public void ouvrirPanier(View view) {
        Intent intent = new Intent(this, PanierActivity.class);
        startActivity(intent);
    }
}