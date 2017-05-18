package me.joshvocal.inventory_app;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import me.joshvocal.inventory_app.data.InventoryContract.ProductEntry;

/**
 * Created by josh on 5/17/17.
 */

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.inventory_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template.
        TextView productNameTextView = (TextView) view.findViewById(R.id.product_title_text_view);
        TextView productPriceTextView = (TextView) view.findViewById(R.id.product_price_text_view);
        TextView productQuantityTextView = (TextView) view.findViewById(R.id.product_quantity_text_view);
        //ImageView productImageImageView = (ImageView) view.findViewById(R.id.product_image_image_view);

        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
        int price = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        //String image = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PICTURE));

        // Populate fields with extracted properties.
        productNameTextView.setText(name);
        productPriceTextView.setText("Price: $"+ Integer.toString(price));
        productQuantityTextView.setText("Quantity: " + Integer.toString(quantity));
        //productImageImageView.setText(name);
    }
}
