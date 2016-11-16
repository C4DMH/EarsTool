package com.radicalninja.logger;

import android.text.TextUtils;
import android.util.Log;

import com.menny.android.anysoftkeyboard.BuildConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileWriter {

    final boolean append, encryptionEnabled;
    final String filePath;

    BufferedWriter writer;

    public FileWriter(final String filePath, final boolean append) throws IOException {
        this(filePath, append, BuildConfig.USE_ENCRYPTION);
    }

    private FileWriter(final String filePath, final boolean append, final boolean encryptionEnabled)
            throws IOException {
        this.filePath = filePath;


        File fileNew = new File(filePath);
        try{
            fileNew.delete();
        }catch (Exception e){
            Log.d("File", "Could not delete");
        }



        this.append = append;
        this.encryptionEnabled = encryptionEnabled;
        openFileWriter();
        Log.d("Log", "This is FileWriter 1 and file path is:" + filePath);
    }

    private void openFileWriter() throws IOException {
        synchronized (this) {

//            File newFile = new File(filePath);
//            newFile.clo
            Log.d("Log", "2 This is FileWriter 1 and file path is:" + filePath);
            final File file = getFile();
            Log.d("Log", "This is FileWriter 2");
            Log.d("Log", "This encryption is enabled? : " + encryptionEnabled);

            writer = encryptionEnabled ?
                    CipherUtils.flushableEncryptedBufferedWriter(file, append) :
                    new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            Log.d("Log", "This is FileWriter 3");

        }
    }

    public File getFile() {
        return (!TextUtils.isEmpty(filePath)) ? new File(filePath) : null;
    }


//    public File getFile() {
//        if(!TextUtils.isEmpty(filePath)){
//            Log.d("FileWriter", "1");
//            return new File(filePath);
//        }
//        else{
//            Log.d("FileWrite", "2");
//            return new File(filePath);
//        }
//    }



    public void write(final String str) throws IOException {
        synchronized (this) {
            if (writer != null) {
                writer.write(str);
                writer.flush();
            } else {
                throw new IOException(
                        String.format("File writer does not exist for {%s}", filePath));
            }
        }
    }

    public void close() throws IOException {
        synchronized (this) {
            if (writer != null) {
                writer.close();
            } else {
                throw new IOException(
                        String.format("File writer does not exist for {%s}", filePath));
            }
        }
    }

    private boolean isEmpty(final File file) {
        synchronized (this) {
            return encryptionEnabled ? CipherUtils.isEncryptedFileEmpty(file) : file.length() == 0;
        }
    }

    public File exportFile(final String exportedFilename) throws IOException {
        synchronized (this) {
            final File currentFile = getFile();
            if (isEmpty(currentFile)) {
                return null;
            }
            close();
            final File newFile = new File(currentFile.getParent(), exportedFilename);
            final boolean renamed = currentFile.renameTo(newFile);
            openFileWriter();
            if (renamed) {
                return newFile;
            } else {
                throw new IOException(
                        String.format("File renaming operation failed for {%s}", filePath));
            }
        }
    }

}
