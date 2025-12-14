package br.ufpb.dcx.rodrigor.projetos.carrinho.model;

import br.ufpb.dcx.rodrigor.projetos.produtos.model.Produto;

import java.math.BigDecimal;
import java.util.Objects;

public class ItemCarrinho {
    private String id;
    private int quantidade;
    private Produto produto;

    public ItemCarrinho(int quantidade, Produto produto){
        this.quantidade =quantidade;
        this.produto = produto;
    }

    public ItemCarrinho() {

    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public BigDecimal getSubtotal() {
        BigDecimal preco = produto.getPreco();
        if (preco == null) {
            return BigDecimal.ZERO;
        }
        return preco.multiply(new BigDecimal(getQuantidade()));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ItemCarrinho itemCarrinho = (ItemCarrinho) o;
        return quantidade == itemCarrinho.quantidade && Objects.equals(produto, itemCarrinho.produto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantidade, produto);
    }
}
