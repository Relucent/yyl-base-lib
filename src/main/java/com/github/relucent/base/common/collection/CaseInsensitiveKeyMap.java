package com.github.relucent.base.common.collection;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 忽略键大小写的 MAP <br>
 * 内部实现基于HashMap，不保证线程安全 <br>
 */
public class CaseInsensitiveKeyMap<V> implements Map<String, V> {

    // =================================Fields=================================================
    private final Map<Key, V> map;
    private transient Set<String> keySet;
    private transient Collection<V> values;

    // =================================Constructs=============================================
    /**
     * 构造函数
     */
    public CaseInsensitiveKeyMap() {
        map = new HashMap<>();
    }

    /**
     * 构造函数
     * @param initialCapacity 初始大小
     * @param loadFactor 负载因子
     */
    public CaseInsensitiveKeyMap(int initialCapacity, float loadFactor) {
        map = new HashMap<>(initialCapacity, loadFactor);
    }

    /**
     * 构造函数
     * @param sample 模板映射表
     */
    public CaseInsensitiveKeyMap(Map<? extends String, ? extends V> sample) {
        map = new HashMap<>(sample.size());
        putAll(sample);
    }

    // =================================Methods================================================
    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }

    @Override
    public V put(String key, V value) {
        return map.put(Key.getInstance(key), value);
    }

    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        for (Map.Entry<? extends String, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<String> keySet() {
        Set<String> ks = keySet;
        if (ks == null) {
            ks = new AbstractSet<String>() {
                public Iterator<String> iterator() {
                    return new Iterator<String>() {
                        private Iterator<Entry<String, V>> i = entrySet().iterator();

                        public boolean hasNext() {
                            return i.hasNext();
                        }

                        public String next() {
                            return i.next().getKey();
                        }

                        public void remove() {
                            i.remove();
                        }
                    };
                }

                public int size() {
                    return CaseInsensitiveKeyMap.this.size();
                }

                public boolean isEmpty() {
                    return CaseInsensitiveKeyMap.this.isEmpty();
                }

                public void clear() {
                    CaseInsensitiveKeyMap.this.clear();
                }

                public boolean contains(Object k) {
                    return CaseInsensitiveKeyMap.this.containsKey(k);
                }
            };
            keySet = ks;
        }
        return ks;
    }

    @Override
    public Collection<V> values() {
        Collection<V> vals = values;
        if (vals == null) {
            vals = new AbstractCollection<V>() {
                public Iterator<V> iterator() {
                    return new Iterator<V>() {
                        private Iterator<Entry<String, V>> i = entrySet().iterator();

                        public boolean hasNext() {
                            return i.hasNext();
                        }

                        public V next() {
                            return i.next().getValue();
                        }

                        public void remove() {
                            i.remove();
                        }
                    };
                }

                public int size() {
                    return CaseInsensitiveKeyMap.this.size();
                }

                public boolean isEmpty() {
                    return CaseInsensitiveKeyMap.this.isEmpty();
                }

                public void clear() {
                    CaseInsensitiveKeyMap.this.clear();
                }

                public boolean contains(Object v) {
                    return CaseInsensitiveKeyMap.this.containsValue(v);
                }
            };
            values = vals;
        }
        return vals;
    }

    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }

    // =================================InnerClass=============================================
    private static class EntrySet<V> extends AbstractSet<Entry<String, V>> {

        private final Set<Entry<Key, V>> entrySet;

        public EntrySet(Set<Map.Entry<Key, V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String, V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }

    private static class EntryIterator<V> implements Iterator<Entry<String, V>> {

        private final Iterator<Entry<Key, V>> iterator;

        public EntryIterator(Iterator<Entry<Key, V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String, V> next() {
            Entry<Key, V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }
    private static class EntryImpl<V> implements Entry<String, V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }
    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}
