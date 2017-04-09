public class CASEntry<T> {
    final DataReference<T> a; // что хотим поменять
    final T expect; // ожидаемое значение
    final T update; // на что хотим поменять

    CASEntry(DataReference<T> a, T expect, T update) {
        this.a = a;
        this.expect = expect;
        this.update = update;
    }
}
