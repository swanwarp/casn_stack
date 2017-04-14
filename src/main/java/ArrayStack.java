/*
    Это стек на массиве, использующий CASN.
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

    public T push(T element) {
        Integer curtop;

        do {
            curtop = top.get();
        } while (!DataReference.CASN(
                new CASEntry<Integer>(top, curtop, curtop + 1),
                new CASEntry<T>(data[curtop], null, element)
        ));

        return element;
    }

    public T pop() {
        Integer curtop;
        T ret;

        while(true) {
            try {
                // пытаемся сделать pop()
                do {
                    curtop = top.get();

                    if (curtop == 0) {
                        return null; // если стек пуст, возвращаем null
                    }

                    ret = data[curtop - 1].get();

                } while (!DataReference.CASN(
                        new CASEntry<Integer>(top, curtop, curtop - 1),
                        new CASEntry<T>(data[top.get() - 1], ret, null) // здесь из-за top.get() - 1 может вылететь ArrayIndexOutOfBoundException
                                                                        // поэтому мы пытаемся зайти в этот цикл еще раз после того, как поймали эксепшн
                ));

                return ret;

            } catch (ArrayIndexOutOfBoundsException e) {
                continue;
            }
        }
    }
}
