package br.ufpb.dcx.rodrigor.projetos.db;
import br.ufpb.dcx.rodrigor.projetos.produtos.model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ProdutoDAO {

    public void adicionar(Produto p) {
        String sql = "INSERT INTO produtos (nome, preco, descricao) VALUES (?, ?, ?)";
        Connection conn = Database.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
             
            stmt.setString(1, p.getNome());
            stmt.setBigDecimal(2, p.getPreco());
            stmt.setString(3, p.getDescricao());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar produto", e);
        }
    }
    public void remover(String id) {
        String sql = "DELETE FROM produtos WHERE id = ?";
        Connection conn = Database.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover produto", e);
        }
    }

    public List<Produto> listar() {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM produtos";

        Connection conn = Database.getConnection();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Produto p = new Produto();
                p.setId(rs.getString("id"));
                p.setNome(rs.getString("nome"));
                p.setPreco(rs.getBigDecimal("preco"));
                p.setDescricao(rs.getString("descricao"));
                lista.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos", e);
        }
        return lista;
    }
}