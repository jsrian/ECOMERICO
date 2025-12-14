package br.ufpb.dcx.rodrigor.projetos.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;

public final class Database {

    private Database() {}

    public static void init() {
        new File("./data").mkdirs();
        try (Connection conn = DriverManager.getConnection("jdbc:h2:file:./data/produtosdb;AUTO_SERVER=TRUE", "sa", "");
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS produtos (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        nome VARCHAR(255) NOT NULL,
                        preco DECIMAL(10,2) NOT NULL,
                        descricao VARCHAR(255),
                        image VARCHAR(255)
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
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inicializar banco H2 em arquivo", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:file:./data/produtosdb;AUTO_SERVER=TRUE", "sa", "");
    }

    public static void close() {
    }

}