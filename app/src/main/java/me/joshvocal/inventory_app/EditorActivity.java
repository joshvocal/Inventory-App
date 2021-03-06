package me.joshvocal.inventory_app;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.joshvocal.inventoroy_app.R;
import me.joshvocal.inventory_app.data.InventoryContract.ProductEntry;

public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    // Boolean flag that keeps track of whether the product has been edited (true) or not (false).
    private boolean mProductHasChanged = false;

    // Identifier for the product data loader.
    private static final int EXISTING_PRODUCT_LOADER = 0;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 2;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 3;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 4;

    private String mCurrentPhotoPath;

    // Content URI for the existing product (null if it's a new product)
    private Uri mCurrentProductUri;

    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mEmailEditText;
    private ImageView mPictureImageView;

    private TextView mChangeQuantityTextView;
    private Button mTrackSaleButton;
    private Button mReceiveShipmentButton;
    private TextView mEmailActionTextView;
    private Button mOrderSupplierButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        mEmailEditText = (EditText) findViewById(R.id.edit_product_email);
        mPictureImageView = (ImageView) findViewById(R.id.edit_product_image);

        mChangeQuantityTextView = (TextView) findViewById(R.id.text_change_quantity);
        mEmailActionTextView = (TextView) findViewById(R.id.text_email_action);

        mTrackSaleButton = (Button) findViewById(R.id.button_track_sale);
        mTrackSaleButton.setOnClickListener(this);
        mReceiveShipmentButton = (Button) findViewById(R.id.button_receive_shipment);
        mReceiveShipmentButton.setOnClickListener(this);
        mOrderSupplierButton = (Button) findViewById(R.id.button_order_supplier);
        mOrderSupplierButton.setOnClickListener(this);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        // If the intent DOES NOT contain a product content URI, then we know that we are
        // creating a new product.
        if (mCurrentProductUri == null) {
            // This is a new product
            setTitle("Add New Product");

            mChangeQuantityTextView.setVisibility(View.GONE);
            mTrackSaleButton.setVisibility(View.GONE);
            mReceiveShipmentButton.setVisibility(View.GONE);
            mEmailActionTextView.setVisibility(View.GONE);
            mOrderSupplierButton.setVisibility(View.GONE);

        } else {

            setTitle("Product Details");
            mNameEditText.setEnabled(false);
            mNameEditText.setBackground(null);

            mPriceEditText.setEnabled(false);
            mPriceEditText.setBackground(null);

            mQuantityEditText.setEnabled(false);
            mQuantityEditText.setBackground(null);

            mEmailEditText.setEnabled(false);
            mEmailEditText.setBackground(null);

            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }


        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mEmailEditText.setOnTouchListener(mTouchListener);
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setEditMode() {
        setTitle("Edit Product");

        mNameEditText.setEnabled(true);
        mPriceEditText.setEnabled(true);
        mQuantityEditText.setEnabled(true);
        mEmailEditText.setEnabled(true);

        mChangeQuantityTextView.setVisibility(View.GONE);
        mTrackSaleButton.setVisibility(View.GONE);
        mReceiveShipmentButton.setVisibility(View.GONE);
        mEmailActionTextView.setVisibility(View.GONE);
        mOrderSupplierButton.setVisibility(View.GONE);
    }

    private void setDetailsMode() {
        setTitle("Product Details");

        mNameEditText.setEnabled(false);
        mNameEditText.setBackground(null);

        mPriceEditText.setEnabled(false);
        mPriceEditText.setBackground(null);

        mQuantityEditText.setEnabled(false);
        mQuantityEditText.setBackground(null);

        mEmailEditText.setEnabled(false);
        mEmailEditText.setBackground(null);

        mChangeQuantityTextView.setVisibility(View.VISIBLE);
        mTrackSaleButton.setVisibility(View.VISIBLE);
        mReceiveShipmentButton.setVisibility(View.VISIBLE);
        mEmailActionTextView.setVisibility(View.VISIBLE);
        mOrderSupplierButton.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_save:
                // Save product to database
                saveProduct();
                // Exit activity
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_add_photo:
                // Take a picture.
                requestPermissions();
                // Set the picture to the imageView.
                //setPic();
                return true;
            case R.id.action_details:
                setDetailsMode();
                return true;
            case R.id.action_edit:
                setEditMode();
                return true;
            case android.R.id.home:
                // If the product hasn't changed, continue with handling back button press
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User clicked "Discard" button, close the current activity.
                                finish();
                            }
                        };

                // Show dialog that there are unsaved changes.
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mCurrentProductUri == null) {
            return null;
        }

        // Since the editor shows all product attributes, define a projection that contains
        // all columns from the inventory table.
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL,
                ProductEntry.COLUMN_PRODUCT_PICTURE
        };

        // This loader will execute the ContentProvider's query method on a background thread.
        return new CursorLoader(this,   // Parent activity context
                mCurrentProductUri,     // Query the content URI for the current product
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor.
        if (cursor == null) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int emailColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL);
            int pictureColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PICTURE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String email = cursor.getString(emailColumnIndex);
            String picturePath = cursor.getString(pictureColumnIndex);
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

            mCurrentPhotoPath = picturePath;

            // Update the views on the screen with the values.
            mNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mEmailEditText.setText(email);
            mPictureImageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader in invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mEmailEditText.setText("");
    }

    /**
     * Get user input from editor and save new product into database.
     */
    private void saveProduct() {
        // Read from input fields
        // User trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String emailString = mEmailEditText.getText().toString().trim();
        String photoPathString = mCurrentPhotoPath;

        if (priceString.equals("")) {
            priceString = "0";
        }

        if (quantityString.equals("")) {
            quantityString = "0";
        }

        // Create a ContentValues object where column names are the keys,
        // and product attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, Double.parseDouble(priceString));
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, Integer.parseInt(quantityString));
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL, emailString);
        values.put(ProductEntry.COLUMN_PRODUCT_PICTURE, photoPathString);

        if (mCurrentProductUri == null) {

            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                Toast.makeText(this, "Error with saving product", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Product Saved", Toast.LENGTH_SHORT).show();
            }

        } else {

            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, "Error with updating product", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Product updated", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void deleteProduct() {
        // Only perform the delete if this is an existing product.
        if (mCurrentProductUri != null) {
            // Call the ContentResolver to delete the product at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentProductUri
            // content URI already identifies the product that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, "Error with deleting product", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, "Product Deleted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked the "Keep editing" button so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this product?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
                finish();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes.
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void requestPermissions() {

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(EditorActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(EditorActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }

        if (ContextCompat.checkSelfPermission(EditorActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(EditorActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(EditorActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(EditorActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {
                    requestPermissions();
                }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "me.joshvocal.inventory_app.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic();
        }
    }

    private void setPic() {
        File file = new File(mCurrentPhotoPath);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            mPictureImageView.setImageBitmap(bitmap);
        }
    }

    private void orderSupplier() {

        String supplierEmail = mEmailEditText.getText().toString();
        if (TextUtils.isEmpty(supplierEmail)) {
            return;
        }

        Intent email = new Intent(Intent.ACTION_SENDTO);
        email.setData(Uri.parse("mailto:")); // only email apps should handle this
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{supplierEmail});
        email.putExtra(Intent.EXTRA_SUBJECT, "New order for " + mNameEditText.getText().toString());
        email.putExtra(Intent.EXTRA_TEXT, "I would like to order a new shipment.");
        if (email.resolveActivity(getPackageManager()) != null) {
            startActivity(email);
        }
    }

    private void receiveShipment() {
        String quantityString = mQuantityEditText.getText().toString().trim();

        if (!TextUtils.isEmpty(quantityString)) {
            int quantity = Integer.parseInt(quantityString);
            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity + 10);

            getContentResolver().update(mCurrentProductUri, values, null, null);
        }
    }

    private void trackSale() {

        String quantityString = mQuantityEditText.getText().toString().trim();

        if (!TextUtils.isEmpty(quantityString)) {
            int quantity = Integer.parseInt(quantityString);
            ContentValues values = new ContentValues();

            if (quantity > 0) {
                values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity - 1);
            } else {
                values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
            }

            getContentResolver().update(mCurrentProductUri, values, null, null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_order_supplier:
                orderSupplier();
                break;
            case R.id.button_receive_shipment:
                receiveShipment();
                Toast.makeText(this, "Received Shipment", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_track_sale:
                trackSale();
                break;
        }
    }
}
