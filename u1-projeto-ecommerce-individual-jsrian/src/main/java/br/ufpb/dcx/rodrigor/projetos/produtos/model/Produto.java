package br.ufpb.dcx.rodrigor.projetos.produtos.model;

import java.math.BigDecimal;

public class Produto {
    private String nome;
    private BigDecimal preco;
    private String descricao;
    private String id;
    private String caminhoImagem;

    public Produto(String id, String nome, BigDecimal preco, String descricao, String caminhoImagem) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.descricao = descricao;
        this.caminhoImagem = caminhoImagem;
    }
    
    public Produto() {
    }

    public String getCaminhoImagem() {
        return caminhoImagem;
    }

    public void setCaminhoImagem(String caminhoImagem) {
        this.caminhoImagem = caminhoImagem;
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
