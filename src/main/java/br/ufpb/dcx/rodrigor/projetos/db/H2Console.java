package br.ufpb.dcx.rodrigor.projetos.db;

import org.h2.tools.Server;

public class H2Console {
    private Server webServer;

    public void start() {
        try {
            webServer = Server.createWebServer("-webPort", "8082", "-tcpAllowOthers").start();
            System.out.println("H2 Console iniciado em: http://localhost:8082");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (webServer != null) {
            webServer.stop();
        }
    }
}