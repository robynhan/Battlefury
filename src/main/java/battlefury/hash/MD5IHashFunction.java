package battlefury.hash;

import com.google.common.hash.Funnels;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

public class MD5IHashFunction implements IHashFunction {

    @Override
    public int hash(final String value) {
        HashFunction hashFunction = Hashing.md5();
        HashCode hashCode = hashFunction.newHasher()
                .putObject(value, Funnels.stringFunnel(Charset.defaultCharset()))
                .hash();
        return hashCode.hashCode();
    }
}
