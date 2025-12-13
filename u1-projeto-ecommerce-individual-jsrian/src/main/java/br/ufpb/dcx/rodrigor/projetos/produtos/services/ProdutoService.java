package br.ufpb.dcx.rodrigor.projetos.produtos.services;

import java.util.List;
import java.util.Optional;

import br.ufpb.dcx.rodrigor.projetos.produtos.model.Produto;
import br.ufpb.dcx.rodrigor.projetos.produtos.repository.ProdutoRepository;

public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(){
        this.produtoRepository = new ProdutoRepository();
    }

    public List<Produto> listarProdutos(){
        return produtoRepository.listarProdutos();
    }

    public void cadastrarProduto(Produto produto){
        if (produto == null){
            throw new IllegalArgumentException("Produto invalido");
        }
        if (produto.getPreco() == null || produto.getPreco().doubleValue() <= 0) {
            throw new IllegalArgumentException("Preço inválido");
        }
        produtoRepository.salvarProduto(produto);
    }
    public Optional<Produto> produtoServiceFindById(String id) {
        Produto produtoDoBD = produtoRepository.buscarPorId(id);
        return Optional.ofNullable(produtoDoBD);
    }

    public void removerProduto(String id){
        if (id == null){
            throw new IllegalArgumentException("ID do produto invalido");
        }
        produtoRepository.removerProduto(id);
    }
}