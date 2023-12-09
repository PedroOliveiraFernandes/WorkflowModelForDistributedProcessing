package isel.meic.tmf.configuration.models;

public class PortDto {
    public String name;
    public String type;
    public String channel;

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", channel='" + channel + '\'' +
                '}';
    }
}
