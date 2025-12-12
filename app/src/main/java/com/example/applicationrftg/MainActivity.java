package com.example.applicationrftg;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;
import org.json.JSONObject;

/**
 * Activity principale - Écran de connexion
 */
public class MainActivity extends AppCompatActivity {

    private EditText editTextLogin;
    private EditText editTextPassword;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextLogin = findViewById(R.id.editTextLogin);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.button2);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seConnecter();
            }
        });
    }

    /**
     * Méthode appelée lors du clic sur "Se connecter"
     */
    private void seConnecter() {
        String email = editTextLogin.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Vérifier que les champs ne sont pas vides
        if (email.isEmpty()) {
            Toast.makeText(this, "Veuillez saisir un email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Veuillez saisir un mot de passe", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lancer l'AsyncTask pour vérifier la connexion
        Log.d("mydebug", "Tentative de connexion: " + email);
        new ConnexionTask(this).execute(email, password);
    }

    /**
     * Méthode appelée par ConnexionTask après vérification
     */
    public void onConnexionTerminee(String resultat) {
        Log.d("mydebug", "Résultat connexion JSON: " + resultat);

        try {
            // Parser le JSON de retour
            JSONObject jsonResponse = new JSONObject(resultat);
            int customerId = jsonResponse.getInt("customerId");

            Log.d("mydebug", "Customer ID reçu: " + customerId);

            if (customerId > 0) {
                // Connexion réussie
                Toast.makeText(this, "Connexion réussie !", Toast.LENGTH_SHORT).show();

                // Ouvrir la liste des films
                ouvrirPage(null);

            } else {
                // Identifiants incorrects (customerId = -1)
                Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_LONG).show();
                editTextPassword.setText(""); // Vider le mot de passe
            }

        } catch (Exception e) {
            Log.e("mydebug", "Erreur parsing JSON connexion: " + e.toString());
            Toast.makeText(this, "Erreur de connexion au serveur", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Méthode pour ouvrir la liste de films
     */
    public void ouvrirPage(View view) {
        Intent intent = new Intent(MainActivity.this, ListefilmsActivity.class);
        startActivity(intent);
    }
}