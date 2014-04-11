package io.kickflip.sample;

import android.app.Activity;
import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Persist a Java Object to disk
 */
public class LocalPersistence {

    /**
     * @param context
     * @param object
     * @param filename
     */
    public static void writeObjectToFile(Context context, Object object, String filename) {
        ObjectOutputStream objectOut = null;
        try {
            FileOutputStream fileOut = context.openFileOutput(filename, Activity.MODE_PRIVATE);
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(object);
            fileOut.getFD().sync();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (objectOut != null) {
                try {
                    objectOut.close();
                } catch (IOException e) {
                    // do nowt
                }
            }
        }
    }


    /**
     * @param context
     * @param filename
     * @return
     */
    public static Object readObjectFromFile(Context context, String filename) {

        ObjectInputStream objectIn = null;
        Object object = null;
        try {

            FileInputStream fileIn = context.getApplicationContext().openFileInput(filename);
            objectIn = new ObjectInputStream(fileIn);
            object = objectIn.readObject();

        } catch (FileNotFoundException e) {
            // Do nothing
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (objectIn != null) {
                try {
                    objectIn.close();
                } catch (IOException e) {
                    // do nowt
                }
            }
        }

        return object;
    }

}
