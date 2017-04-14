import javafx.util.Pair;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ArrayStackTest2 {
    ArrayStack<Integer> int_stack = new ArrayStack<>(1000);
    ArrayStack<Pair<Integer, Integer>> pair_stack = new ArrayStack<>(1000);
    
    
    @Test
    public void testIntSimple() {
        assertThat(int_stack.push(0), is(0));
        assertThat(int_stack.push(1), is(1));
        assertThat(int_stack.push(2), is(2));
        assertThat(int_stack.pop(), is(2));
        assertThat(int_stack.pop(), is(1));
        assertThat(int_stack.push(3), is(3));
        assertThat(int_stack.pop(), is(3));
    }

    @Test
    public void testInt() {
        int n = 1000;

        for (int i = 0; i < n; i++) {
            assertThat(int_stack.push(i), is(i));
        }

        for (int i = n - 1; i >= 0; i--) {
            assertThat(int_stack.pop(), is(i));
        }
    }

    @Test
    public void testPairSimple() {
        Pair<Integer, Integer> toPush1 = new Pair<>(1, 1),
                                toPush2 = new Pair<>(2, 2),
                                toPush3 = new Pair<>(3, 3);

        assertThat(pair_stack.pop(), is((Pair<Integer, Integer>) null));
        assertThat(pair_stack.push(toPush1), is(toPush1));
        assertThat(pair_stack.push(toPush2), is(toPush2));
        assertThat(pair_stack.push(toPush3), is(toPush3));
        assertThat(pair_stack.pop(), is(toPush3));
        assertThat(pair_stack.pop(), is(toPush2));
        assertThat(pair_stack.push(toPush3), is(toPush3));
        assertThat(pair_stack.pop(), is(toPush3));
        assertThat(pair_stack.pop(), is(toPush1));
        assertThat(pair_stack.pop(), is((Pair<Integer, Integer>) null));
        assertThat(pair_stack.pop(), is((Pair<Integer, Integer>) null));
        assertThat(pair_stack.push(toPush3), is(toPush3));
        assertThat(pair_stack.pop(), is(toPush3));
    }

    @Test
    public void testPair() {
        int n = 1000;

        for (int i = n - 1; i >= 0; i--) {
            assertThat(pair_stack.pop(), is((Pair<Integer, Integer>) null));
        }

        for (int i = 0; i < n; i++) {
            assertThat(pair_stack.push(new Pair<>(i, i)), is(new Pair<>(i, i)));
        }

        for (int i = n - 1; i >= 0; i--) {
            assertThat(pair_stack.pop(), is(new Pair<>(i, i)));
        }

        for (int i = n - 1; i >= 0; i--) {
            assertThat(pair_stack.pop(), is((Pair<Integer, Integer>) null));
        }
    }
}