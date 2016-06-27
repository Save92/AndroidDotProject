package com.sncf.itnovem.dotandroidapplication.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by Journaud Nicolas on 20/04/16.
 */
/**
 * Classe utilitaire permettant de simplifier la manipulation de methodes liees a la securite (hashage de mot de passe, par exemple) dans l'application.
 *
 */
public class SecurityUtils {

    /**
     * Constructeur privé de la classe.
     * Il est private pour empecher son instanciation.
     */
    private SecurityUtils() {

    }

    /**
     * Methode permettant d'hasher une chaine de caractères en SHA-256.
     *
     * @param passwordToHash La chaine à hasher.
     *
     * @return La chaane hashee en SHA-256.
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