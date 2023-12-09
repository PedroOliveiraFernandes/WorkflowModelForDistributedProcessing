package isel.meic.tmf.commands.launchWorkflow.nodeSuppliers;

public class Node<T> {
    public T value;
    public Node<T> next;

    public Node(T value) {
        this.value = value;
    }
}
