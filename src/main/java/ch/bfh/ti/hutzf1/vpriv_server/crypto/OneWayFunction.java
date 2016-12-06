package ch.bfh.ti.hutzf1.vpriv_server.crypto;

import ch.bfh.unicrypt.UniCryptException;
import ch.bfh.unicrypt.helper.array.classes.ByteArray;
import ch.bfh.unicrypt.helper.hash.HashAlgorithm;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Fabian Hutzli
 */

public class OneWayFunction {
    public BigInteger getHash(BigInteger message, BigInteger key) throws NoSuchAlgorithmException, InvalidKeyException, UniCryptException {
        PedersenScheme ps = new PedersenScheme();
        ByteArray baMessage = ps.getElement(message).convertToByteArray();
        ByteArray baKey = ps.getElement(key).convertToByteArray();
        HashAlgorithm ha = HashAlgorithm.getInstance();
        
        ByteArray hash = ha.getHashValue(baMessage, baKey);
        return ps.getElement(hash).convertToBigInteger();
    }
}
