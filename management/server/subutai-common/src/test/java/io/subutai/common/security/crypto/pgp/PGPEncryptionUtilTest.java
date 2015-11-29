package io.subutai.common.security.crypto.pgp;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyConverter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;


public class PGPEncryptionUtilTest
{
    private static final Logger logger = LoggerFactory.getLogger( PGPEncryptionUtilTest.class );

    private static final String MESSAGE = "hello";
    private static final String PUBLIC_KEYRING = "dummy.pkr";
    private static final String SECRET_KEYRING = "dummy.skr";

    private static final String SECRET_PWD = "12345678";
    private static final String PUBLIC_KEY_ID = "e2451337c277dbf1";
    private static final String SECRET_KEY_ID = "d558f9a4a0b450b3";
    private static final String USER_ID = "user@mail.org";
    private static final String PUBLIC_KEY_FINGERPRINT = "8338133EF14DE47D4B1646BEE2451337C277DBF1";
    private static final String SECRET_KEY_FINGERPRINT = "3E5DB4DCF15A31C93CF3C9D8D558F9A4A0B450B3";


    public static InputStream findFile( final String file )
    {
        return PGPEncryptionUtilTest.class.getClassLoader().getResourceAsStream( file );
    }


    @Test
    public void testSignVerify() throws Exception
    {
        PGPSecretKey secretKey = PGPEncryptionUtil.findSecretKeyById( findFile( SECRET_KEYRING ), SECRET_KEY_ID );
        byte[] signedMessage = PGPEncryptionUtil.sign( MESSAGE.getBytes(), secretKey, SECRET_PWD, true );

        assertTrue( PGPEncryptionUtil.verify( signedMessage, secretKey.getPublicKey() ) );
    }


    @Test
    public void testEncryptAndDecrypt() throws Exception
    {
        byte[] encryptedMessage = PGPEncryptionUtil.encrypt( MESSAGE.getBytes(),
                PGPEncryptionUtil.findPublicKeyById( findFile( PUBLIC_KEYRING ), PUBLIC_KEY_ID ), true );

        byte[] decryptedMessage = PGPEncryptionUtil.decrypt( encryptedMessage, findFile( SECRET_KEYRING ), SECRET_PWD );

        assertTrue( Arrays.equals( MESSAGE.getBytes(), decryptedMessage ) );
    }


    @Test
    public void testSignEncryptAndDecryptVerify() throws Exception
    {
        PGPSecretKey signingKey =
                PGPEncryptionUtil.findSecretKeyByFingerprint( findFile( SECRET_KEYRING ), SECRET_KEY_FINGERPRINT );
        PGPPublicKey encryptingKey =
                PGPEncryptionUtil.findPublicKeyByFingerprint( findFile( PUBLIC_KEYRING ), PUBLIC_KEY_FINGERPRINT );

        byte[] signedAndEncryptedMessage =
                PGPEncryptionUtil.signAndEncrypt( MESSAGE.getBytes(), signingKey, SECRET_PWD, encryptingKey, true );

        PGPSecretKey decryptingSecretKey = PGPEncryptionUtil.findSecretKeyByFingerprint( findFile( SECRET_KEYRING ),
                PGPEncryptionUtil.BytesToHex( encryptingKey.getFingerprint() ) );

        byte[] decryptedAndVerifiedMessage = PGPEncryptionUtil
                .decryptAndVerify( signedAndEncryptedMessage, decryptingSecretKey, SECRET_PWD,
                        signingKey.getPublicKey() );

        assertTrue( Arrays.equals( MESSAGE.getBytes(), decryptedAndVerifiedMessage ) );

        //auto secret key detection
        decryptedAndVerifiedMessage = PGPEncryptionUtil
                .decryptAndVerify( signedAndEncryptedMessage, findFile( SECRET_KEYRING ), SECRET_PWD,
                        signingKey.getPublicKey() );

        assertTrue( Arrays.equals( MESSAGE.getBytes(), decryptedAndVerifiedMessage ) );
    }


    @Test
    public void testGenerateKeyPair() throws Exception
    {
        KeyPair keyPair = PGPEncryptionUtil.generateKeyPair( USER_ID, SECRET_PWD, false );

        assertNotNull( PGPEncryptionUtil
                .findPublicKeyById( new ByteArrayInputStream( keyPair.getPubKeyring() ), keyPair.getSubKeyId() ) );
        assertNotNull( PGPEncryptionUtil
                .findSecretKeyByFingerprint( new ByteArrayInputStream( keyPair.getSecKeyring() ),
                        keyPair.getPrimaryKeyFingerprint() ) );
    }


    @Test
    public void testGetX509CertificateFromPgpKeyPair() throws Exception
    {

        Date today = new Date();
        PGPPublicKey pgpPublicKey = PGPEncryptionUtil.findPublicKeyById( findFile( PUBLIC_KEYRING ), PUBLIC_KEY_ID );
        PGPSecretKey pgpSecretKey = PGPEncryptionUtil.findSecretKeyById( findFile( SECRET_KEYRING ), SECRET_KEY_ID );
        X509Certificate x509Certificate = PGPEncryptionUtil
                .getX509CertificateFromPgpKeyPair( pgpPublicKey, pgpSecretKey, SECRET_PWD,
                        "C=ZA, ST=Western Cape, L=Cape Town, O=Thawte Consulting cc,"
                                + " OU=Certification Services Division,"
                                + " CN=Thawte Server CA/emailAddress=server-certs@thawte.com",
                        "C=US, ST=Maryland, L=Pasadena, O=Brent Baccala,"
                                + "OU=FreeSoft, CN=www.freesoft.org/emailAddress=baccala@freesoft.org",

                        today, new Date( today.getTime() + ( 1000 * 60 * 60 * 24 ) ), new BigInteger( "1" ) );

        assertNotNull( x509Certificate );


        JcaPGPKeyConverter c = new JcaPGPKeyConverter();
        PublicKey publicKey = c.getPublicKey( pgpSecretKey.getPublicKey() );
        x509Certificate.verify( publicKey, new BouncyCastleProvider() );
    }


