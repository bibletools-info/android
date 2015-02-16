package rawcomposition.bibletools.info.util;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 */
public class CacheUtil {

    public static void save(Context context, String fileName, String data) {

        if (context == null || fileName == null || data == null) {
            return;
        }

        File file = new File(context.getCacheDir(), fileName);

        try {
            file.createNewFile();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data.getBytes());

            fos.close();
        } catch (FileNotFoundException e) {
            Log.e(CacheUtil.class.getName(), e.getMessage(), e);
        } catch (IOException e) {
            Log.e(CacheUtil.class.getName(), e.getMessage(), e);
        }

    }

    public static String find(Context context, String fileName) {

        if (context == null || fileName == null) {
            return null;
        }

        String data = null;

        try {
            File cacheDir = new File(context.getCacheDir(), fileName);
            FileInputStream fis = new FileInputStream(cacheDir);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            byte[] bytes = new byte[1024];

            int bytesRead = -1;
            while ((bytesRead = fis.read(bytes)) != -1) {
                bos.write(bytes, 0, bytesRead);
            }
            data = new String(bos.toByteArray());

            bos.close();
            fis.close();
        } catch (FileNotFoundException e) {
            Log.w(CacheUtil.class.getName(), "Cache file [" + fileName + "] has not yet been instantiated.");
        } catch (IOException e) {
            Log.e(CacheUtil.class.getName(), e.getMessage(), e);
        }

        return data;
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            Log.e(CacheUtil.class.getName(), "Could not clear cache directory.");
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
