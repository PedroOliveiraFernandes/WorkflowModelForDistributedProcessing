package isel.meic.tmf.commands.launchWorkflow.nodeSuppliers;

class RoundRobinSupplier<T> {
    private Node<T> head;

    public RoundRobinSupplier(T[] values) {
        Node<T> auxNode;
        Node<T> previousNode = null;
        for (int i = 0; i < values.length; i++) {
            auxNode = new Node<>(values[i]);

            if (i == 0) {
                previousNode = auxNode;
                head = auxNode;
            }
            previousNode.next = auxNode;
            previousNode = auxNode;
            if (i >= values.length - 1) {
                auxNode.next = head;
            }
        }
    }

    public T get() {
        var result = head.value;
        head = head.next;
        return result;
    }
}
