package id.penawaran.penerimaan;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Extract PrivateKey, PublicKey, and X509Certificate from a DER encoded byte
 * array or file. Usually generated from openssl
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public final class DerUtils {

    static {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    private DerUtils() {
    }

    public static PrivateKey decodePrivateKey(InputStream is)
            throws IOException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        final DataInputStream dis = new DataInputStream(is);
        byte[] keyBytes = new byte[dis.available()];
        dis.readFully(keyBytes);
        dis.close();

        final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        final KeyFactory kf = KeyFactory.getInstance("RSA", "BC");

        return kf.generatePrivate(spec);
    }

    public static PublicKey decodePublicKey(byte[] der) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        final X509EncodedKeySpec spec = new X509EncodedKeySpec(der);
        final KeyFactory kf = KeyFactory.getInstance("RSA", "BC");

        return kf.generatePublic(spec);
    }

    public static X509Certificate decodeCertificate(InputStream is) throws IOException, CertificateException, NoSuchProviderException {
        final CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
        final X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
        is.close();

        return cert;
    }

    public static PrivateKey decodePrivateKey(byte[] der, String algorithm) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(der);
        KeyFactory kf = KeyFactory.getInstance(algorithm, "BC");
        return kf.generatePrivate(spec);
    }

}
