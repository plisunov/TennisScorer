package by.plisunov.tennis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.socket.config.annotation.*;

import javax.websocket.RemoteEndpoint;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class StompWebSocketConfiguration extends AbstractWebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/");
		config.setApplicationDestinationPrefixes("/tennisscorer");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/livescore").setAllowedOrigins("*");
	}
}
