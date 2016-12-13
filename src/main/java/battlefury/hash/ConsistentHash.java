package battlefury.hash;

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.SortedMap;

public class ConsistentHash<T> {

    private final IHashFunction hashFunction;
    private final int numberOfReplicas;
    private final SortedMap<Integer, T> circle = Maps.newTreeMap();

    public ConsistentHash(final IHashFunction hashFunction, final int numberOfReplicas, final Collection<T> nodes) {

        this.hashFunction = hashFunction;
        this.numberOfReplicas = numberOfReplicas;

        for (T node : nodes) {
            add(node);
        }
    }

    public void add(final T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.put(hashFunction.hash(node.toString() + i), node);
        }
    }

    public void remove(final T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.remove(hashFunction.hash(node.toString() + i));
        }
    }

    public T get(final Object key) {
        if (circle.isEmpty()) {
            return null;
        }
        int hash = hashFunction.hash(key.toString());
        if (!circle.containsKey(hash)) {
            SortedMap<Integer, T> tailMap = circle.tailMap(hash);
            if (tailMap.isEmpty()) {
                hash = circle.firstKey();
            } else {
                hash = tailMap.firstKey();
            }
        }
        return circle.get(hash);
    }

    public static void main(final String[] args) {
//        ArrayList<Integer> list = Lists.newArrayList();
//        ConsistentHash<Integer> consistentHash = new ConsistentHash(new MD5IHashFunction(), 5, list);
//        consistentHash.add(1);
//        consistentHash.add(2);
//        consistentHash.add(3);
//        Integer integer = consistentHash.get(1);
//        System.out.println(integer);
    }

}
