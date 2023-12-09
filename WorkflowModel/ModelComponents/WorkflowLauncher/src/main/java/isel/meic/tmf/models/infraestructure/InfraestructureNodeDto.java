package isel.meic.tmf.models.infraestructure;

public class InfraestructureNodeDto {
    public String nodeName;
    public String ip;
    public int port = 80;

    public String getUri() {
        return ip + ":" + port;
    }
}
