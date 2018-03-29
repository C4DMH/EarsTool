/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.radicalninja.logger;

import android.util.Log;

import com.sevencupsoftea.ears.BuildConfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/*
Created by gwicks

*/

public class CipherUtils {

    private static SecretKeySpec key = new SecretKeySpec(BuildConfig.LOG_CRYPTO_BYTES, "AES");
    private static int HEADER_LENGTH = BuildConfig.LOG_CRYPTO_BYTES.length;

    static BufferedWriter flushableEncryptedBufferedWriter(File file, boolean append) throws IOException {
        FlushableCipherOutputStream fcos = new FlushableCipherOutputStream(file, key, append, false);
        return new BufferedWriter(new OutputStreamWriter(fcos, "UTF-8"));
    }

    static InputStream readerEncryptedByteStream(File file) throws
            IOException, NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException {
        Log.d("LOG", "This is CipherUtils 1");
        FileInputStream fin = new FileInputStream(file);
        byte[] iv = new byte[16];
        byte[] headerBytes = new byte[HEADER_LENGTH];
        if (fin.read(headerBytes) < HEADER_LENGTH) {
            throw new IllegalArgumentException("Invalid file length (failed to read the file header)");
        }
        if (headerBytes[0] != 100) {
            throw new IllegalArgumentException("The file header does not conform to our encrypted format.");
        }
        if (fin.read(iv) < 16) {
            throw new IllegalArgumentException("Invalid file length (needs a full block for iv)");
        }
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return new CipherInputStream(fin, cipher);
    }

    static BufferedReader readerEncrypted(File file) throws
            InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException,
            NoSuchPaddingException, IOException {

        InputStream cis = readerEncryptedByteStream(file);
        return new BufferedReader(new InputStreamReader(cis));
    }

    public static boolean isEncryptedFileEmpty(final File file) {
        return file.length() <= 16 + HEADER_LENGTH;
    }

    public void test(File zfilename) throws Exception {

        BufferedWriter cos = flushableEncryptedBufferedWriter(zfilename, false);
        cos.append("Sunny ");
        cos.append("and green.  \n");
        cos.close();

        int spaces = 0;
        for (int i = 0; i < 10; i++) {
            cos = flushableEncryptedBufferedWriter(zfilename, true);
            for (int j = 0; j < 2; j++) {
                cos.append("Karelia and Tapiola" + i);
                for (int k = 0; k < spaces; k++) {
                    cos.append(" ");
                }
                spaces++;
                cos.append("and other nice things.  \n");
                cos.flush();
                tail(zfilename);
            }
            cos.close();
        }

        BufferedReader cis = readerEncrypted(zfilename);
        String msg;
        while ((msg = cis.readLine()) != null) {
            System.out.println(msg);
        }
        cis.close();
    }

    private void tail(File filename) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, InvalidKeyException, IOException {

        BufferedReader infile = readerEncrypted(filename);
        String last = null, secondLast = null;
        do {
            String msg = infile.readLine();
            if (msg == null)
                break;
            if (!msg.startsWith("}")) {
                secondLast = last;
                last = msg;
            }
        } while (true);
        if (secondLast != null) {
            System.out.println(secondLast);
        }
        System.out.println(last);
        System.out.println();
    }

}
