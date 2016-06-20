package com.sncf.itnovem.dotandroidapplication.utils;

import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Classe utilitaire permettant de simplifier la manipulation de m�thodes li�es � la s�curit� (hashage de mot de passe, par exemple) dans l'application.
 *
 */
public class SecurityUtils {

    /**
     * Constructeur privé de la classe.<br />
     * Il est <i>private</i> pour emp�cher son instanciation.
     */
    private SecurityUtils() {

    }

    /**
     * M�thode permettant d'hasher une cha�ne de caractères en SHA-256.
     *
     * @param passwordToHash La chaine à hasher.
     *
     * @return La cha�ne hash�e en SHA-256.
     */
    public static String sha256(String passwordToHash) {
        String generatedPassword = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();

            for(int i=0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Le SHA-256 n'est pas supporté.");
        }

        return generatedPassword;
    }

    /**
     * M�thode permettant d'hasher une cha�ne de caractères en SHA-512.
     *
     * @param passwordToHash La chaine à hasher.
     *
     * @return La cha�ne hash�e en SHA-512.
     */
//    public static String sha512(String passwordToHash) {
//        String generatedPassword = null;
//
//        MessageDigest md = null;
//        String saltedToken = "saltedToken";
//        try {
//            md = MessageDigest.getInstance("SHA-512");
//            md.update(passwordToHash.getBytes());
//            byte byteData[] = md.digest();
//            String base64 = Base64.encodeToString(byteData, Base64.NO_WRAP);
//        } catch (NoSuchAlgorithmException e) {
//            Log.w("Security", "Could not load MessageDigest: SHA-512");
//            return "";
//        }
//        return generatedPassword;
//    }

    private static String sha512(String passwordToHash) throws NoSuchAlgorithmException {
        String salt = getSalt();
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes());
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    private static String getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA512.");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt.toString();
    }
}