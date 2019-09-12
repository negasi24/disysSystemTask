package com.disys.systemtask.utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.disys.systemtask.R;
import com.disys.systemtask.RequestToWhomItMayConcernActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utilities {

    public static void showAlert(Context context, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                context, R.style.DialogStyle).create();
        alertDialog.setTitle("Alert");
        alertDialog.setCancelable(false);
        alertDialog.setMessage(message);
        alertDialog.setIcon(R.drawable.ic_warning);
        alertDialog.setButton("Submit",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog,
                                        int which) {
                       context.startActivity(new Intent(context, RequestToWhomItMayConcernActivity.class));
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    public static String dateFormat(String givenDate) throws ParseException {
        Date parsedDate = null;

        parsedDate = new SimpleDateFormat("dd-MM-yyyy").parse(givenDate);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM, yyyy", Locale.US);
        return dateFormatter.format(parsedDate);
    }

    public static String dateFormat(String givenDate, String format) throws ParseException {
        Date parsedDate = null;
        parsedDate = new SimpleDateFormat(format).parse(givenDate);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM, yyyy", Locale.US);
        return dateFormatter.format(parsedDate);
    }

}
