package pl.wiktorek140.telefony;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class TelefonyProvider extends ContentProvider {
    private PomocnikBD mPomocnikBD;
    private static final String IDENTYFIKATOR = "pl.wiktorek140.telefony.TelefonyProvider";
    public static final Uri URI_ZAWARTOSCI = Uri.parse("content://" + IDENTYFIKATOR + "/" + PomocnikBD.NAZWA_TABELI);
    private static final int CALA_TABELA = 1;
    private static final int WYBRANY_WIERSZ = 2;
    private static final UriMatcher sDopasowanieUri = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sDopasowanieUri.addURI(IDENTYFIKATOR, PomocnikBD.NAZWA_TABELI, CALA_TABELA);
        sDopasowanieUri.addURI(IDENTYFIKATOR, PomocnikBD.NAZWA_TABELI + "/#", WYBRANY_WIERSZ);
    }

    @Override
    public String getType(@NonNull Uri uri)
    {
        return null;
    }

    /**
     * Metoda usuwa dane z bazy danych
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int typUri = sDopasowanieUri.match(uri);
        SQLiteDatabase baza = mPomocnikBD.getWritableDatabase();
        int liczbaUsunietych = 0;

        switch(typUri) {
            case CALA_TABELA:
                liczbaUsunietych = baza.delete(PomocnikBD.NAZWA_TABELI, selection, selectionArgs);
                break;
            case WYBRANY_WIERSZ:
                liczbaUsunietych = baza.delete(PomocnikBD.NAZWA_TABELI, dodajIdDoSelekcji(selection, uri), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return liczbaUsunietych;
    }

    /**
     * Metoda wstawia dane do bazy danych
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        int typUri = sDopasowanieUri.match(uri);
        SQLiteDatabase baza = mPomocnikBD.getWritableDatabase();
        long idDodanego = 0;

        switch(typUri) {
            case CALA_TABELA:
                idDodanego = baza.insert(PomocnikBD.NAZWA_TABELI, null, values);
                break;
            default:
                throw new IllegalArgumentException("Nieznane Uri : "+uri);
        }

        getContext().getContentResolver().notifyChange(	uri, null);
        return Uri.parse(PomocnikBD.NAZWA_TABELI + "/" + idDodanego);
    }

    @Override
    public boolean onCreate() {
        mPomocnikBD = new PomocnikBD(getContext());
        return false;
    }

    /**
     * Metoda wysyła zapytanie do bazy danych
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int typUri = sDopasowanieUri.match(uri);
        SQLiteDatabase baza = mPomocnikBD.getWritableDatabase();
        Cursor kursorTel = null;

        switch (typUri) {
            case CALA_TABELA:
                kursorTel = baza.query(false, PomocnikBD.NAZWA_TABELI, projection, selection, selectionArgs, null, null, sortOrder, null, null);
                break;
            case WYBRANY_WIERSZ:
                kursorTel = baza.query(false, PomocnikBD.NAZWA_TABELI, projection, dodajIdDoSelekcji(selection, uri), selectionArgs, null, null, sortOrder, null, null);
                break;
            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }
        kursorTel.setNotificationUri(getContext().getContentResolver(), uri);
        return kursorTel;
    }

    private String dodajIdDoSelekcji(String selekcja, Uri uri) {
        if(selekcja != null && !selekcja.equals("")) {
            selekcja = selekcja + " and " + PomocnikBD.ID + "=" + uri.getLastPathSegment();
        } else {
            selekcja = PomocnikBD.ID + "=" + uri.getLastPathSegment();
        }
        return selekcja;
    }

    // Aktualizacja danych w bazie danych
    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int typUri = sDopasowanieUri.match(uri);
        SQLiteDatabase baza = mPomocnikBD.getWritableDatabase();
        int liczbaZaktualizowanych = 0;

        switch (typUri) {
            case CALA_TABELA:
                liczbaZaktualizowanych = baza.update(PomocnikBD.NAZWA_TABELI, values, selection, selectionArgs);
                break;
            case WYBRANY_WIERSZ:
                liczbaZaktualizowanych = baza.update(PomocnikBD.NAZWA_TABELI, values, dodajIdDoSelekcji(selection, uri), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return liczbaZaktualizowanych;
    }
}
