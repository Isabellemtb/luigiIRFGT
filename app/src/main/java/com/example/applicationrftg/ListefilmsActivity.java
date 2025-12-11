package com.example.applicationrftg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.util.Log;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.util.ArrayList;
import com.google.gson.reflect.TypeToken;

import androidx.appcompat.app.AppCompatActivity;

import java.net.MalformedURLException;
import java.net.URL;

public class ListefilmsActivity extends AppCompatActivity {

    private ListView listViewFilms;
    private ArrayList<Film> filmsList;  // Pour avoir l'ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listefilms);

        listViewFilms = findViewById(R.id.listViewFilms);
        try {
            URL url = new URL("http://10.0.2.2:8180/films"); // URL l'API
            new ListFilmsTask(this).execute(url);  // Lancement de la tâche asynchrone
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    // Cette méthode sera appelée par ListFilmsTask après la récupération des données JSON
    public void mettreAJourActivityApresAppelRest(String json) {
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

            // Listener pour détecter les clics sur les films
            listViewFilms.setOnItemClickListener((parent, view, position, id) -> {
                // Récupérer seulement l'ID du film cliqué
                Film filmSelectionne = filmsList.get(position);
                int filmId = filmSelectionne.getFilmId();

                // Ouvrir la page détail en passant juste l'ID
                ouvrirDetailFilm(filmId);
            });

        } catch (Exception e) {
            Log.e("mydebug", "Erreur parsing JSON : " + e.toString());
        }
    }

    // Méthode pour ouvrir la page détail d'un film
    private void ouvrirDetailFilm(int filmId) {
        Intent intent = new Intent(this, DetailfilmActivity.class);
        intent.putExtra("film_id", filmId);  // Passer juste l'ID
        startActivity(intent);
    }

    public void ouvrirPanier(View view) {
        Intent intent = new Intent(this, PanierActivity.class);
        startActivity(intent);
    }
}