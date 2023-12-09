package isel.meic.tmf.token;

public class InputToken {

    private final String portName;
    private final Token token;

    public InputToken(String portName,Token token) {
        this.portName = portName;
        this.token = token;
    }

    public String getPortNameThatReceivedThisToken() {
        return portName;
    }

    public String getContent() {
        return token.content;
    }

    @Override
    public String toString() {
        return "InputToken{" +
                "portName='" + portName + '\'' +
                ", token=" + token +
                '}';
    }
}
