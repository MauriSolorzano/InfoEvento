package com.InfoEvento.Service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class SectorNotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public SectorNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notificarCambio(String sector) {
        String destino = "/topic/imagenes/" + sector;
        messagingTemplate.convertAndSend(destino, (Object) Map.of(
                "sector", sector,
                "timestamp", System.currentTimeMillis()
        ));
        System.out.println(">>> WebSocket notificado: " + destino);
    }
}

