package br.ufpb.dcx.rodrigor.projetos.produtos.model;

import java.math.BigDecimal;

public class Produto {
    private String nome;
    private BigDecimal preco;
    private String descricao;
    private String id;

    public Produto(String id, String nome, BigDecimal preco, String descricao) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.descricao = descricao;
    }
    
    public Produto() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }
    public BigDecimal getPreco(){
        return preco;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
