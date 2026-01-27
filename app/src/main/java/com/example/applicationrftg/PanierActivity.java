package com.example.applicationrftg;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Activity pour afficher et gérer le panier
 * Utilisation SQLite pour stocker les films localement
 */
public class PanierActivity extends AppCompatActivity {

    private ListView listViewPanier;
    private Button buttonValiderPanier;
    private Button buttonViderPanier;

    // ArrayLists pour stocker les données du panier
    private ArrayList<String> filmsTitres;
    private ArrayList<Integer> filmsIds;
    private ArrayList<Integer> filmsPanierIds;  // IDs de la table panier (pour supprimer)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panier);

        // Récupérer les références des composants
        listViewPanier = findViewById(R.id.listViewPanier);
        buttonValiderPanier = findViewById(R.id.buttonValiderPanier);
        buttonViderPanier = findViewById(R.id.buttonViderPanier);

        // Initialiser les ArrayLists
        filmsTitres = new ArrayList<>();
        filmsIds = new ArrayList<>();
        filmsPanierIds = new ArrayList<>();

        // Charger les films du panier depuis SQLite
        chargerPanier();

        // Listener pour valider le panier
        buttonValiderPanier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validerPanier();
            }
        });

        // Listener pour vider le panier
        buttonViderPanier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viderPanier();
            }
        });

    }

    /**
     * Chargement des films du panier depuis la base SQLite
     * et les affiche dans la ListView
     */
    private void chargerPanier() {
        SQLiteDatabase database = null;
        PanierDatabaseHelper dbHelper = null;
        Cursor cursor = null;

        try {
            // Ouvrir la base de données en lecture
            dbHelper = new PanierDatabaseHelper(this);
            database = dbHelper.getReadableDatabase();

            // Définir les colonnes à récupérer
            String[] columns = {
                    PanierDatabaseHelper.COLUMN_ID,
                    PanierDatabaseHelper.COLUMN_FILM_ID,
                    PanierDatabaseHelper.COLUMN_TITLE,
                    PanierDatabaseHelper.COLUMN_YEAR,
                    PanierDatabaseHelper.COLUMN_LENGTH
            };

            // Exécuter la requête
            cursor = database.query(
                    PanierDatabaseHelper.TABLE_PANIER,  // Table
                    columns,                             // Colonnes
                    null,                                // WHERE (null = tout)
                    null,                                // Arguments WHERE
                    null,                                // GROUP BY
                    null,                                // HAVING
                    null                                 // ORDER BY
            );

            // Vider les ArrayLists
            filmsTitres.clear();
            filmsIds.clear();
            filmsPanierIds.clear();

            // Parcourir les résultats avec le Cursor
            if (cursor.moveToFirst()) {
                do {
                    // Récupérer les données de chaque film
                    int panierId = cursor.getInt(cursor.getColumnIndexOrThrow(PanierDatabaseHelper.COLUMN_ID));
                    int filmId = cursor.getInt(cursor.getColumnIndexOrThrow(PanierDatabaseHelper.COLUMN_FILM_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(PanierDatabaseHelper.COLUMN_TITLE));
                    int year = cursor.getInt(cursor.getColumnIndexOrThrow(PanierDatabaseHelper.COLUMN_YEAR));
                    int length = cursor.getInt(cursor.getColumnIndexOrThrow(PanierDatabaseHelper.COLUMN_LENGTH));

                    // Ajouter aux ArrayLists
                    filmsTitres.add(title + " (" + year + ") - " + length + " min");
                    filmsIds.add(filmId);
                    filmsPanierIds.add(panierId);

                    Log.d("mydebug", "Film chargé du panier: " + title);
                } while (cursor.moveToNext());
            }

            // Afficher dans la ListView
            if (filmsTitres.isEmpty()) {
                filmsTitres.add("Panier vide");
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    filmsTitres
            );
            listViewPanier.setAdapter(adapter);

            // Vérifier la disponibilité de chaque film
            if (!filmsIds.isEmpty()) {
                verifierDisponibilites();
            }

            Log.d("mydebug", "Panier chargé: " + filmsIds.size() + " film(s)");

        } catch (Exception e) {
            Log.e("mydebug", "Erreur lors du chargement du panier: " + e.toString());
            Toast.makeText(this, "Erreur lors du chargement du panier", Toast.LENGTH_SHORT).show();
        } finally {
            // Toujours fermer le cursor et la base
            if (cursor != null) {
                cursor.close();
            }
            if (dbHelper != null) {
                dbHelper.close();
            }
        }
    }

    /**
     * Vérifie la disponibilité de chaque film du panier
     */
    private void verifierDisponibilites() {
        for (int i = 0; i < filmsIds.size(); i++) {
            new CheckDisponibiliteTask(this, i).execute(filmsIds.get(i));
        }
    }

    /**
     * Callback appelé quand la disponibilité d'un film est vérifiée
     */
    public void onDisponibiliteVerifiee(int position, boolean disponible) {
        if (position < filmsTitres.size() && !filmsTitres.get(position).equals("Panier vide")) {
            String titre = filmsTitres.get(position);
            // Enlever l'ancien statut s'il existe
            if (titre.contains(" - Disponible") || titre.contains(" - Indisponible")) {
                titre = titre.replace(" - Disponible", "").replace(" - Indisponible", "");
            }
            // Ajouter le nouveau statut
            if (disponible) {
                filmsTitres.set(position, titre + " - Disponible");
            } else {
                filmsTitres.set(position, titre + " - Indisponible");
            }

            // Rafraîchir la ListView
            ((ArrayAdapter) listViewPanier.getAdapter()).notifyDataSetChanged();
        }
    }

    /**
     * Supprimer un film du panier
     */
    private void supprimerFilmDuPanier(int position) {
        // Vérifier que le panier n'est pas vide
        if (position >= filmsPanierIds.size() || filmsTitres.get(position).equals("Panier vide")) {
            return;
        }

        SQLiteDatabase database = null;
        PanierDatabaseHelper dbHelper = null;

        try {
            int panierId = filmsPanierIds.get(position);

            // Ouvrir la base en écriture
            dbHelper = new PanierDatabaseHelper(this);
            database = dbHelper.getWritableDatabase();

            // Supprimer le film
            int rowsDeleted = database.delete(
                    PanierDatabaseHelper.TABLE_PANIER,
                    PanierDatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(panierId)}
            );

            if (rowsDeleted > 0) {
                Log.d("mydebug", "Film supprimé du panier");
                Toast.makeText(this, "Film supprimé du panier", Toast.LENGTH_SHORT).show();
                // Recharger le panier
                chargerPanier();
            }

        } catch (Exception e) {
            Log.e("mydebug", "Erreur lors de la suppression: " + e.toString());
            Toast.makeText(this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
        } finally {
            if (dbHelper != null) {
                dbHelper.close();
            }
        }
    }

    /**
     * Vider complètement le panier
     */
    private void viderPanier() {
        SQLiteDatabase database = null;
        PanierDatabaseHelper dbHelper = null;

        try {
            // Ouvrir la base en écriture
            dbHelper = new PanierDatabaseHelper(this);
            database = dbHelper.getWritableDatabase();

            // Supprimer tous les films
            int rowsDeleted = database.delete(PanierDatabaseHelper.TABLE_PANIER, null, null);

            if (rowsDeleted > 0) {
                Log.d("mydebug", "Panier vidé: " + rowsDeleted + " film(s) supprimé(s)");
                Toast.makeText(this, "Panier vidé !", Toast.LENGTH_SHORT).show();
                chargerPanier();
            } else {
                Toast.makeText(this, "Le panier est déjà vide", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e("mydebug", "Erreur lors du vidage du panier: " + e.toString());
            Toast.makeText(this, "Erreur lors du vidage du panier", Toast.LENGTH_SHORT).show();
        } finally {
            if (dbHelper != null) {
                dbHelper.close();
            }
        }
    }

    /**
     * Valider le panier et crée la commande via l'API
     */
    private void validerPanier() {
        // Vérifier que le panier n'est pas vide
        if (filmsIds.isEmpty() || filmsTitres.get(0).equals("Panier vide")) {
            Toast.makeText(this, "Le panier est vide !", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("mydebug", "Validation du panier avec " + filmsIds.size() + " film(s)");

        // Récupérer le customerId (TODO: récupérer le vrai customerId stocké à la connexion)
        int customerId = 1;

        // Appeler l'API pour valider
        new ValiderPanierTask(this).execute(customerId);
    }

    /**
     * Méthode appelée par ValiderPanierTask après validation
     */
    public void onPanierValide(String resultat) {
        if (resultat.equals("OK")) {
            Toast.makeText(this, "Commande validée avec succès !", Toast.LENGTH_LONG).show();
            // Vider le panier après validation réussie
            viderPanier();
        } else {
            Toast.makeText(this, "Erreur lors de la validation de la commande", Toast.LENGTH_LONG).show();
        }
    }
}