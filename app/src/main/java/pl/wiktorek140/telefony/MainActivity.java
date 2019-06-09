package pl.wiktorek140.telefony;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.os.Bundle;

import pl.wiktorek140.telefony.listener.WybierzWieleListener;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private SimpleCursorAdapter mCursorAdapter;
    private ListView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mList = findViewById(R.id.lista_wartosci);
        wypelnijPola();

        mList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mList.setMultiChoiceModeListener(new WybierzWieleListener(mList,this)); //Listener do listy - zaznaczenie wielu elementów

        // Listener listy -  reaguje na kliknięcie elementu
        mList.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent zamiar = new Intent(MainActivity.this, EditActivity.class);
            zamiar.putExtra(PomocnikBD.ID, l);
            startActivityForResult(zamiar, 0);
        });
    }

    /**
     * Metoda wypełnia listę danymi
     */
    private void wypelnijPola() {
        getLoaderManager().initLoader(0, null, this);
        mCursorAdapter = new SimpleCursorAdapter(this, R.layout.list_row, null,
                new String[] { PomocnikBD.PRODUCENT, PomocnikBD.MODEL },
                new    int[] { R.id.producentText,   R.id.modelText }, 0);
        mList.setAdapter(mCursorAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projekcja = { PomocnikBD.ID, PomocnikBD.PRODUCENT, PomocnikBD.MODEL };
        return new CursorLoader(this, TelefonyProvider.URI_ZAWARTOSCI, projekcja, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor dane) {
        mCursorAdapter.swapCursor(dane);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    //Funkcja do widoku nowego elementu
    private void tworzElement() {
        Intent zamiar = new Intent(this, EditActivity.class);
        zamiar.putExtra(PomocnikBD.ID, (long) -1);
        startActivityForResult(zamiar, 0);
    }

    /**
     * @param requestCode Kod zadania
     * @param resultCode Kod wyniku
     * @param data Dane pobrane z wcześniej wywołanej aktywności
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getLoaderManager().restartLoader(0, null, this);
    }

    /**
     * Dodaje menu do paska
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pasek_akcji_lista_tel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.dodaj) //Instrukcja wykona się gdy zostanie wciśnięty przycisk "Dodaj"
            tworzElement();

        return super.onOptionsItemSelected(item);
    }
}