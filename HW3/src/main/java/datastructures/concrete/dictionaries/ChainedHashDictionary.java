package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;



import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @see IDictionary and the assignment page for more details on what each method should do
 */
public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private IDictionary<K, V>[] chains;
    private int sizeOfChains;
    private int numPairs;
    private int[] primeSizes;
    private int primeIndex;

    // You're encouraged to add extra fields (and helper methods) though!

    public ChainedHashDictionary() {
        this.sizeOfChains = 13;
        this.chains = makeArrayOfChains(13);
        this.numPairs = 0;
        this.primeSizes = new int[]{29, 61, 127, 251, 503, 1009, 2011, 4021, 8039, 16057, 32089, 64217, 120607,
                241517, 480019, 960049, 1820033, 3640081, 7280149, 14560283};
        this.primeIndex = 0;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain IDictionary<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int size) {
        // Note: You do not need to modify this method.
        // See ArrayDictionary's makeArrayOfPairs(...) method for
        // more background on why we need this method.
        return (IDictionary<K, V>[]) new IDictionary[size];
    }

    @Override
    public V get(K key) {
        int keyHash;
        if (key == null){
            keyHash = 0;
        } else {
            keyHash = Math.abs(key.hashCode() % sizeOfChains);
        }
        if (chains[keyHash] != null && chains[keyHash].containsKey(key)) {
            return chains[keyHash].get(key);
        }
        throw new NoSuchKeyException();
    }

    @Override
    public void put(K key, V value) {
        hashPut(chains, sizeOfChains, key, value);
        if ((numPairs / sizeOfChains) * 1.0 > (1)) {
            resizeChains();
        }
    }

    // hashes key and puts key and value into the hash dictionary.
    // pre: hashDict doesn't already contain the key. hashDict is an appropriate size
    // returns: number of pairs
    private void hashPut(IDictionary<K, V>[] hashDict, int sizeOfDict, K key, V value) {
        int keyHash;
        if (key == null){ // create keyhash for key
            keyHash = 0;
        } else {
            keyHash = Math.abs(key.hashCode() % sizeOfDict);
        }
        if (hashDict[keyHash] != null){ // array exists at index
            if (!hashDict[keyHash].containsKey(key)){ // array at index doesn't contain key (new PV)
                numPairs++;
            }
            hashDict[keyHash].put(key, value);
        } else {
            hashDict[keyHash] = new ArrayDictionary<>(); // array doesn't exist at index
            hashDict[keyHash].put(key, value); // create new pair
            numPairs++;
        }
    }

    private void resizeChains() {
        IDictionary<K, V>[] newChains = makeArrayOfChains(primeSizes[primeIndex]);
        numPairs = 0;
        for (int i = 0; i < sizeOfChains; i++) {
            if (chains[i] != null) {
                for (KVPair<K, V> pair : chains[i]) {
                    hashPut(newChains, primeSizes[primeIndex], pair.getKey(), pair.getValue());
                }
            }
        }
        chains = newChains;
        sizeOfChains = primeSizes[primeIndex];
        primeIndex++;
    }

    @Override
    public V remove(K key) {
        int keyHash;
        if (key == null){
            keyHash = 0;
        } else {
            keyHash = Math.abs(key.hashCode() % sizeOfChains);
        }
        if (chains[keyHash] != null && chains[keyHash].containsKey(key)) {
            numPairs--;
            V val = chains[keyHash].remove(key);
            if (chains[keyHash].size() == 0) {
                chains[keyHash] = null;
            }
            return val;
        }
        throw new NoSuchKeyException();
    }

    @Override
    public boolean containsKey(K key) {
        int keyHash;
        if (key == null){
            keyHash = 0;
        } else {
            keyHash = Math.abs(key.hashCode() % sizeOfChains);
        }
        return (chains[keyHash] != null && chains[keyHash].containsKey(key));
    }

    @Override
    public int size() {
        return numPairs;
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: you do not need to change this method
        return new ChainedIterator<>(this.chains, numPairs);
    }

    /**
     * Hints:
     *
     * 1. You should add extra fields to keep track of your iteration
     *    state. You can add as many fields as you want. If it helps,
     *    our reference implementation uses three (including the one we
     *    gave you).
     *
     * 2. Before you try and write code, try designing an algorithm
     *    using pencil and paper and run through a few examples by hand.
     *
     *    We STRONGLY recommend you spend some time doing this before
     *    coding. Getting the invariants correct can be tricky, and
     *    running through your proposed algorithm using pencil and
     *    paper is a good way of helping you iron them out.
     *
     * 3. Think about what exactly your *invariants* are. As a
     *    reminder, an *invariant* is something that must *always* be
     *    true once the constructor is done setting up the class AND
     *    must *always* be true both before and after you call any
     *    method in your class.
     *
     *    Once you've decided, write them down in a comment somewhere to
     *    help you remember.
     *
     *    You may also find it useful to write a helper method that checks
     *    your invariants and throws an exception if they're violated.
     *    You can then call this helper method at the start and end of each
     *    method if you're running into issues while debugging.
     *
     *    (Be sure to delete this method once your iterator is fully working.)
     *
     * Implementation restrictions:
     *
     * 1. You **MAY NOT** create any new data structures. Iterators
     *    are meant to be lightweight and so should not be copying
     *    the data contained in your dictionary to some other data
     *    structure.
     *
     * 2. You **MAY** call the `.iterator()` method on each IDictionary
     *    instance inside your 'chains' array, however.
     */
    private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
        private IDictionary<K, V>[] chains;
        private int seen;
        private int size;
        private int index;
        private Iterator<KVPair<K, V>> iter;

        public ChainedIterator(IDictionary<K, V>[] chains, int chainsSize) {
            this.chains = chains;
            seen = 0;
            size = chainsSize;
            index = -1;
            getNextIndex();
            if (hasNext()){
                iter = chains[index].iterator();
            }
        }

        @Override
        public boolean hasNext() {
            return (seen < size);
        }

        private void getNextIndex() {
            index++;
            while (chains[index] == null && hasNext()) {
                index++;
            }
        }

        @Override
        public KVPair<K, V> next() {
            if (!hasNext()){
                throw new NoSuchElementException();
            } else if (iter.hasNext()) {
                seen++;
                return iter.next();
            } else {
                getNextIndex();
                iter = chains[index].iterator();
                seen++;
                return iter.next();
            }
        }
    }
}
