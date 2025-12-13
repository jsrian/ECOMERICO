package br.ufpb.dcx.rodrigor.projetos;

import br.ufpb.dcx.rodrigor.projetos.carrinho.controllers.CarrinhoController;
import br.ufpb.dcx.rodrigor.projetos.carrinho.services.CarrinhoServices;
import br.ufpb.dcx.rodrigor.projetos.db.H2Console;
import br.ufpb.dcx.rodrigor.projetos.form.controller.FormController;
import br.ufpb.dcx.rodrigor.projetos.login.LoginController;
import br.ufpb.dcx.rodrigor.projetos.login.UsuarioController;
import br.ufpb.dcx.rodrigor.projetos.login.UsuarioService;

import br.ufpb.dcx.rodrigor.projetos.login.VerificaLogin;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Consumer;

import br.ufpb.dcx.rodrigor.projetos.produtos.controllers.ProdutoController;
import br.ufpb.dcx.rodrigor.projetos.produtos.services.ProdutoService;

public class App {
    private static final Logger logger = LogManager.getLogger(App.class);

    private static final int PORTA_PADRAO = 8000;

    private static final String PROP_PORTA_SERVIDOR = "porta.servidor";


    private final Properties propriedades;

    public App() {
        this.propriedades = carregarPropriedades();
    }



    public void iniciar() {
        Javalin app = inicializarJavalin();
        configurarPaginasDeErro(app);
        configurarRotas(app);

        app.exception(Exception.class, (e, ctx) -> {
            logger.error("Erro não tratado", e);
            ctx.status(500);
        });
    }

    private void configurarPaginasDeErro(Javalin app) {
        app.error(404, ctx -> ctx.render("erro_404.html"));
        app.error(500, ctx -> ctx.render("erro_500.html"));
    }

    private Javalin inicializarJavalin() {
        int porta = obterPortaServidor();

        logger.info("Iniciando aplicação na porta {}", porta);

        Consumer<JavalinConfig> configConsumer = this::configureJavalin;

        return Javalin.create(configConsumer).start(porta);
    }

    private void configureJavalin(JavalinConfig config) {
        TemplateEngine templateEngine = configurarThymeleaf();

        config.events(event -> {
            event.serverStarting(() -> {
                logger.info("Servidor Javalin está iniciando...");
                registrarServicos(config);
            });
            event.serverStopping(() -> {
            });
        });
        config.staticFiles.add(staticFileConfig -> {
            staticFileConfig.directory = "/public";
            staticFileConfig.location = Location.CLASSPATH;
        });
        config.fileRenderer(new JavalinThymeleaf(templateEngine));

    }

private void registrarServicos(JavalinConfig config) {
    br.ufpb.dcx.rodrigor.projetos.db.Database.init();
    config.appData(Keys.DB_CONNECTION.key(), br.ufpb.dcx.rodrigor.projetos.db.Database.getConnection());
    config.appData(Keys.USUARIO_SERVICE.key(), new UsuarioService());
    config.appData(Keys.PRODUTO_SERVICE.key(), new ProdutoService());
    config.appData(Keys.CARRINHO_SERVICE.key(), new CarrinhoServices());
}


    private void configurarRotas(Javalin app) {
        ProdutoService produtoService = new ProdutoService(); // Crie sua instância
        CarrinhoServices carrinhoServices = new CarrinhoServices();
        CarrinhoController carrinhoController = new CarrinhoController(carrinhoServices, produtoService);
        app.get("/add_carrinho/{produtoId}", carrinhoController::adicionarItemAoCarrinho);
        app.get("/carrinho", carrinhoController :: mostrarTelaCarrinho);
        app.post("/iniciar_checkout", ctx -> {
            carrinhoServices.persistirCarrinho(ctx);
            ctx.redirect("/pagamento");
        });

        LoginController loginController = new LoginController();
        app.before("/produtos",ctx -> {
            if (!"true".equals(ctx.sessionAttribute(VerificaLogin.USUARIO_LOGADO))) {
                // Redireciona
                ctx.redirect("/login");
            }
        });
        app.get("/login", loginController::mostrarPaginaLogin);
        app.post("/login", VerificaLogin::postLogin);
        app.get("/logout", ctx -> {
            ctx.req().getSession().invalidate();
            ctx.redirect("/login");
        });

        ProdutoController produtoController = new ProdutoController();
        app.get("/inicio", produtoController :: mostrarTelaProdutos);
        app.get("/produtos/novo", produtoController::mostrarFormularioCadastro);
        app.get("/produtos", produtoController::listarProdutos);
        app.get("/", ctx -> ctx.redirect("/inicio"));
        app.post("/produtos/cadastrar", produtoController::adicionarProduto);
        app.get("/produtos/{id}/remover", produtoController ::removerProduto);

        // Rotas para o controlador de usuário
        UsuarioController usuarioController = new UsuarioController();
        app.get("/usuarios", usuarioController::listarUsuarios);
        app.get("/usuarios/novo", usuarioController::mostrarFormularioCadastro);
        app.post("/usuarios/cadastrar", usuarioController::cadastrarUsuario);
        app.get("/usuarios/signup", usuarioController::mostrarFormulario_signup);
        app.get("/usuarios/{id}/remover", usuarioController::removerUsuario);

        // Rotas para o controlador de formulários
        FormController formController = new FormController();
        app.get("/form/{formId}", formController::abrirFormulario);
        app.post("/form/{formId}", formController::validarFormulario);

    }

    private int obterPortaServidor() {
        if (propriedades.containsKey(PROP_PORTA_SERVIDOR)) {
            try {
                return Integer.parseInt(propriedades.getProperty(PROP_PORTA_SERVIDOR));
            } catch (NumberFormatException e) {
                logger.error("Porta definida no arquivo de propriedades não é um número válido: '{}'", propriedades.getProperty(PROP_PORTA_SERVIDOR));
                System.exit(1);
            }
        } else {
            logger.info("Porta não definida no arquivo de propriedades, utilizando porta padrão {}", PORTA_PADRAO);
        }
        return PORTA_PADRAO;
    }

    private TemplateEngine configurarThymeleaf() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }

    private Properties carregarPropriedades() {
        Properties prop = new Properties();
        try (InputStream input = App.class.getClassLoader().getResourceAsStream("application.properties")) {
            if(input == null){
                logger.error("Arquivo de propriedades /src/main/resources/application.properties não encontrado");
                System.exit(1);
            }
            prop.load(input);
        } catch (IOException ex) {
            logger.error("Erro ao carregar o arquivo de propriedades /src/main/resources/application.properties", ex);
            System.exit(1);
        }
        return prop;
    }

    public static void main(String[] args) {
        try {
            new App().iniciar();
            new H2Console().start();
        } catch (Exception e) {
            logger.error("Erro ao iniciar a aplicação", e);
            System.exit(1);
        }
    }
}