package com.clubdevfire.booklibrary.document;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

public class DocumentUtils {
    public static final String FILE_NAME = "DocumentosMarcados.list";
    private static final HashSet<Integer> mDocumentsChecks = new HashSet<>();

    public static void checkAdd(int id, Context context) throws IOException {
        mDocumentsChecks.add(id);
        checkSave(context);
    }

    public static void checkRemove(int id, Context context) throws IOException {
        mDocumentsChecks.remove(id);
        checkSave(context);
    }

    public static void checkSave(Context context) throws IOException {
        FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
        for (Integer mDocumentsCheck : mDocumentsChecks) {
            fos.write((mDocumentsCheck + "\n").getBytes());
        }
    }

    public static void checkLoad(Context context) throws IOException {
        mDocumentsChecks.clear();
        FileInputStream fis = context.openFileInput(FILE_NAME);
        InputStreamReader inputStreamReader;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
        }else{
            inputStreamReader = new InputStreamReader(fis);
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        if(reader.ready()){
            String line = reader.readLine();
            while (line != null) {
                mDocumentsChecks.add(Integer.parseInt(line));
                line = reader.readLine();
            }
        }
        fis.close();
    }

    public static boolean CheckIsChecked(int id) {
        return mDocumentsChecks.contains(id);
    }

}
