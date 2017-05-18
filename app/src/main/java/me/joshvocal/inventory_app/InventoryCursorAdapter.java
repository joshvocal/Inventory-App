package me.joshvocal.inventory_app;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * Created by josh on 5/17/17.
 */

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c) {
        // TODO:
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // TODO:
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // TODO:
    }
}
