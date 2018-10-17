package macro.noteorganizer;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.services.s3.AmazonS3Client;

/*
    Written by Adam Robinson
 */

class Encrypter {
    private String id;
    private String plaintext;
    private static final int keyLength = 256;
    private final int itCount = 1000;
    private final int saltLength = keyLength / 8;
    private SecureRandom random = new SecureRandom();
    private String cipherText;
    private byte[] mIV;
    private byte[] mSalt;

    Encrypter(String ID, String text, Context context) {
        id = ID;
        plaintext = text;
        byte[] cipher = encrypt();

        try { cipherText = new String(cipher, "UTF-8"); }
        catch (UnsupportedEncodingException ex) { System.err.println(ex.getMessage()); }

        writeToFile(context);
    }

    private byte[] encrypt() {
        mSalt = new byte[saltLength];
        random.nextBytes(mSalt);
        byte[] ciphertext = new byte[plaintext.getBytes().length];

        KeySpec keySpec = new PBEKeySpec(id.toCharArray(), mSalt, itCount, keyLength);
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2withHmacSHA1");
            byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            mIV = new byte[cipher.getBlockSize()];
            random.nextBytes(mIV);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(mIV);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
            ciphertext = cipher.doFinal(plaintext.getBytes());
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeySpecException | InvalidKeyException
               | BadPaddingException | IllegalBlockSizeException ex) { System.err.println(ex.getMessage()); }

               return ciphertext;
    }

    void writeToFile(Context context) {
        File file = new File(context.getFilesDir(), id + "salt.txt");
        File file2 = new File(context.getFilesDir(), id + "iv.txt");
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            FileOutputStream outputStream2 = new FileOutputStream(file2);
            outputStream.write(getmSalt());
            outputStream.close();
            outputStream2.write(getmIV());
            outputStream2.close();
        }
        catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        uploadToS3(context);
    }

    void uploadToS3(Context context) {
        TransferUtility transferUtility = TransferUtility.builder()
                .context(context.getApplicationContext())
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .s3Client(new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider()))
                .build();

        TransferObserver uploadObserver = transferUtility.upload("protected/salt.txt",
                new File(context.getFilesDir() + id + "salt.txt"));

        uploadObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state)
                    Log.d("UPLOAD_COMP", "Upload complete");
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float)bytesCurrent / (float)bytesTotal) * 100;
                int percentDone = (int)percentDonef;

                Log.d("UPLOAD_PROGRESS", "ID:" + id + " bytesCurrent: "
                + bytesCurrent + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                System.err.println(ex.getMessage());
            }
        });

        if (TransferState.COMPLETED == uploadObserver.getState()) {
            Log.d("NOTE_ORG", "Transfer Completed");
        }

        Log.d("NOTE_ORG", "Bytes Transferred: " + uploadObserver.getBytesTransferred());
        Log.d("NOTE_ORG", "Bytes Total: " + uploadObserver.getBytesTotal());
    }

    // Accessors
    byte[] getmIV() { return mIV; }
    byte[] getmSalt() { return mSalt; }
    String getCipherText() { return cipherText; }
}
