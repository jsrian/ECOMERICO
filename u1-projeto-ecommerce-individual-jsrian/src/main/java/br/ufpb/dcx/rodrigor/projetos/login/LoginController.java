package br.ufpb.dcx.rodrigor.projetos.login;

import br.ufpb.dcx.rodrigor.projetos.Keys;
import io.javalin.http.Context;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Map;

public class LoginController {
    public static final String USUARIO_LOGADO = "usuario_logado";


    public void mostrarPaginaLogin(Context ctx) {
        String teste = ctx.queryParam("teste");
        if(teste != null){
            throw new RuntimeException("Erro de teste a partir do /login?teste=1");
        }
        ctx.render("/login/login.html");
    }

    public static void processarLogin(Context ctx) {
        String login = ctx.formParam("login");
        String senha = ctx.formParam("senha");

        UsuarioService usuarioService = ctx.appData(Keys.USUARIO_SERVICE.key());
        Usuario usuario = usuarioService.buscarUsuarioPorLogin(login);
        if (usuario != null && BCrypt.checkpw(senha, usuario.getSenha())) {
            ctx.sessionAttribute(USUARIO_LOGADO, "true");
            ctx.sessionAttribute("usuario", usuario);
            ctx.redirect("/produtos");
        } else {
            ctx.render("/login/login.html", Map.of("erro", "Credenciais inválidas."));
        }
    }

    public void logout(Context ctx) {
        ctx.sessionAttribute("usuario", null);
        ctx.redirect("/login");
    }
}