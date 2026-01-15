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
    private ArrayList<Film> filmsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listefilms);

        listViewFilms = findViewById(R.id.listViewFilms);
        try {
            URL url = new URL(UrlManager.getURLConnexion() + "/films");
            new ListFilmsTask(this).execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

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

            listViewFilms.setOnItemClickListener((parent, view, position, id) -> {
                Film filmSelectionne = filmsList.get(position);
                int filmId = filmSelectionne.getFilmId();
                ouvrirDetailFilm(filmId);
            });

        } catch (Exception e) {
            Log.e("mydebug", "Erreur parsing JSON : " + e.toString());
        }
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