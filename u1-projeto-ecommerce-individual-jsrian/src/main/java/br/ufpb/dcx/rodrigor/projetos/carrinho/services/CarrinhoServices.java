package br.ufpb.dcx.rodrigor.projetos.carrinho.services;

import br.ufpb.dcx.rodrigor.projetos.Keys;
import br.ufpb.dcx.rodrigor.projetos.carrinho.model.Carrinho;
import br.ufpb.dcx.rodrigor.projetos.carrinho.model.ItemCarrinho;
import br.ufpb.dcx.rodrigor.projetos.carrinho.repository.CarrinhoRepository;
import br.ufpb.dcx.rodrigor.projetos.produtos.model.Produto;
import br.ufpb.dcx.rodrigor.projetos.produtos.repository.ProdutoRepository;
import io.javalin.http.Context;

import java.util.List;

public class CarrinhoServices {
    private final String CARRINHO_KEY = String.valueOf(Keys.CARRINHO_SESSION_KEY.key());

    private final CarrinhoRepository carrinhoRepository;
    private final ProdutoRepository produtoRepository;

    public CarrinhoServices() {
        this.carrinhoRepository = new CarrinhoRepository();
        this.produtoRepository = new ProdutoRepository();
    }

    public Carrinho getCarrinhoFromSession(Context ctx) {
        // Agora recuperamos o objeto Carrinho, não um Map
        Carrinho carrinho = ctx.sessionAttribute(CARRINHO_KEY);

        if (carrinho == null) {
            carrinho = new Carrinho();
        }
        return carrinho;
    }

    public void adicionarOuAtualizarItem(Context ctx, Produto produto) {

        Carrinho carrinho = getCarrinhoFromSession(ctx);

        ItemCarrinho novoItem = new ItemCarrinho(1, produto);

        carrinho.adicionarItem(novoItem);

        ctx.sessionAttribute(CARRINHO_KEY, carrinho);
    }

    public void persistirCarrinho(Context ctx) {
        Carrinho carrinho = getCarrinhoFromSession(ctx);
        if (carrinho.getItens().size() > 0) {
            carrinhoRepository.salvar(carrinho);
        }
    }
}

