package br.ufpb.dcx.rodrigor.projetos.config;

import br.ufpb.dcx.rodrigor.projetos.carrinho.controllers.CarrinhoController;
import br.ufpb.dcx.rodrigor.projetos.carrinho.services.CarrinhoServices;
import br.ufpb.dcx.rodrigor.projetos.form.controller.FormController;
import br.ufpb.dcx.rodrigor.projetos.login.LoginController;
import br.ufpb.dcx.rodrigor.projetos.login.UsuarioController;
import br.ufpb.dcx.rodrigor.projetos.produtos.controllers.ProdutoController;
import br.ufpb.dcx.rodrigor.projetos.produtos.services.ProdutoService;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;

public class RouterConfig {

    public RouterConfig(JavalinConfig config) {
    }

    public void configurar(Javalin app) {

        // Chamada Services
        CarrinhoServices carrinhoServices = new CarrinhoServices();
        ProdutoService produtoService = new ProdutoService();

        // Chamada Controllers
        CarrinhoController carrinhoController = new CarrinhoController(carrinhoServices, produtoService);
        ProdutoController produtoController = new ProdutoController(produtoService);
        LoginController loginController = new LoginController();
        UsuarioController usuarioController = new UsuarioController();
        FormController formController = new FormController();


        // Rotas de CARRINHO
        app.get("/add_carrinho/{produtoId}", carrinhoController::adicionarItemAoCarrinho);
        app.get("/carrinho", carrinhoController :: mostrarTelaCarrinho);
        app.get("/carrinho/remover/{produtoId}", carrinhoController :: removerItemCarrinho);
        app.get("/pedido_feito", carrinhoController :: mostrarPedidoFeito);
        app.post("/iniciar_checkout", ctx -> {
            carrinhoServices.processarFinalizacao(ctx);
            ctx.redirect("/pedido_feito");
        });

        // Rotas de LOGIN
        app.before("/produtos", this::autenticador);
        app.get("/login", loginController::mostrarPaginaLogin);
        app.post("/login", LoginController::processarLogin);
        app.get("/logout", ctx -> {
            ctx.req().getSession().invalidate();
            ctx.redirect("/login");
        });


        // Rotas de PRODUTOS
        app.get("/inicio", produtoController :: mostrarTelaProdutos);
        app.get("/produtos/novo", produtoController::mostrarFormularioCadastro);
        app.get("/produtos", produtoController::listarProdutos);
        app.get("/", ctx -> ctx.redirect("/inicio"));
        app.post("/produtos/cadastrar", produtoController::adicionarProduto);
        app.get("/produtos/{id}/remover", produtoController ::removerProduto);


        // Rotas de USUÁRIO
        app.get("/usuarios", usuarioController::listarUsuarios);
        app.get("/usuarios/novo", usuarioController::mostrarFormularioCadastro);
        app.post("/usuarios/cadastrar", usuarioController::cadastrarUsuario);
        app.get("/usuarios/signup", usuarioController::mostrarFormulario_signup);
        app.get("/usuarios/{id}/remover", usuarioController::removerUsuario);


        // Rotas de FORMULÁRIOS
        app.get("/form/{formId}", formController::abrirFormulario);
        app.post("/form/{formId}", formController::validarFormulario);
    }

    private void autenticador(Context ctx) {
        if (!"true".equals(ctx.sessionAttribute(LoginController.USUARIO_LOGADO))) {
            ctx.redirect("/login");
        }
    }
}