    @Test
    public void testKeySigning() throws PGPException, IOException
    {
        KeyPair first = PGPEncryptionUtil.generateKeyPair( "first@key.com", "first", false );
        KeyPair second = PGPEncryptionUtil.generateKeyPair( "second@key.com", "second", false );
        KeyPair third = PGPEncryptionUtil.generateKeyPair( "third@key.com", "third", false );
        signKeyAndPrintIds( first, second, "second" );

        InputStream firstPublicStream = new ByteArrayInputStream( first.getPubKeyring() );
        InputStream thirdPublicStream = new ByteArrayInputStream( third.getPubKeyring() );

        PGPPublicKeyRingCollection keyrings =
                new PGPPublicKeyRingCollection( PGPUtil.getDecoderStream( firstPublicStream ),
                        new JcaKeyFingerprintCalculator() );

        PGPPublicKeyRing firstPublicKeyRing = null;
        if ( keyrings.getKeyRings().hasNext() )
        {
            firstPublicKeyRing = keyrings.getKeyRings().next();

            PGPPublicKey thirdPublicKey =
                    PGPEncryptionUtil.findPublicKeyById( thirdPublicStream, third.getPrimaryKeyId() );

            printPublicKeySignatures( firstPublicKeyRing.getPublicKey(), thirdPublicKey );

            InputStream secondPublicStream = new ByteArrayInputStream( second.getPubKeyring() );
            PGPPublicKey secondPublicKey =
                    PGPEncryptionUtil.findPublicKeyById( secondPublicStream, second.getPrimaryKeyId() );

            firstPublicKeyRing = PGPEncryptionUtil.removeSignature( firstPublicKeyRing, secondPublicKey );
            printPublicKeySignatures( firstPublicKeyRing.getPublicKey(), secondPublicKey );
        }
    }


    private void printKeyInArmoredFormat( KeyPair keyPair ) throws PGPException
    {
        //        logger.info( "*********************" );
        //        logger.info( keyPair.getSubKeyFingerprint() );
        String firstPublicArmored = PGPEncryptionUtil.armorByteArrayToString( keyPair.getPubKeyring() );
        String firstPrivateArmored = PGPEncryptionUtil.armorByteArrayToString( keyPair.getSecKeyring() );
        //        logger.info( String.format( "\n%s\n\n%s", firstPublicArmored, firstPrivateArmored ) );
    }


    private void printPublicKeySignatures( PGPPublicKey publicKey, final PGPPublicKey secondPublicKey )
    {
        //        logger.info( "@@@@@@@@@@@@@@@@@@@@@" );
        boolean verification = false;
        try
        {
            verification = PGPEncryptionUtil
                    .verifyPublicKey( publicKey, Long.toHexString( publicKey.getKeyID() ), secondPublicKey );
        }
        catch ( PGPException e )
        {
            e.printStackTrace();
        }
        logger.info( "%%%%%%%%%%%%% Signature verification: " + verification );
        Iterator keySignatures = publicKey.getSignatures();
        while ( keySignatures.hasNext() )
        {
            PGPSignature signature = ( PGPSignature ) keySignatures.next();
            signature.getSignatureType();
            logger.info( Long.toHexString( signature.getKeyID() ) );
        }
    }


    private void signKeyAndPrintIds( KeyPair first, KeyPair second, String password ) throws IOException, PGPException
    {
        InputStream firstPublicStream = new ByteArrayInputStream( first.getPubKeyring() );
        InputStream secondPublicStream = new ByteArrayInputStream( second.getPubKeyring() );
        InputStream secondSecretStream = new ByteArrayInputStream( second.getSecKeyring() );

        PGPPublicKeyRingCollection keyrings =
                new PGPPublicKeyRingCollection( PGPUtil.getDecoderStream( firstPublicStream ),
                        new JcaKeyFingerprintCalculator() );

        PGPPublicKeyRing firstPublicKeyRing = null;
        if ( keyrings.getKeyRings().hasNext() )
        {
            firstPublicKeyRing = keyrings.getKeyRings().next();


            PGPSecretKey secondSecretKey =
                    PGPEncryptionUtil.findSecretKeyById( secondSecretStream, second.getPrimaryKeyId() );
            PGPPublicKey secondPublicKey =
                    PGPEncryptionUtil.findPublicKeyById( secondPublicStream, second.getPrimaryKeyId() );

            if ( secondSecretKey != null )
            {
                String keyId = Long.toHexString( firstPublicKeyRing.getPublicKey().getKeyID() );

                PGPPublicKeyRing firstSignedPublicKeyRing =
                        PGPEncryptionUtil.signPublicKey( firstPublicKeyRing, keyId, secondSecretKey, password );

                //                printPublicKeySignatures( firstPublicKeyRing.getPublicKey(), secondPublicKey );
                printPublicKeySignatures( firstSignedPublicKeyRing.getPublicKey(), secondPublicKey );

                first.setPubKeyring( firstSignedPublicKeyRing.getEncoded() );
            }
        }
    }
}
