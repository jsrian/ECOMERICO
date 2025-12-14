package br.ufpb.dcx.rodrigor.projetos.carrinho.services;

import br.ufpb.dcx.rodrigor.projetos.Keys;
import br.ufpb.dcx.rodrigor.projetos.carrinho.model.Carrinho;
import br.ufpb.dcx.rodrigor.projetos.carrinho.model.ItemCarrinho;
import br.ufpb.dcx.rodrigor.projetos.carrinho.repository.CarrinhoRepository;
import br.ufpb.dcx.rodrigor.projetos.produtos.model.Produto;
import br.ufpb.dcx.rodrigor.projetos.produtos.repository.ProdutoRepository;
import io.javalin.http.Context;


public class CarrinhoServices {
    private final String CARRINHO_KEY = String.valueOf(Keys.CARRINHO_SESSION_KEY.key());

    private final CarrinhoRepository carrinhoRepository;
    private final ProdutoRepository produtoRepository;

    public CarrinhoServices() {
        this.carrinhoRepository = new CarrinhoRepository();
        this.produtoRepository = new ProdutoRepository();
    }

    public Carrinho getCarrinhoFromSession(Context ctx) {
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

    public void finalizarEsvaziarCarrinho(Context ctx) {
        Carrinho carrinho = getCarrinhoFromSession(ctx);

        if (carrinho.getItens().isEmpty()) {
            return;
        }
        carrinhoRepository.salvar(carrinho);
        ctx.sessionAttribute(CARRINHO_KEY, new Carrinho());
    }
    public void processarFinalizacao(Context ctx) {

        CarrinhoServices carrinhoService = (CarrinhoServices) ctx.appData(Keys.CARRINHO_SERVICE.key());
        if (carrinhoService.getCarrinhoFromSession(ctx).getItens().isEmpty()) {
            ctx.redirect("/carrinho?erro=vazio");
            return;
        }
        try {
            carrinhoService.finalizarEsvaziarCarrinho(ctx);
            ctx.redirect("/pedido_feito");

        } catch (RuntimeException e) {
            System.err.println("Erro ao finalizar compra e salvar no DB: " + e.getMessage());
            ctx.status(500).result("Erro interno ao processar seu pedido. Tente novamente.");
        }
    }

    public String getCarrinhoKey() {
        return CARRINHO_KEY;
    }

}