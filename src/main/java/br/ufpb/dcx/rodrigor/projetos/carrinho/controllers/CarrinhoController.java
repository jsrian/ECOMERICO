package br.ufpb.dcx.rodrigor.projetos.carrinho.controllers;

import br.ufpb.dcx.rodrigor.projetos.Keys;
import br.ufpb.dcx.rodrigor.projetos.carrinho.model.Carrinho;
import br.ufpb.dcx.rodrigor.projetos.carrinho.services.CarrinhoServices;
import br.ufpb.dcx.rodrigor.projetos.produtos.model.Produto;
import br.ufpb.dcx.rodrigor.projetos.produtos.services.ProdutoService;
import io.javalin.http.Context;

import java.util.Map;
import java.util.Optional;


public class CarrinhoController {

    private final CarrinhoServices carrinhoServices;
    private final ProdutoService produtoService;

    public CarrinhoController(CarrinhoServices carrinhoServices, ProdutoService produtoService) {
        this.carrinhoServices = carrinhoServices;
        this.produtoService = produtoService;
    }

    public void mostrarTelaCarrinho(Context ctx){
        Carrinho carrinho = carrinhoServices.getCarrinhoFromSession(ctx);
        ctx.render("/carrinho/compra_carrinho.html", Map.of(
                "itensCarrinho", carrinho.getItens(),
                "totalGeral", carrinho.getTotal()
        ));
    }
    public void mostrarPedidoFeito(Context ctx){
        ctx.sessionAttribute(String.valueOf(Keys.CARRINHO_SESSION_KEY), null);
        ctx.render("/carrinho/pedido_feito.html");
    }

    public void adicionarItemAoCarrinho(Context ctx) {
        String produtoId;
        try {
            produtoId = ctx.pathParam("produtoId");
        } catch (NumberFormatException e) {
            ctx.status(400).result("ID do produto inválido.");
            return;
        }
        Optional<Produto> produtoOpt = produtoService.produtoServiceFindById(produtoId);
        Produto produto = produtoOpt.get();
        if (produtoOpt.isEmpty()) {
            ctx.status(404).result("Produto não encontrado.");
            return;
        }
        carrinhoServices.adicionarOuAtualizarItem(ctx, produto);
        ctx.sessionAttribute("mensagemSucesso", "O produto '" + produto.getNome() + "' foi adicionado ao carrinho!");
        ctx.redirect("/inicio");
    }

    public void removerItemCarrinho(Context context){

        Carrinho carrinho = carrinhoServices.getCarrinhoFromSession(context); // MUDANÇA AQUI!
        String produtoIdParaRemover = context.pathParam("produtoId");

        if (carrinho != null) {
            carrinho.removerItemPeloId(produtoIdParaRemover);
            context.sessionAttribute(carrinhoServices.getCarrinhoKey(), carrinho);
        }
        context.redirect("/carrinho");
    }
}