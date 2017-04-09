import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class DataReference<T> {
    volatile Object value; // поле для объекта
    private static final AtomicReferenceFieldUpdater<DataReference, Object>
            VALUE_UPDATER = AtomicReferenceFieldUpdater.newUpdater(DataReference.class, Object.class, "value"); // штука которая позволяет делать CAS

    public DataReference(Object value) {
        this.value = value;
    }

    // получаем наше значение, если видим какие-либо дескрипторы, то помогаем им завершиться
    public T get() {
        while(true) {
            Object curval = value;

            if(curval instanceof RDCSSDescriptor) {
                ((RDCSSDescriptor) curval).complete();
                continue;
            }

            if(curval instanceof CASNDescriptor) {
                ((CASNDescriptor) curval).complete();
                continue;
            }

            return (T) curval;
        }
    }

    // делаем CAS
    boolean CAS(Object expect, Object update) {
        return VALUE_UPDATER.compareAndSet(this, expect, update);
    }

    // пытаемся сделать CAS и возвращаем значение, которое лежит в value
    Object getAndCas(Object expect, Object update) {
        do {
            Object curval = value;
            if(curval != expect) {
                return curval;
            }
        } while (!CAS(expect, update));

        return expect;
    }

    // собственно, CASN, ради него и стараемся
    public static boolean CASN(CASEntry... entries) {
        return new CASNDescriptor(entries).complete();
    }
}
