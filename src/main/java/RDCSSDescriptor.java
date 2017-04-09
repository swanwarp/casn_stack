public class RDCSSDescriptor {
    private final DataReference a1; // контрольная секция
    private final Object expect1; // что хотим увидеть в контрольной секции
    private final DataReference a2; // данные которые хотим поменять
    private final Object expect2; // что хотим увидеть в этих данных
    private final Object update2; // и на что хотим их поменять

    RDCSSDescriptor(DataReference a1, Object expect1, DataReference a2, Object expect2, Object update2) {
        this.a1 = a1;
        this.expect1 = expect1;
        this.a2 = a2;
        this.expect2 = expect2;
        this.update2 = update2;
    }

    // здесь мы вызываем наш RDCSS
    // тут мы как бы блокируем наш RDCSS без использования блокировок
    Object invoke() {
        Object r;

        do {
            r = a2.getAndCas(expect2, this); // пытаемся получить a2 и положить туда текущий RDCSSDescriptor
            if(r instanceof RDCSSDescriptor) {
                ((RDCSSDescriptor) r).complete(); // если вместо expect2 получили дескриптор, то помогаем ему завершиться
            }
        } while(r instanceof RDCSSDescriptor); // продолжаем пока видим дескрипторы

        if(r == expect2) {
            complete(); // если все хорошо, завершаемся
        }

        return r;
    }

    // завершаем наш RDCSS, меняя дескриптор, который положили в референс (a2) на update2 или expect2, в зависимости от проверки a1 == expect1
    // в a1 у нас никогда не может оказаться дескриптор, поэтому не нужно заморачиваться с циклами проверки
    void complete() {
        if(a1.value == expect1) {
            a2.CAS(this, update2);
        } else {
            a2.CAS(this, expect2);
        }
    }
}
