package isel.meic.tmf.ports;

import isel.meic.tmf.token.Token;

public class TokenMessageDetails {
    public  final Token token;
    public final long deliveryTag;

    public TokenMessageDetails(Token token, long deliveryTag) {
        this.token = token;
        this.deliveryTag = deliveryTag;
    }
}
