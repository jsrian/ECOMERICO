package br.ufpb.dcx.rodrigor.projetos.carrinho.repository;

import br.ufpb.dcx.rodrigor.projetos.carrinho.model.Carrinho;
import br.ufpb.dcx.rodrigor.projetos.carrinho.model.ItemCarrinho;
import br.ufpb.dcx.rodrigor.projetos.db.Database;
import java.sql.*;

public class CarrinhoRepository {

    public void salvar(Carrinho carrinho) {
        String sqlCarrinho = "INSERT INTO carrinhos DEFAULT VALUES";
        String sqlItem = "INSERT INTO itens_carrinho (carrinho_id, produto_id, quantidade) VALUES (?, ?, ?)";

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sqlCarrinho, Statement.RETURN_GENERATED_KEYS)) {
                stmt.executeUpdate();

                var rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    carrinho.setId(rs.getString(1));
                }
            }
            try (PreparedStatement stmt = conn.prepareStatement(sqlItem)) {
                for (ItemCarrinho item : carrinho.getItens()) {
                    stmt.setString(1, carrinho.getId());
                    stmt.setString(2, item.getProduto().getId());
                    stmt.setInt(3, item.getQuantidade());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
            conn.commit();

        } catch (SQLException e) {
            System.err.println("Erro durante transação. Tentando rollback.");
            throw new RuntimeException("Erro ao salvar carrinho (Transação falhou)", e);
        }
    }
    public void removerItensDoCarrinhoPorProdutoId(String produtoId) {
        String sql = "DELETE FROM ITENS_CARRINHO WHERE PRODUTO_ID = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, produtoId);

        } catch (SQLException e) {
            System.err.println("Erro ao remover itens do carrinho para o produto ID: " + produtoId);
            throw new RuntimeException("Erro ao limpar referências do carrinho no DB", e);
        }
    }
}
