package com.example.a30467984.deaddyspy.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.a30467984.deaddyspy.HistoryManagerActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by 30467984 on 2/19/2019.
 */

public class CsvCreator {
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private static final String FILE_EXTENSION = ".csv";
    //CSV file header
    private static final String FILE_HEADER = "id,firstName,lastName,gender,age";
    private static Context context;
    public CsvCreator(Context context){
        this.context = context;
    }

    public static void createCsvFile(String fileName,List<String> header,ArrayList tripData) {


        FileWriter fileWriter = null;

        try {
            File dir = context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            File csvFile = new File(dir +"/"+ fileName + FILE_EXTENSION);
            fileWriter = new FileWriter(csvFile);

            //Write the CSV file header
            StringBuilder headerString = new StringBuilder();
            for (Object o : header)
            {
                headerString.append(o.toString());
                headerString.append(",");
            }
            fileWriter.append(headerString);

            //Add a new line separator after the header
            fileWriter.append(NEW_LINE_SEPARATOR);

            for (int i = 1; i < tripData.size();i++) {
                HashMap<String, String> lineData = (HashMap<String, String>) tripData.get(i);
                StringBuilder lineString = new StringBuilder();
                for (String col : header)
                {
                    lineString.append(lineData.get(col));
                    lineString.append(",");
                }
                fileWriter.append(lineString);
                fileWriter.append(NEW_LINE_SEPARATOR);
            }

            //Write a new student object list to the CSV file



            System.out.println("CSV file was created successfully !!!");
            //Toast.makeText(context, "File " + fileName + " saved", Toast.LENGTH_LONG).show();
            final Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/*");
            //final File csvFile = new File(dir, fileName + FILE_EXTENSION);
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(csvFile));
            context.startActivity(Intent.createChooser(shareIntent, "Share saved data"));
         //   csvFile.delete();
        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {

            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }

        }
    }
}
