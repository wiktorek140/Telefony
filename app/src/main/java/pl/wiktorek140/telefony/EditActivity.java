package pl.wiktorek140.telefony;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import pl.wiktorek140.telefony.helper.Validator;

public class EditActivity extends Activity
{
    private long idWiersz;
    private EditText model;
    private EditText phoneURL;
    private EditText producent;
    private EditText androidVer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        producent = findViewById(R.id.producentEdycja);
        model = findViewById(R.id.modelEdycja);
        androidVer = findViewById(R.id.androidEdycja);
        phoneURL = findViewById(R.id.wwwEdycja);

        idWiersz = -1;

        if(savedInstanceState != null) {
            idWiersz = savedInstanceState.getLong(PomocnikBD.ID);
        } else {
            Bundle bundle = getIntent().getExtras();
            if(bundle != null)
                idWiersz = bundle.getLong(PomocnikBD.ID);
        }

        if(idWiersz != -1) //Instrukcja wykona się jeśli aktywność została uruchomiona w trybie dodawania nowego telefonu
            uzupelnijPola();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putLong(PomocnikBD.ID, idWiersz);
    }

    /**
     * Zapis do bazy - akcja po kliknięciu przycisku
     */
    public void zapisz(View view) {
        if(Validator.sprawdzNapisy(producent, model, androidVer, phoneURL)) {
            ContentValues wartosci = new ContentValues();
            wartosci.put(PomocnikBD.PRODUCENT, producent.getText().toString());
            wartosci.put(PomocnikBD.MODEL, model.getText().toString());
            wartosci.put(PomocnikBD.ANDROID, androidVer.getText().toString());

            String adres = phoneURL.getText().toString();
            if(adres.contains(".") && !adres.startsWith("http")) {
                adres= "https://"+adres;
            }
            if(!adres.startsWith("http://") && !adres.startsWith("https://")) { //Sprawdzenie czy adres zaczyna się od http:// lub https:// i jeśli nie to dodanie http:// do adres
                adres = "https://www.google.com/search?q="+ adres.replace(' ','+');
            }

            wartosci.put(PomocnikBD.WWW, adres);

            if(idWiersz == -1) {
                Uri uriNowego = getContentResolver().insert(TelefonyProvider.URI_ZAWARTOSCI, wartosci);
                idWiersz = Integer.parseInt(uriNowego.getLastPathSegment());
            } else {
                getContentResolver().update(ContentUris.withAppendedId(TelefonyProvider.URI_ZAWARTOSCI, idWiersz), wartosci, null, null);
            }
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, getString(R.string.wypelnij_pola_komunikat), Toast.LENGTH_SHORT).show();
        }
    }

    public void anuluj(View view) {
        setResult(RESULT_CANCELED);
        finish(); //Zakończenie aktywności
    }

    public void otworzStrone(View view) {
        if(!phoneURL.getText().toString().equals("")) { //Sprawdzenie czy pole WWW nie jest puste
            String adres = phoneURL.getText().toString();

            if(adres.contains(".") && !adres.startsWith("http")) {
             adres= "https://"+adres;
            }
            if(!adres.startsWith("http://") && !adres.startsWith("https://")) { //Sprawdzenie czy adres zaczyna się od http:// lub https:// i jeśli nie to dodanie http:// do adres
                adres = "https://www.google.com/search?q="+ adres.replace(' ','+');
            }
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(adres))); //Uruchomienie aktywności przeglądarki
        } else {
            Toast.makeText(this, getString(R.string.wypelnij_url_komunikat), Toast.LENGTH_SHORT).show();
        }
    }

    //Metoda wypełnia pola formularza jeśli aktywność została wywołana w trybie edycji
    private void uzupelnijPola() {
        String projekcja[] = { PomocnikBD.PRODUCENT, PomocnikBD.MODEL, PomocnikBD.ANDROID, PomocnikBD.WWW };
        Cursor kursorTel = getContentResolver().query(ContentUris.withAppendedId(TelefonyProvider.URI_ZAWARTOSCI, idWiersz), projekcja, null, null, null);
        kursorTel.moveToFirst();

        int indeksKolumny = kursorTel .getColumnIndexOrThrow(PomocnikBD.PRODUCENT);
        String wartosc = kursorTel.getString(indeksKolumny);

        producent.setText(wartosc);
        model.setText(kursorTel.getString(kursorTel.getColumnIndexOrThrow(PomocnikBD.MODEL)));
        androidVer.setText(kursorTel.getString(kursorTel .getColumnIndexOrThrow(PomocnikBD.ANDROID)));
        phoneURL.setText(kursorTel.getString(kursorTel .getColumnIndexOrThrow(PomocnikBD.WWW)));
        kursorTel.close();
    }
}