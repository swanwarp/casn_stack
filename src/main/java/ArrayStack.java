/*
    Это стек на массиве, использующий CASN.
    Так как я не совсем понял, как сделать EnsureCapacity для данного стэка, не попортив ничего, я еще реализовал стэк на нодах.
 */
public class ArrayStack<T> {
    private final DataReference<Integer> top = new DataReference<Integer>(0);
    private final DataReference<T>[] data;

    ArrayStack(int capacity) {
        data = new DataReference[capacity];
        for(int i = 0; i < capacity; i++) {
            data[i] = new DataReference<T>(null);
        }
    }

    public int size() {
        return top.get();
    }

    public void push(T element) {
        Integer curtop;

        do {
            curtop = top.get();
        } while (!DataReference.CASN(
                new CASEntry<Integer>(top, curtop, curtop + 1),
                new CASEntry<T>(data[curtop], null, element)
        ));
    }

    public T pop() {
        Integer curtop;
        T ret;

        do {
            curtop = top.get();
            ret = data[curtop - 1].get();
        } while (!DataReference.CASN(
                new CASEntry<Integer>(top, curtop, curtop - 1),
                new CASEntry<T>(data[top.get() - 1], ret, null)
        ));

        return ret;
    }
}
