package isel.meic.tmf.token;

public class TaskResult {
    public final String result;
    public final int index;

    public TaskResult(String result, int index) {
        this.result = result;
        this.index = index;
    }

    @Override
    public String toString() {
        return "TaskResult{" +
                "result='" + result + '\'' +
                ", index=" + index +
                '}';
    }
}
