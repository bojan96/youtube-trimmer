package org.unibl.etf.youtubetrimmer.api.stomp;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.unibl.etf.youtubetrimmer.api.service.AuthService;

@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements ChannelInterceptor {

    private final AuthService authService;
    private static final String AUTHORIZATION_HEADER = "authorization";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor sha = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (sha.getCommand() == StompCommand.CONNECT) {
            String token = sha.getFirstNativeHeader(AUTHORIZATION_HEADER);
            if (token != null && authService.isTokenValid(token)) {
                sha.setUser(authService.getPrincipalFromToken(token));
            }
        }
        return message;
    }
}
