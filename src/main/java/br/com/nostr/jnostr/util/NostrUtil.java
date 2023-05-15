package br.com.nostr.jnostr.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;

import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import br.com.nostr.jnostr.crypto.schnorr.Point;

public class NostrUtil {

    private NostrUtil(){}

    public static byte[] generatePrivateKey() {
        try {
            Security.addProvider(new BouncyCastleProvider());
            // ECNamedCurveParameterSpec ecsp = ECNamedCurveTable.getParameterSpec("curve25519");
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("ECDSA", "BC");
            kpg.initialize(new ECGenParameterSpec("secp256k1"), new SecureRandom());
            KeyPair processorKeyPair = kpg.genKeyPair();
            // System.out.println("private key: " + ((ECPrivateKey) processorKeyPair.getPrivate()).getS().toString(16));
            // System.out.println("public key:  " + toHex(((org.bouncycastle.jce.interfaces.ECPublicKey) processorKeyPair.getPublic()).getQ().getEncoded(true)));
            // System.out.println("public key:  " +  NostrUtil.bytesToHex(((org.bouncycastle.jce.interfaces.ECPublicKey) processorKeyPair.getPublic()).getQ().getEncoded(true)));
            
            return bytesFromBigInteger(((ECPrivateKey) processorKeyPair.getPrivate()).getS());
        
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] genPubKey(byte[] secKey) {
        try {
            BigInteger x = NostrUtil.bigIntFromBytes(secKey);
            if (!(BigInteger.ONE.compareTo(x) <= 0 && x.compareTo(Point.getn().subtract(BigInteger.ONE)) <= 0)) {
                throw new Exception("The secret key must be an integer in the range 1..n-1.");
            }
            Point ret = Point.mul(Point.G, x);
            return bytesFromBigInteger(ret.getX());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b: data) sb.append(String.format("%02x", b&0xff));
        return sb.toString();
    }

    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        // return Base64.getEncoder().encodeToString(hash).toLowerCase();

        // return new BigInteger(1, hash).toString(16);
        
        return hexString.toString().toLowerCase();
    }

    public static String bytesToHex(String hash) {
        return bytesToHex(hash.getBytes(StandardCharsets.UTF_8));
    }
    
    public static byte[] sha256(byte[] b) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(b);
    }


    public static byte[] sha256(String string) {
        try {
            return sha256(string.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] sha512(String string) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        return digest.digest(string.getBytes(StandardCharsets.UTF_8));
    }

    public static String sig(String id, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        // Signature privateSignature = Signature.getInstance("SHA256withRSA");
        // privateSignature.initSign(kf.generatePrivate(spec));
        // privateSignature.update(input.getBytes("UTF-8"));
        // byte[] s = privateSignature.sign();
        // return Base64.getEncoder().encodeToString(s);


        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);

        byte[] messageBytes = sha256(id);

            signature.update(messageBytes);
            byte[] digitalSignature = signature.sign();

            return bytesToHex(digitalSignature);
        
    }


    public static Key getSecureRandomKey(String cipher, int keySize) {
        byte[] secureRandomKeyBytes = new byte[keySize / 8];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(secureRandomKeyBytes);
        return new SecretKeySpec(secureRandomKeyBytes, cipher);
    }

    public static byte[] bytesFromBigInteger(BigInteger n) {

        byte[] b = n.toByteArray();

        if (b.length == 32) {
            return b;
        } else if (b.length > 32) {
            return Arrays.copyOfRange(b, b.length - 32, b.length);
        } else {
            byte[] buf = new byte[32];
            System.arraycopy(b, 0, buf, buf.length - b.length, b.length);
            return buf;
        }
    }

    public static BigInteger bigIntFromBytes(byte[] b) {
        return new BigInteger(1, b);
    }

    public static byte[] xor(byte[] b0, byte[] b1) {

        if (b0.length != b1.length) {
            return null;
        }

        byte[] ret = new byte[b0.length];
        int i = 0;
        for (byte b : b0) {
            ret[i] = (byte) (b ^ b1[i]);
            i++;
        }

        return ret;
    }

    public static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] buf = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            buf[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return buf;
    }

    public static byte[] createRandomByteArray(int len) {
        byte[] b = new byte[len];
        new SecureRandom().nextBytes(b);
        return b;
    }

    public static String getJavaVersion() {
        String[] javaVersionElements = System.getProperty("java.runtime.version").split("\\.|_|-b");
        String main = "", major = "", minor = "", update = "", build = "";
        int elementsSize = javaVersionElements.length;
        if (elementsSize > 0) {main = javaVersionElements[0];}
        if (elementsSize > 1) {major   = javaVersionElements[1];}
        if (elementsSize > 2) {minor   = javaVersionElements[2];}
        if (elementsSize > 3) {update  = javaVersionElements[3];}
        if (elementsSize > 4) {build   = javaVersionElements[4];}
        return "main: " + main + " major: " + major + " minor: " + minor + " update: " + update + " build: " + build;
    }
}
