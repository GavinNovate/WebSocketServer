package net.novate.demo.websocketserver

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.handler.AbstractWebSocketHandler
import java.util.*
import java.util.concurrent.TimeUnit


@Configuration
@EnableWebSocket
class WebSocketConfig : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(Handler(), "/socket")
    }
}

class Handler : AbstractWebSocketHandler() {

    var disposables: MutableMap<String, Disposable> = mutableMapOf()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        println("Connected: ${session.id}")
        disposables[session.id] = Observable.interval(1, TimeUnit.SECONDS).subscribe {
            val message = "${Date()}"
            println("SendMessage: $message")
            session.sendMessage(TextMessage(message))
        }
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        println("ReceiveMessage: ${message.payload}")
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        println("Disconnected ${session.id}")
        disposables.remove(session.id)?.dispose()
    }
}