package odyssey.ru.linglibrary;

import java.util.*;

/**
 * Класс, позволяющий из указанного массива или списка по очереди случайным образом
 * выбрать все элементы так, чтобы каждый элемент был выбран только один раз.
 * Внимание ! При выборе элементов модифицирует порядок элементов в массиве !
 * Чтобы вернуть всё как было до начала выбора случайных элементов, нужно выполнить
 * revert() явно или вызвать close(), который выполнит revert() автоматически !
 *
 * Также позволяет выбирать элементы по индексу и перебирать все оставшиеся
 * невыбранными элементами, получив итератор.
 *
 * @author igor.kostromin
 *         04.02.14 13:21
 */
public class Picker<T> implements Iterable<T>, AutoCloseable {
    private static final Random random = new Random(  );

    private T[] array;
    private List<T> list;
    private List<Integer> usedIndexes = new ArrayList<>(  );
    private long version = 0;

    public Picker(T[] array) {
        if (null == array || 0 == array.length) throw new IllegalArgumentException( "array" );
        this.array = array;
    }

    public Picker(List<T> list) {
        if (null == list || list.isEmpty()) throw new IllegalArgumentException( "list" );
        this.list = list;
    }

    /**
     * Возвращает true, если в коллекции не осталось невыбранных элементов.
     */
    public boolean nothingToPick() {
        if (null != array) return array.length == usedIndexes.size();
        else return list.size() == usedIndexes.size();
    }

    /**
     * Возвращает следующий элемент случайным образом.
     * @throws IllegalStateException - если элементы закончились
     */
    public T pickRandom() {
        if (null != array) {
            int total = array.length;
            int used = usedIndexes.size();
            if ( used == total )
                throw new IllegalStateException( "All items are retrieved already" );
            //
            int index = random.nextInt( total - used );
            T result = array[index];
            array[index] = array[total - 1 - used];
            array[total - 1 - used] = result;
            usedIndexes.add( index );
            version++;
            return result;
        } else {
            int total = list.size();
            int used = usedIndexes.size();
            if ( used == total )
                throw new IllegalStateException( "All items are retrieved already" );
            //
            int index = random.nextInt( total - used );
            T result = list.get(index);
            list.set( index, list.get(total - 1 - used));
            list.set(total - 1 - used, result);
            usedIndexes.add( index );
            version++;
            return result;
        }
    }

    /**
     * Возвращает элемент по индексу, не удаляя его из пула доступных.
     * @throws IllegalStateException - если элементы закончились или если индекс лежит за пределами
     * доступной области (доступная область - от 0 до array.length - pickedCount - 1).
     */
    public T peekAt( int index) {
        if (null != array) {
            int total = array.length;
            int used = usedIndexes.size();
            if ( used == total )
                throw new IllegalStateException( "All items are retrieved already" );
            if (index >= total - used)
                throw new IllegalStateException( "Item at specified index is unavailable now" );
            //
            return array[index];
        } else {
            int total = list.size();
            int used = usedIndexes.size();
            if ( used == total )
                throw new IllegalStateException( "All items are retrieved already" );
            if (index >= total - used)
                throw new IllegalStateException( "Item at specified index is unavailable now" );
            //
            return list.get( index );
        }
    }

    /**
     * Возвращает элемент по индексу и сохраняет информацию об этом, чтобы
     * в следующий раз этот элемент уже не был доступен.
     * @throws IllegalStateException - если элементы закончились или если индекс лежит за пределами
     * доступной области (доступная область - от 0 до array.length - pickedCount - 1).
     */
    public T pickAt( int index ) {
        if (null != array) {
            int total = array.length;
            int used = usedIndexes.size();
            if ( used == total )
                throw new IllegalStateException( "All items are retrieved already" );
            if (index >= total - used)
                throw new IllegalStateException( "Item at specified index is unavailable now" );
            //
            T result = array[index];
            array[index] = array[total - 1 - used];
            array[total - 1 - used] = result;
            usedIndexes.add( index );
            version++;
            return result;
        } else {
            int total = list.size();
            int used = usedIndexes.size();
            if ( used == total )
                throw new IllegalStateException( "All items are retrieved already" );
            if (index >= total - used)
                throw new IllegalStateException( "Item at specified index is unavailable now" );
            //
            T result = list.get( index );
            list.set(index, list.get(total - 1 - used));
            list.set(total - 1 - used, result);
            usedIndexes.add( index );
            version++;
            return result;
        }
    }

    private class SafeIterator implements Iterator<T> {

        private long createdVersion;
        private int index;

        public SafeIterator(long createdVersion) {
            this.createdVersion = createdVersion;
        }

        @Override
        public boolean hasNext() {
            if (version != createdVersion)
                throw new IllegalStateException( "State of wrapped collection has been changed." );
            int totalSize = array != null ? array.length : list.size();
            return index < totalSize - usedIndexes.size();
        }

        @Override
        public T next() {
            if (version != createdVersion)
                throw new IllegalStateException( "State of wrapped collection has been changed." );
            if (!hasNext())
                throw new NoSuchElementException();
            T result = array != null ? array[index] : list.get( index );
            index++;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Возвращает итератор, который может быть использован для перебора
     * оставшихся в коллекции элементов. Но его можно использовать только до тех пор, пока
     * коллекция не будет изменена одним из вызовов
     * {@link #pickRandom()}, {@link #pickAt(int)}, {@link #revert()}, {@link #close()}.
     * В противном случае будет сгенерировано исключение {@link java.lang.IllegalStateException}.
     */
    @Override
    public Iterator<T> iterator() {
        return new SafeIterator(version );
    }

    /**
     * Возвращает количество выбранных элементов.
     */
    public int getPickedCount() {
        return usedIndexes.size();
    }

    /**
     * Восстанавливает изначальный порядок элементов массива (списка).
     */
    public void revert() {
        int total = null != array ? array.length : list.size();
        while (!usedIndexes.isEmpty()) {
            int used = usedIndexes.size();
            int index = usedIndexes.get( used - 1 );
            if (null != array) {
                T tmp = array[total - used];
                array[total - used] = array[index];
                array[index] = tmp;
            } else {
                T tmp = list.get(total - used);
                list.set(total - used, list.get(index));
                list.set(index, tmp);
            }
            usedIndexes.remove( used - 1 );
        }
        version++;
    }

    @Override
    public void close() {
        revert();
    }
}