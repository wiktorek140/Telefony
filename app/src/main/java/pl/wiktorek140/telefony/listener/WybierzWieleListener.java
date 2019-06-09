package pl.wiktorek140.telefony.listener;

import android.content.ContentUris;
import android.content.Context;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

import pl.wiktorek140.telefony.R;
import pl.wiktorek140.telefony.TelefonyProvider;

public class WybierzWieleListener implements AbsListView.MultiChoiceModeListener {

    private ListView mList;
    private Context mContext;

    public WybierzWieleListener(ListView list, Context context) {
        mList = list;
        mContext = context;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {}

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {}

    /**
     * Menu do zaznaczonych elementów
     */
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.pasek_kontekstowy_listy, menu);
        return true;
    }

    /**
     * Metoda wykonuje się gdy zostanie kliknięty przycisk w menu
     */
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if(mContext == null || mList == null) return false;
        if(item.getItemId() == R.id.kasuj_menu) { //Instrukcja wykona się gdy zostanie wciśnięty przycisk "USUŃ"
            long[] zaznaczone = mList.getCheckedItemIds();

            for(int i = 0; i < zaznaczone.length; i++) {
                mContext.getContentResolver().delete(ContentUris.withAppendedId(TelefonyProvider.URI_ZAWARTOSCI, zaznaczone[i]), null, null);
            }
            return true;
        }
        return false;
    }
}
