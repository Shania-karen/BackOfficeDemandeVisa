package mg.backoffice.controllers;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur pour les endpoints API génériques du système
 */
@RestController
@RequestMapping("/api")
public class ApiInfoController {

    /**
     * Endpoint pour récupérer les informations du serveur (IP, port, etc.)
     * Utile pour les QR codes qui doivent scanner depuis d'autres appareils
     */
    @GetMapping("/server-info")
    public ResponseEntity<Map<String, Object>> getServerInfo() {
        Map<String, Object> info = new HashMap<>();
        
        try {
            // Obtenir l'IP locale du serveur
            String hostName = InetAddress.getLocalHost().getHostName();
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            
            info.put("serverIp", hostAddress);
            info.put("serverHostName", hostName);
            info.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            info.put("error", "Impossible de récupérer l'adresse IP du serveur");
            info.put("message", e.getMessage());
            return ResponseEntity.status(500).body(info);
        }
    }

    /**
     * Endpoint de test pour vérifier que l'API est accessible
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Le serveur est opérationnel");
        return ResponseEntity.ok(response);
    }
}
