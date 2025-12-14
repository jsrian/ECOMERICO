package br.ufpb.dcx.rodrigor.projetos.produtos.controllers;

import java.math.BigDecimal;

import br.ufpb.dcx.rodrigor.projetos.Keys;
import br.ufpb.dcx.rodrigor.projetos.carrinho.model.Carrinho;
import br.ufpb.dcx.rodrigor.projetos.carrinho.model.ItemCarrinho;
import br.ufpb.dcx.rodrigor.projetos.carrinho.services.CarrinhoServices;
import br.ufpb.dcx.rodrigor.projetos.produtos.model.Produto;
import br.ufpb.dcx.rodrigor.projetos.produtos.services.ProdutoService;
import io.javalin.http.Context;

import java.util.List;

import io.javalin.http.UploadedFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class ProdutoController {

    private final ProdutoService produtoService;
    private static final Logger logger = LogManager.getLogger(ProdutoController.class);

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }
    public void handleCadastrarProduto(Context ctx) {
        String nome = ctx.formParam("nome");
        String precoStr = ctx.formParam("preco");
        String descricao = ctx.formParam("descricao");

        BigDecimal preco = null;

        if (precoStr == null || precoStr.trim().isEmpty()) {
            ctx.status(400).result("Preço é obrigatório.");
            return;
        }

        try {
            String precoLimpo = precoStr.trim().replace(",", ".");

            preco = new BigDecimal(precoLimpo);

            if (preco.compareTo(BigDecimal.ZERO) < 0) {
                ctx.status(400).result("O preço não pode ser negativo.");
                return;
            }

        } catch (NumberFormatException e) {
            ctx.status(400).result("Formato de preço inválido. Use ponto ou vírgula como separador decimal.");
            return;
        }
        UploadedFile uploadedFile = ctx.uploadedFile("caminhoImagem");
        Produto novoProduto = new Produto(null, nome, preco, descricao, null);
        try {
            this.produtoService.cadastrarProduto(novoProduto, uploadedFile);
            ctx.redirect("/produtos");
        } catch (RuntimeException e) {
            logger.error("Erro ao cadastrar produto", e);
            ctx.status(500).result("Erro ao cadastrar produto: " + e.getMessage());
        }
    }

        public void mostrarTelaProdutos (Context ctx){

            String mensagemSucesso = ctx.sessionAttribute("mensagemSucesso");
            if (mensagemSucesso != null) {
                ctx.attribute("anuncio", mensagemSucesso);
                ctx.sessionAttribute("mensagemSucesso", null);
            }
            CarrinhoServices carrinhoServices = ctx.appData(Keys.CARRINHO_SERVICE.key());
            if (carrinhoServices == null) {
                System.err.println("ERRO CRÍTICO: CarrinhoServices não encontrado no appData com a chave: " + Keys.CARRINHO_SERVICE.key());
            } else {
                Carrinho carrinho = carrinhoServices.getCarrinhoFromSession(ctx);

                int totalItens = 0;
                if (carrinho != null && carrinho.getItens() != null) {
                    totalItens = carrinho.getItens().stream()
                            .mapToInt(ItemCarrinho::getQuantidade)
                            .sum();
                }
                ctx.attribute("qtdItensCarrinho", totalItens);
            }
            ProdutoService produtoService = ctx.appData(Keys.PRODUTO_SERVICE.key());
            List<Produto> produtos = produtoService.listarProdutos();
            ctx.attribute("produtos", produtos);
            ctx.render("/carrinho/tela_produto.html");
        }

        public void mostrarFormularioCadastro (Context ctx){
            ctx.render("/produtos/form_produtos.html");
        }

        public void adicionarProduto (Context ctx){
            handleCadastrarProduto(ctx);
        }


        public void listarProdutos (Context ctx){
            ProdutoService produtoService = ctx.appData(Keys.PRODUTO_SERVICE.key());
            try {
                List<Produto> produtos = produtoService.listarProdutos();
                ctx.attribute("produtos", produtos);
                ctx.render("/produtos/lista_produtos.html");
            } catch (Exception e) {
                logger.error("Erro ao recuperar produtos", e);
                ctx.status(500).result("Erro ao recuperar produtos: " + e.getMessage());
            }
        }
        public void removerProduto (@NotNull Context context){
            ProdutoService produtoService = context.appData(Keys.PRODUTO_SERVICE.key());
            String id = context.pathParam("id");
            produtoService.removerProduto(id);
            context.redirect("/produtos");

        }
    }