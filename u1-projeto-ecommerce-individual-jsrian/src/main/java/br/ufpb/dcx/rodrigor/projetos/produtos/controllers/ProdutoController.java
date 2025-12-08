package br.ufpb.dcx.rodrigor.projetos.produtos.controllers;

import java.math.BigDecimal;

import br.ufpb.dcx.rodrigor.projetos.Keys;
import br.ufpb.dcx.rodrigor.projetos.produtos.model.Produto;
import br.ufpb.dcx.rodrigor.projetos.produtos.services.ProdutoService;
import io.javalin.http.Context;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class ProdutoController {

    private final ProdutoService produtoService;
    private static final Logger logger = LogManager.getLogger(ProdutoController.class);

    public ProdutoController() {
        this.produtoService =null;
    }
    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    public void mostrarFormulario(Context ctx) {
        ProdutoService produtoService = ctx.appData(Keys.PRODUTO_SERVICE.key());
        ctx.attribute("produtos", produtoService.listarProdutos());
        ctx.render("/produtos/form_produtos.html");
    }
    public void mostrarFormularioCadastro(Context ctx) {
        ctx.render("/produtos/form_produtos.html");
    }

    public void adicionarProduto(Context ctx){
        ProdutoService produtoService = ctx.appData(Keys.PRODUTO_SERVICE.key());
        String nome = ctx.formParam("nome");
        String descricao = ctx.formParam("descricao");
        String precoStr = ctx.formParam("preco");

        try {
            if (nome == null || precoStr == null) {
                ctx.status(400).result("Nome e preço são obrigatórios.");
                return;
            }
            BigDecimal preco = new BigDecimal(precoStr);
            produtoService.cadastrarProduto(new Produto(null,nome,preco,descricao));
            ctx.attribute("info","Produto cadastrado");
            System.out.println("Passou por aqui");
            ctx.redirect("/produtos");
        } catch (Exception e) {
            logger.error("Erro ao adicionar produto", e);
            ctx.status(500).result("Erro ao adicionar produto: " + e.getMessage());
        }
    }


    public void listarProdutos(Context ctx) {
        ProdutoService produtoService = ctx.appData(Keys.PRODUTO_SERVICE.key());
        try {
            List<Produto> produtos = produtoService.listarProdutos();
            ctx.attribute("produtos",produtos);
            ctx.render("/produtos/lista_produtos.html");
        } catch (Exception e) {
            logger.error("Erro ao recuperar produtos", e);
            ctx.status(500).result("Erro ao recuperar produtos: " + e.getMessage());
        }
    }
    public void removerProduto(@NotNull Context context){
        ProdutoService produtoService = context.appData(Keys.PRODUTO_SERVICE.key());
        String id = context.pathParam("id");
        produtoService.removerProduto(id);
        context.redirect("/produtos");

    }
}