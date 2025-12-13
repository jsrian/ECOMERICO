package br.ufpb.dcx.rodrigor.projetos.login;

import br.ufpb.dcx.rodrigor.projetos.Keys;
import io.javalin.http.Context;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Map;


public class VerificaLogin {

    public static final String USUARIO_LOGADO = "usuario_logaddo";
    public static final Usuario usuario = new Usuario();

    public static void postLogin(Context ctx){
        String login = ctx.formParam("login");
        String senha = ctx.formParam("senha");


        System.out.println("--- DADOS RECEBIDOS ---");
        System.out.println("Login: " + login);
        System.out.println("Senha: " + senha); // Cuidado: Nunca logue senhas em produção!
        System.out.println("-----------------------");

        UsuarioService usuarioService = ctx.appData(Keys.USUARIO_SERVICE.key());
        Usuario usuario = usuarioService.buscarUsuarioPorLogin(login);
        if (usuario != null && BCrypt.checkpw(senha, usuario.getSenha())) {
            ctx.sessionAttribute(USUARIO_LOGADO, "true");
            ctx.redirect("/produtos");
            System.out.println("Chegou aqui");
        } else {
            ctx.render("/login/login.html", Map.of("erro", "Credenciais inválidas."));
        }
    }
}
