public class CASNDescriptor {
    // всевозможнные статусы для нашего CASN
    enum Status {
        UNDECIDED,
        SUCCEEDED,
        FAILED
    }

    private final DataReference<Status> status = new DataReference<Status>(Status.UNDECIDED); // текущий статус
    private final CASEntry[] entries; // то, с чем мы работаем

    public CASNDescriptor(CASEntry... entries) {
        this.entries = entries;
    }

    //вот тут-то и происходит CASN
    boolean complete() {
        if(status.value == Status.UNDECIDED) {
            // новый статус сразу помечаем как SUCCEEDED, мы оптимисты
            Status newStatus = Status.SUCCEEDED;

            //работаем с каждым entry отдельно
            for(int i = 0; i < entries.length;) {
                CASEntry entry = entries[i];

                /*
                    Здесь происходит вся магия CASN, которая позволяет избежать проблемы АВА.
                    Как я понял, при попытке подменить значение в entry на текущий CASNDescriptor
                    с использованием RDCSS, мы как бы делаем Acquire на этой секции.
                    Т.е. RDCSS используется как неблокирующая альтернатива блокировкам.
                    Причем, благодаря совмещению двух дескрипторов мы делаем двухфазную "блокировку" и из-за этого и не возникает ABA проблемы.
                 */
                Object val = new RDCSSDescriptor(this.status, Status.UNDECIDED, entry.a, entry.expect, this).invoke();


                if(val instanceof CASNDescriptor) {
                    if(val != this) {
                        ((CASNDescriptor) val).complete();
                        continue; // с этим entry ничего не вышло, пытаемся сделать это снова
                    }
                } else {
                    if(val != entry.expect) {
                        // все плохо, прерываем цикл
                        // "я с тобой CASN не сделал, ты проверку не прошел" (с) oxxxymiron
                        newStatus = Status.FAILED;
                        break;
                    }
                }

                i++;
            }

            // если мы сами сделали всю работу, то меняем статус на новый - FAILED или SUCCEEDED
            this.status.CAS(Status.UNDECIDED, newStatus);
        }

        // проверочка на статус
        boolean succeeded = status.value == Status.SUCCEEDED;

        // меняем наши entry
        for(CASEntry entry : entries) {
            entry.a.CAS(this, succeeded ? entry.update : entry.expect);
        }

        // возвращаем true или false в зависимости от статуса
        return succeeded;
    }
}
