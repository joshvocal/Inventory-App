package me.joshvocal.inventory_app.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by josh on 5/17/17.
 */

public class InventoryContract {

    public static final String CONTENT_AUTHORITY = "me.joshvocal.inventory_app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_INVENTORY = "inventory";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private InventoryContract() {

    }

    public static final class ProductEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /** Name of database table for inventory*/
        public final static String TABLE_NAME = "inventory";

        /** Unique ID number for the inventory (only for use in the database table).
         *
         * Type: INTEGER
         */

        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the product.
         *
         * Type: TEXT
         */

        public final static String COLUMN_PRODUCT_NAME = "name";

        /**
         * Quantity of the product
         *
         * Type: INTEGER
         */

        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";

        /**
         * Price of the product
         *
         * Type: INTEGER
         */

        public final static String COLUMN_PRODUCT_PRICE = "price";

        /**
         * Supplier's email of the product
         *
         * Type: TEXT
         */

        public final static String COLUMN_PRODUCT_SUPPLIER_EMAIL = "supplierEmail";

        /**
         * Picture of the product
         *
         * Type: TEXT
         */

        public final static String COLUMN_PRODUCT_PICTURE = "productPicture";

    }

}
