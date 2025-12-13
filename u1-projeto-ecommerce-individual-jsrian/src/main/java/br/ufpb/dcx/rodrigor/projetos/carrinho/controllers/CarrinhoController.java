package br.ufpb.dcx.rodrigor.projetos.carrinho.controllers;

import br.ufpb.dcx.rodrigor.projetos.Keys;
import br.ufpb.dcx.rodrigor.projetos.carrinho.model.Carrinho;
import br.ufpb.dcx.rodrigor.projetos.carrinho.model.ItemCarrinho;
import br.ufpb.dcx.rodrigor.projetos.carrinho.services.CarrinhoServices;
import br.ufpb.dcx.rodrigor.projetos.produtos.model.Produto;
import br.ufpb.dcx.rodrigor.projetos.produtos.services.ProdutoService;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CarrinhoController {

    private final CarrinhoServices carrinhoServices;
    private final ProdutoService produtoService;

    public CarrinhoController(CarrinhoServices carrinhoServices, ProdutoService produtoService) {
        this.carrinhoServices = carrinhoServices;
        this.produtoService = produtoService;
    }

    public Optional<Produto> produtoServiceFindById(Long id) {
        // Exemplo:
        if (id > 0) {
            // Supondo que você tem um construtor ou setter para o ID
            Produto p = new Produto();

            p.setId(String.valueOf(id));
            // ... configure outros campos como nome, preco, etc.
            return Optional.of(p);
        }
        return Optional.empty();
    }

    public void mostrarTelaCarrinho(Context ctx){

        Carrinho carrinho = carrinhoServices.getCarrinhoFromSession(ctx);

        ctx.render("/carrinho/compra_carrinho.html", Map.of(
                "itensCarrinho", carrinho.getItens(),
                "totalGeral", carrinho.getTotal()
        ));
    }

    public void adicionarItemAoCarrinho(Context ctx) {
        // 1. Obter o ID do produto
        String produtoId;

        try {
            produtoId = ctx.pathParam("produtoId");
        } catch (NumberFormatException e) {
            ctx.status(400).result("ID do produto inválido.");
            return;
        }

        // 2. Buscar o Produto
        Optional<Produto> produtoOpt = produtoService.produtoServiceFindById(produtoId);
        Produto produto = produtoOpt.get();
        if (produtoOpt.isEmpty()) {
            ctx.status(404).result("Produto não encontrado.");
            return;
        }
        carrinhoServices.adicionarOuAtualizarItem(ctx, produto);

        // 4. Redirecionar para o carrinho
        ctx.redirect("/carrinho");
    }

    // --- ROTA DE COMPRAR (Exemplo) ---
}