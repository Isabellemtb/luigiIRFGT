package com.example.applicationrftg;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Classe de gestion de la base de données SQLite pour le panier
 */
public class PanierDatabaseHelper extends SQLiteOpenHelper {

    // Nom de la table
    public static final String TABLE_PANIER = "panier";

    // Colonnes de la table
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FILM_ID = "film_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_LENGTH = "length";

    // Nom et version de la base de données
    private static final String DATABASE_NAME = "panier.db";
    private static final int DATABASE_VERSION = 1;

    // Commande SQL pour la création de la table
    private static final String DATABASE_CREATE = "create table "
            + TABLE_PANIER + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_FILM_ID + " integer not null, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_YEAR + " integer not null, "
            + COLUMN_LENGTH + " integer not null);";

    public PanierDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        // Création de la table lors de la première utilisation
        database.execSQL(DATABASE_CREATE);
        Log.d("mydebug", "Base de données panier créée avec succès");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(PanierDatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANIER);
        onCreate(db);
    }
}
