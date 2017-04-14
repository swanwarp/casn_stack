import com.devexperts.dxlab.lincheck.Checker;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.ReadOnly;
import com.devexperts.dxlab.lincheck.annotations.Reload;
import com.devexperts.dxlab.lincheck.util.Result;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

// Тест на трех потоках, каждый поток выполняет от одного до трех действий.
// Если стек пустой, то pop() возвращает null, в результатах теста это фиксируется как -1.
@CTest(iter = 10, actorsPerThread = {"1:3", "1:3", "1:3"})
// Тест на пяти потоках, каждый поток выполняет от одного до двух действий.
@CTest(iter = 20, actorsPerThread = {"1:2", "1:2", "1:2", "1:2","1:2"})
public class ArrayStackTest1 {

    private ArrayStack<Integer> stack;
    private int size = 1000;

    @Reload
    public void reload() {
        stack = new ArrayStack<>(size);
    }

    @Operation(args = {"1:5"})
    public void push(Result res, Object[] args) throws Exception {
        Integer value = (Integer) args[0];
        res.setValue(stack.push(value));
    }

    @ReadOnly
    @Operation(args = {})
    public void pop(Result res, Object[] args) throws Exception {
        Object r = stack.pop();
        res.setValue(r == null ? -1 : r);
    }

    @Test
    public void test() throws Exception {
        assertTrue(Checker.check(new ArrayStackTest1()));
    }
}
