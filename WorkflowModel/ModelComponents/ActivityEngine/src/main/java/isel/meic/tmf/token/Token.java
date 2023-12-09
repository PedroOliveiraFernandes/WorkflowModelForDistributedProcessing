package isel.meic.tmf.token;

public class Token {
    public String content;
    public int iteration;

    public Token(String content, int iteration) {
        this.content = content;
        this.iteration = iteration;
    }

    @Override
    public String toString() {
        return "{" +
                "content='" + content + '\'' +
                ", iteration=" + iteration +
                '}';
    }
}
