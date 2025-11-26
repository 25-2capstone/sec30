package com.gmg.sec30.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Logger;

/**
 * 서버 시작 시 지정 포트가 이미 사용 중이면, 자동으로 사용 가능한 포트로 대체합니다.
 * 기본 활성화: true (server.autofreeport.enabled)
 */
@Configuration
public class ServerPortCustomizer implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    private static final Logger log = Logger.getLogger(ServerPortCustomizer.class.getName());

    @Value("${server.autofreeport.enabled:true}")
    private boolean autoFreeportEnabled;

    @Value("${server.port:${PORT:8081}}")
    private int configuredPort;

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        if (!autoFreeportEnabled) {
            return;
        }

        int targetPort = configuredPort;
        if (isPortInUse(targetPort)) {
            int free = findFreePort();
            if (free > 0) {
                log.warning(() -> String.format("Port %d is busy. Falling back to free port %d", targetPort, free));
                factory.setPort(free);
                System.setProperty("local.server.port", String.valueOf(free));
            } else {
                log.warning(() -> String.format("Port %d is busy and no free port found; keeping original.", targetPort));
            }
        } else {
            factory.setPort(targetPort);
        }
    }

    private boolean isPortInUse(int port) {
        try (ServerSocket s = new ServerSocket(port)) {
            s.setReuseAddress(true);
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    private int findFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        } catch (IOException e) {
            return -1;
        }
    }
}

