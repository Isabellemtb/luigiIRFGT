package com.example.applicationrftg;

import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import com.google.gson.Gson;
import java.net.MalformedURLException;
import java.net.URL;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity pour afficher les détails d'un film
 * Permet d'ajouter le film au panier (stocké dans SQLite)
 */
public class DetailfilmActivity extends AppCompatActivity {

    private TextView textViewTitle;
    private TextView textViewDescription;
    private TextView textViewYear;
    private TextView textViewLength;
    private TextView textViewLanguage;
    private TextView textViewDirectors;
    private TextView textViewActors;
    private TextView textViewCategories;
    private Button buttonAddToCart;

    // Variables pour stocker les infos du film
    private int filmId;
    private String filmTitle;
    private int filmYear;
    private int filmLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailfilm);

        // Récupérer les références des composants
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewYear = findViewById(R.id.textViewYear);
        textViewLength = findViewById(R.id.textViewLength);
        textViewLanguage = findViewById(R.id.textViewLanguage);
        textViewDirectors = findViewById(R.id.textViewDirectors);
        textViewActors = findViewById(R.id.textViewActors);
        textViewCategories = findViewById(R.id.textViewCategories);
        buttonAddToCart = findViewById(R.id.buttonAddToCart);

        // Listener sur le bouton "Ajouter au panier"
        buttonAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ajouterAuPanier();
            }
        });

        // Récupérer l'ID du film passé depuis ListefilmsActivity
        Intent intent = getIntent();
        if (intent != null) {
            filmId = intent.getIntExtra("film_id", -1);

            if (filmId != -1) {
                // Faire un appel API pour récupérer le film
                try {
                    URL url = new URL(UrlManager.getURLConnexion() + "/films/" + filmId);
                    new GetFilmByIdTask(this).execute(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Erreur : URL invalide", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Erreur : Film non trouvé", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Ajoute le film au panier (SQLite)
     */
    private void ajouterAuPanier() {
        SQLiteDatabase database = null;
        PanierDatabaseHelper dbHelper = null;

        try {
            // Créer/ouvrir la base de données
            dbHelper = new PanierDatabaseHelper(this);
            database = dbHelper.getWritableDatabase();

            // Préparer les valeurs à insérer
            ContentValues values = new ContentValues();
            values.put(PanierDatabaseHelper.COLUMN_FILM_ID, filmId);
            values.put(PanierDatabaseHelper.COLUMN_TITLE, filmTitle);
            values.put(PanierDatabaseHelper.COLUMN_YEAR, filmYear);
            values.put(PanierDatabaseHelper.COLUMN_LENGTH, filmLength);

            // Insérer dans la base de données
            long insertId = database.insert(PanierDatabaseHelper.TABLE_PANIER, null, values);

            if (insertId != -1) {
                Log.d("mydebug", "Film ajouté au panier, ID: " + insertId);
                Toast.makeText(this, "Film ajouté au panier !", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Erreur lors de l'ajout au panier", Toast.LENGTH_SHORT).show();
            }

        } catch (SQLException sqle) {
            Log.e("mydebug", "SQLException: " + sqle.toString());
            Toast.makeText(this, "Erreur base de données", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("mydebug", "Exception: " + e.toString());
            Toast.makeText(this, "Erreur lors de l'ajout au panier", Toast.LENGTH_SHORT).show();
        } finally {
            if (dbHelper != null) {
                dbHelper.close();
            }
        }
    }

    /**
     * Ouvre la page panier
     */
    private void ouvrirPanier() {
        Intent intent = new Intent(this, PanierActivity.class);
        startActivity(intent);
    }

    /**
     * Méthode appelée par GetFilmByIdTask après la récupération du film
     */
    public void afficherFilm(String json) {
        Log.d("mydebug", "JSON reçu pour le film");

        if (json == null || json.isEmpty()) {
            Log.e("mydebug", "ERREUR : JSON est null ou vide !");
            runOnUiThread(() -> Toast.makeText(this, "Erreur : Pas de données reçues", Toast.LENGTH_LONG).show());
            return;
        }

        try {
            Gson gson = new Gson();
            Film film = gson.fromJson(json, Film.class);

            if (film == null) {
                Log.e("mydebug", "ERREUR : Film est null après parsing !");
                runOnUiThread(() -> Toast.makeText(this, "Erreur lors du chargement du film", Toast.LENGTH_LONG).show());
                return;
            }

            // Stocker les infos du film pour l'ajout au panier
            filmTitle = film.getTitle();
            filmYear = film.getReleaseYear();
            filmLength = film.getLength();

            // Afficher les informations de base
            textViewTitle.setText(film.getTitle());
            textViewDescription.setText(film.getDescription());
            textViewYear.setText("Année : " + film.getReleaseYear());
            textViewLength.setText("Durée : " + film.getLength() + " minutes");
            textViewLanguage.setText("Langue : " + getLanguageName(film.getOriginalLanguageId()));

            // Afficher les réalisateurs
            if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
                StringBuilder directorsText = new StringBuilder();
                for (int i = 0; i < film.getDirectors().size(); i++) {
                    Film.Director director = film.getDirectors().get(i);
                    directorsText.append(director.getFirstName())
                            .append(" ")
                            .append(director.getLastName());
                    if (i < film.getDirectors().size() - 1) {
                        directorsText.append(", ");
                    }
                }
                textViewDirectors.setText("Réalisateurs : " + directorsText.toString());
            } else {
                textViewDirectors.setText("Réalisateurs : Aucun");
            }

            // Afficher les acteurs (maximum 5)
            if (film.getActors() != null && !film.getActors().isEmpty()) {
                StringBuilder actorsText = new StringBuilder();
                int maxActors = Math.min(5, film.getActors().size());
                for (int i = 0; i < maxActors; i++) {
                    Film.Actor actor = film.getActors().get(i);
                    actorsText.append(actor.getFirstName())
                            .append(" ")
                            .append(actor.getLastName());
                    if (i < maxActors - 1) {
                        actorsText.append(", ");
                    }
                }
                if (film.getActors().size() > 5) {
                    actorsText.append("...");
                }
                textViewActors.setText("Acteurs : " + actorsText.toString());
            } else {
                textViewActors.setText("Acteurs : Aucun");
            }

            // Afficher les catégories
            if (film.getCategories() != null && !film.getCategories().isEmpty()) {
                StringBuilder categoriesText = new StringBuilder();
                for (int i = 0; i < film.getCategories().size(); i++) {
                    Film.Category category = film.getCategories().get(i);
                    categoriesText.append(category.getName());
                    if (i < film.getCategories().size() - 1) {
                        categoriesText.append(", ");
                    }
                }
                textViewCategories.setText("Catégories : " + categoriesText.toString());
            } else {
                textViewCategories.setText("Catégories : Aucune");
            }

            Log.d("mydebug", "Film affiché avec succès");

        } catch (Exception e) {
            Log.e("mydebug", "ERREUR parsing JSON : " + e.toString());
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(this, "Erreur lors du chargement du film", Toast.LENGTH_LONG).show());
        }
    }

    /**
     * Convertit l'ID de la langue en nom de langue
     */
    private String getLanguageName(Integer languageId) {
        if (languageId == null) return "Non disponible";

        switch (languageId) {
            case 1: return "English";
            case 2: return "Italian";
            case 3: return "Japanese";
            case 4: return "Mandarin";
            case 5: return "French";
            case 6: return "German";
            default: return "Langue ID: " + languageId;
        }
    }
}