package pl.chiveit.android.securenotes.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import android.content.Context;
import android.database.Cursor;
import android.util.Base64;
import android.util.Log;

public class CryptoService {

	public static final String TAG = "CryptoService";

	private static CryptoService mInstance = null;

	private SecretKeyFactory mKeyFactory = null;

	private Context mCtx;

	private String mHash;

	/**
	 * private constructor for singleton pattern usage
	 */
	private CryptoService(Context ctx) {
		try {
			mKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			this.mCtx = ctx;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

	}

	public static CryptoService getInstance(Context ctx) {
		if (mInstance == null) {
			mInstance = new CryptoService(ctx);
		}
		return mInstance;
	}

	/**
	 * calculates the SHA Message Digest from passed password and user name and
	 * returns the hash as a Base64 decoded string
	 * 
	 * @param passwdAndUsr
	 *            the password and user name concatenated to get the sha hash
	 *            from
	 */
	public boolean authenticate(String passwdAndUsr) {
		DBUtils db = null;

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			md.update(passwdAndUsr.getBytes("UTF-8"));

			byte[] data = md.digest();

			mHash = Base64.encodeToString(data, Base64.DEFAULT);

			int size = 0;

			db = DBUtils.getInstance(mCtx);

			Cursor entries = db.fetchAllEntries();
			if ((size = entries.getCount()) != 0) {

				int idx = new Random().nextInt(size);
				/* idx = (idx == 0) ? ++idx : idx; */

				entries.moveToPosition(idx);

				// try to decode the password
				if (decodeAES(entries.getString(entries
						.getColumnIndexOrThrow(DBUtils.KEY_PASSWD))) == null) {
					return false;
				}
			}

			return true;

		} catch (Exception e) {
			Log.e(TAG, "problem with authentication");
			return false;
		} finally {
			db.close();
		}
	}

	/**
	 * encrypts the data passed as parameter using the key derived from passed
	 * hash
	 * 
	 * @param data
	 *            data to be encrypted
	 * @return encrypted data, encoded as Base64 string
	 */
	public String encodeAES(String data) {

		try {

			KeySpec spec = new PBEKeySpec(mHash.toCharArray(),
					mHash.getBytes("UTF-8"), 256, 256);
			SecretKey tmp = mKeyFactory.generateSecret(spec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

			/* Encrypt the message. */
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

			cipher.init(
					Cipher.ENCRYPT_MODE,
					secret,
					new IvParameterSpec(Arrays.copyOf(mHash.getBytes("UTF-8"),
							16)));

			return Base64.encodeToString(
					cipher.doFinal(data.getBytes("UTF-8")), Base64.DEFAULT);

		} catch (Exception e) {
			Log.e(TAG, "unable to encode data " + e.getMessage(), e);
			return null;
		}

	}

	public String decodeAES(String data) {

		try {

			KeySpec spec = new PBEKeySpec(mHash.toCharArray(),
					mHash.getBytes("UTF-8"), 256, 256);
			SecretKey tmp = mKeyFactory.generateSecret(spec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			AlgorithmParameterSpec ivSpec = new IvParameterSpec(Arrays.copyOf(
					mHash.getBytes("UTF-8"), 16));

			cipher.init(Cipher.DECRYPT_MODE, secret, ivSpec);

			byte[] cipherText = Base64.decode(data, Base64.DEFAULT);

			cipherText = cipher.doFinal(cipherText);

			return new String(cipherText, "UTF-8");
		}

		catch (Exception e) {
			Log.e(TAG, "unable to decode data " + e.getMessage(), e);
			return null;
		}
	}

}
