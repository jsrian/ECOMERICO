package br.ufpb.dcx.rodrigor.projetos.carrinho.repository;

import br.ufpb.dcx.rodrigor.projetos.carrinho.model.Carrinho;
import br.ufpb.dcx.rodrigor.projetos.carrinho.model.ItemCarrinho;
import br.ufpb.dcx.rodrigor.projetos.db.Database;
import br.ufpb.dcx.rodrigor.projetos.produtos.model.Produto;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarrinhoRepository {

    public void salvar(Carrinho carrinho) {
        String sqlCarrinho = "INSERT INTO carrinhos DEFAULT VALUES";
        String sqlItem = "INSERT INTO itens_carrinho (carrinho_id, produto_id, quantidade) VALUES (?, ?, ?)";

        Connection conn = Database.getConnection();

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sqlCarrinho, Statement.RETURN_GENERATED_KEYS)) {
                stmt.executeUpdate();

                var rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    carrinho.setId(rs.getString(1)); // Atualiza o objeto Java com o ID
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(sqlItem)) {
                for (ItemCarrinho item : carrinho.getItens()) {
                    stmt.setString(1, carrinho.getId()); // O ID do pai vai aqui
                    stmt.setString(2, item.getProduto().getId());
                    stmt.setInt(3, item.getQuantidade());
                    stmt.addBatch(); // Adiciona no pacote para enviar tudo de uma vez
                }
                stmt.executeBatch(); // Executa todos os inserts de itens
            }

            conn.commit();

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new RuntimeException("Erro ao salvar carrinho", e);
        } finally {
            try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    public List<ItemCarrinho> listarItens(String id){
        List<ItemCarrinho> lista = new ArrayList<>();

        String sql= """
        SELECT i.id AS item_id, i.quantidade,
               p.id AS produto_id, p.nome, p.preco, p.descricao
        FROM itens_carrinho i
        INNER JOIN produtos p ON i.produto_id = p.id
        WHERE i.carrinho_id = ?
    """;
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // 1. Montar o Produto (que veio do JOIN)
                    Produto p = new Produto();
                    p.setId(rs.getString("produto_id"));
                    p.setNome(rs.getString("nome"));
                    p.setPreco(rs.getBigDecimal("preco"));
                    p.setDescricao(rs.getString("descricao"));

                    // 2. Montar o Item
                    ItemCarrinho item = new ItemCarrinho();
                    item.setId(rs.getString("item_id"));
                    item.setQuantidade(rs.getInt("quantidade"));
                    item.setProduto(p); // <--- O item carrega o produto completo dentro dele

                    lista.add(item);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar itens do carrinho", e);
        }

        return lista;

    }


    public Carrinho buscarPorId(String id) {
        String sqlCarrinho = "SELECT * FROM carrinhos WHERE id = ?";

        String sqlItens = """
        SELECT i.id AS item_id, i.quantidade,
               p.id AS produto_id, p.nome, p.preco, p.descricao
        FROM itens_carrinho i
        INNER JOIN produtos p ON i.produto_id = p.id
        WHERE i.carrinho_id = ?
    """;

        Connection conn = Database.getConnection();

        try {
            Carrinho carrinho = null;

            // --- ETAPA 1: Buscar o Objeto Carrinho ---
            try (PreparedStatement stmt = conn.prepareStatement(sqlCarrinho)) {
                stmt.setString(1, id);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    carrinho = new Carrinho();
                    carrinho.setId(rs.getString("id"));
                    // Se tiver data: carrinho.setDataCriacao(rs.getTimestamp("data_criacao"));
                } else {
                    return null;
                }
            }

            // --- ETAPA 2: Buscar os Itens e popular a lista ---
            try (PreparedStatement stmt = conn.prepareStatement(sqlItens)) {
                stmt.setString(1, id); // Usa o mesmo ID do carrinho
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Produto p = new Produto();
                    p.setId(rs.getString("produto_id"));
                    p.setNome(rs.getString("nome"));
                    p.setPreco(rs.getBigDecimal("preco"));
                    if (rs.wasNull()) {
                        p.setPreco(BigDecimal.ZERO);
                    }
                    p.setDescricao(rs.getString("descricao"));

                    ItemCarrinho item = new ItemCarrinho();
                    item.setId(rs.getString("item_id"));
                    item.setQuantidade(rs.getInt("quantidade"));
                    item.setProduto(p);

                    carrinho.getItens().add(item);
                }
            }
            return carrinho;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar carrinho por ID", e);
        }
    }
}
