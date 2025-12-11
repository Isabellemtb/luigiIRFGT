package com.example.applicationrftg;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText editTextLogin;
    private EditText editTextPassword;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Récupérer les références des composants
        editTextLogin = findViewById(R.id.editTextLogin);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.button2);

        // Listener sur le bouton de connexion
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seConnecter();
            }
        });
    }

    private void seConnecter() {
        String login = editTextLogin.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Vérifier que les champs ne sont pas vides
        if (login.isEmpty()) {
            Toast.makeText(this, "Veuillez saisir un identifiant", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Veuillez saisir un mot de passe", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérifier si les identifiants correspondent à "toto" et "mdptoto"
        if (login.equals("toto") && password.equals("mdptoto")) {
            // Identifiants de test corrects, ouvrir la liste des films
            Intent intent = new Intent(MainActivity.this, ListefilmsActivity.class);
            startActivity(intent);
        } else {
            // Identifiants incorrects, afficher un message d'erreur
            Toast.makeText(this, "Identifiants incorrects", Toast.LENGTH_SHORT).show();
        }
    }

    public void ouvrirPage(View view) {
        Intent intent = new Intent(MainActivity.this, ListefilmsActivity.class);
        startActivity(intent);
    }
}