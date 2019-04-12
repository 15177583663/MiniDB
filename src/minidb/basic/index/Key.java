package minidb.basic.index;

import java.io.IOException;
import java.io.RandomAccessFile;

abstract public class Key implements Comparable<Key> {

    public Key() { }

    abstract public int compareTo(Key k);

    abstract public int compareTo(Key k, boolean useAll);

    abstract public void writeToFile(RandomAccessFile fa) throws IOException;
}
