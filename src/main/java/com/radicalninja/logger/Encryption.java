package com.radicalninja.logger;

import android.os.Environment;

import com.anysoftkeyboard.utils.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by gwicks on 25/08/2016.
 */
public class Encryption {

    public final String TAG = "Encrypt";



//  throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException




    public String encrypt(String name, String path, String directoryName) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        //Toast.makeText(this,"Beginning encryption",  Toast.LENGTH_LONG).show();
        // Here you read the cleartext.

        //String directoryName = "/videoDIARY/";

        byte[] iv = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        //File mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + directoryName +  timeStamp + ".mp4");

        File newFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + directoryName);

        //boolean success = true;

        if(!newFolder.exists()){
            newFolder.mkdir();
        }
//        if(success == true){
//            android.util.Log.d(TAG, "encrypt: ");
//
//        }else{
//            android.util.Log.d(TAG, "encrypt: ");
//            exit(0);
//        }



        String final_path = Environment.getExternalStorageDirectory().getAbsolutePath() + directoryName +  name + ".encrypted";
        Log.d(TAG,"We are starting encrytopn!");
        //Toast.makeText(, "Encrypting.",  Toast.LENGTH_LONG).show();
        //File file = new File(path.getPath());
        //String final_path =
        FileInputStream fis = new FileInputStream(path);
        Log.d(TAG,"We are starting encrytopn 2!");
        // This stream write the encrypted text. This stream will be wrapped by another stream.
        FileOutputStream fos = new FileOutputStream(final_path);
        Log.d(TAG,"We are starting encrytopn 3!");

        // Length is 16 byte
        // Careful when taking user input!!! http://stackoverflow.com/a/3452620/1188357
        SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes("UTF-8"), "AES");


        //TRY NEW
        //String newPath = path + ".encrypted";
       // File mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + directoryName + timeStamp + ".mp4");
        //File newFile = new File(newPath);



        // Create cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, sks, ivSpec);
        // Wrap the output stream
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        // Write bytes
        int b;
        //byte[] d = new byte[8];
        byte[] d = new byte[1024];
        while((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }


        // Flush and close streams.
        cos.flush();
        cos.close();
        fis.close();
        Log.d(TAG,"We are starting encrytopn 4 - FINISHED ENCRYTPTION! file at path: " + final_path);


        //decrypt(name, final_path);
        return final_path;
    }


    public static void decrypt(String name, String path) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {


//        FileInputStream fis = new FileInputStream("data/encrypted");
//
//        FileOutputStream fos = new FileOutputStream("data/decrypted");


        String directoryName = "/videoDIARY/";

        byte[] iv = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        //File mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + directoryName +  timeStamp + ".mp4");

        String final_path = Environment.getExternalStorageDirectory().getAbsolutePath() + directoryName +  name + ".mp4";

        FileInputStream fis = new FileInputStream(path);

        FileOutputStream fos = new FileOutputStream(final_path);
        SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, sks, ivSpec);
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        int b;
        byte[] d = new byte[1024];
        while((b = cis.read(d)) != -1) {
            fos.write(d, 0, b);
        }
        fos.flush();
        fos.close();
        cis.close();
    }




}
