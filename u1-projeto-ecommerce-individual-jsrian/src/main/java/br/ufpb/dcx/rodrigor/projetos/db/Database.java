package br.ufpb.dcx.rodrigor.projetos.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;

public final class Database {
    private static Connection conn;

    private Database() {}

    public static void init() {
        if (conn != null) return;
        try {
            new File("./data").mkdirs();

            conn = DriverManager.getConnection("jdbc:h2:file:./data/produtosdb;AUTO_SERVER=TRUE", "sa", "");

            try (Statement stmt = conn.createStatement()) {
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS produtos (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        nome VARCHAR(255) NOT NULL,
                        preco DECIMAL(10,2) NOT NULL,
                        descricao VARCHAR(255)
                    );
                    """);
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS carrinhos (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    );
                    """);
                stmt.execute("""
                   CREATE TABLE IF NOT EXISTS itens_carrinho (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        carrinho_id BIGINT NOT NULL,
                        produto_id BIGINT NOT NULL,
                        quantidade INT NOT NULL,
                        FOREIGN KEY (carrinho_id) REFERENCES carrinhos(id) ON DELETE CASCADE,
                        FOREIGN KEY (produto_id) REFERENCES produtos(id)
                   );
                   """);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inicializar banco H2 em arquivo", e);
        }
    }

    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = null;
                init();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar status da conexão", e);
        }
        return conn;
    }

    public static void close() {
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
            conn = null;
        }
    }

}