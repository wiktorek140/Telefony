package pl.wiktorek140.telefony.helper;

import android.widget.EditText;

public class Validator {
    /**
     * Metoda sprawdza czy wszystkie pola formularza są wypełnione
     * @return TRUE jeśli pola są wypełnione FALSE jeśli nie
     */
    public static boolean sprawdzNapisy(EditText producent, EditText model, EditText android, EditText url)
    {
        return !(producent.getText().toString().equals("") ||
                model.getText().toString().equals("") ||
                android.getText().toString().equals("") ||
                url.getText().toString().equals(""));
    }
}
