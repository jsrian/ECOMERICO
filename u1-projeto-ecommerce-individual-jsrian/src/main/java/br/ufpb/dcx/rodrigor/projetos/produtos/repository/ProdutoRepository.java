package br.ufpb.dcx.rodrigor.projetos.produtos.repository;

import br.ufpb.dcx.rodrigor.projetos.db.Database; // Importe sua classe Database
import br.ufpb.dcx.rodrigor.projetos.produtos.model.Produto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoRepository {

    private Connection conn;

    public ProdutoRepository(){
        this.conn = Database.getConnection();
        criarTabela();
    }

    public ProdutoRepository(Connection conn){
        this.conn = conn;
        criarTabela();
    }

    // Método auxiliar para criar tabela (Centralizado)
    private void criarTabela() {
        try (Statement stmt = conn.createStatement()){
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS produtos(
                id IDENTITY PRIMARY KEY,
                nome VARCHAR(255) NOT NULL,
                preco DECIMAL(10,2) NOT NULL,  -- CORREÇÃO: Decimal para dinheiro
                descricao VARCHAR(255) NOT NULL);
                """);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabela produtos", e);
        }
    }

    public Produto salvarProduto(Produto p){
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO produtos (nome, descricao, preco) VALUES (?,?,?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getDescricao());
            stmt.setBigDecimal(3, p.getPreco());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                p.setId(keys.getString(1));
            }
            return p;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar produto", e);
        }
    }

    public List<Produto> listarProdutos(){
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT id, nome, descricao, preco FROM produtos"
        )) {
            ResultSet rs = stmt.executeQuery();
            List<Produto> produtos = new ArrayList<>();
            while (rs.next()){
                Produto p = new Produto();
                p.setId(rs.getString("id"));
                p.setNome(rs.getString("nome"));
                p.setDescricao(rs.getString("descricao"));
                p.setPreco(rs.getBigDecimal("preco"));
                produtos.add(p);
            }
            return produtos;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos", e);
        }
    }

    public void removerProduto(String id){
        if (id == null){
            throw new IllegalArgumentException("ID do produto invalido");
        }
        String sql = "DELETE FROM produtos WHERE id = ?";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover produto", e);
        }
    }
}