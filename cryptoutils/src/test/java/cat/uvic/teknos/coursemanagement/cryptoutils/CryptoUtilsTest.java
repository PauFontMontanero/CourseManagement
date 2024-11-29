package cat.uvic.teknos.coursemanagement.cryptoutils;

import org.junit.jupiter.api.Test;

import java.security.KeyPairGenerator;

import static org.junit.jupiter.api.Assertions.*;

class CryptoUtilsTest {
    @Test
    void getHash() {
        var text = "Some text...";
        var base64Text = "quonJ6BjRSC1DBOGuBWNdqixj8z20nuP+QH7cVvp7PI=";
        assertEquals(base64Text, CryptoUtils.getHash(text));
    }

    @Test
    void createSecretKey() {
        var secretKey = CryptoUtils.createSecretKey();
        assertNotNull(secretKey);
        var bytes = secretKey.getEncoded();
        System.out.println(CryptoUtils.toBase64(bytes));
    }

    @Test
    void decodeSecretKey() {
        var secretKeyBase84 = "jaruKzlE7xerbNSjxiVjZtuAeYWrcyMGsA8TaTqZ8AM=";
        var secretKey = CryptoUtils.decodeSecretKey(secretKeyBase84);
        assertNotNull(secretKey);
        assertEquals("AES", secretKey.getAlgorithm());
    }

    @Test
    void encrypt() {
        var plainText = "Test message";
        var secretKey = CryptoUtils.createSecretKey();
        var encrypted = CryptoUtils.encrypt(plainText, secretKey);
        assertNotNull(encrypted);
        assertNotEquals(plainText, encrypted);
    }

    @Test
    void decrypt() {
        var plainText = "Test message";
        var secretKey = CryptoUtils.createSecretKey();
        var encrypted = CryptoUtils.encrypt(plainText, secretKey);
        var decrypted = CryptoUtils.decrypt(encrypted, secretKey);
        assertEquals(plainText, decrypted);
    }

    @Test
    void asymmetricEncryptAndDecrypt() throws Exception {
        var keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        var keyPair = keyGen.generateKeyPair();

        var plainText = CryptoUtils.toBase64("Test message".getBytes());
        var encrypted = CryptoUtils.asymmetricEncrypt(plainText, keyPair.getPublic());
        var decrypted = CryptoUtils.asymmetricDecrypt(encrypted, keyPair.getPrivate());

        assertEquals(plainText, decrypted);
    }
}