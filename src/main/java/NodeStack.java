/*
    Стэк на нодах, все вроде как работает и не страдает от ABA.
 */
public class NodeStack<T> {
    private class Node {
        private final T val;
        private final Node next;

        public Node(T val, Node next) {
            this.val = val;
            this.next = next;
        }

        public T get() {
            return val;
        }
    }

    private final DataReference<Node> head;
    private final DataReference<Integer> top;

    public NodeStack() {
        this.top = new DataReference<Integer>(0);
        this.head = new DataReference<Node>(new Node(null, null));
    }

    public int size() {
        return top.get();
    }

    public void push(T element) {
        Integer curtop;
        Node next;

        do {
            curtop = top.get();
            next = head.get();
        } while (!DataReference.CASN(
                new CASEntry<Integer>(top, curtop, curtop + 1),
                new CASEntry<Node>(head, next, new Node(element, next))
        ));
    }

    public T pop() {
        Integer curtop;
        Node ret;

        do {
            curtop = top.get();
            ret = head.get();
        } while (!DataReference.CASN(
                new CASEntry<Integer>(top, curtop, curtop - 1),
                new CASEntry<Node>(head, ret, ret.next)
        ));

        return ret.get();
    }
}
