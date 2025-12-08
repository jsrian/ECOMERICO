package br.ufpb.dcx.rodrigor.projetos;

import br.ufpb.dcx.rodrigor.projetos.login.UsuarioService;
import br.ufpb.dcx.rodrigor.projetos.participante.services.ParticipanteService;
import br.ufpb.dcx.rodrigor.projetos.produtos.services.ProdutoService;
import br.ufpb.dcx.rodrigor.projetos.projeto.services.ProjetoService;
import io.javalin.config.Key;
import java.sql.Connection;

public enum Keys {
    PROJETO_SERVICE(new Key<ProjetoService>("projeto-service")),
    PARTICIPANTE_SERVICE(new Key<ParticipanteService>("participante-service")),
    FORM_SERVICE(new Key<ParticipanteService>("form-service")),
    USUARIO_SERVICE(new Key<UsuarioService>("usuario-service")),
    PRODUTO_SERVICE(new Key<ProdutoService>("produto-service")),
    DB_CONNECTION(new Key<Connection>("db-connection"));

    ;

    private final Key<?> k;

    <T> Keys(Key<T> key) {
        this.k = key;
    }

    public <T> Key<T> key() {
        @SuppressWarnings("unchecked")
        Key<T> typedKey = (Key<T>) this.k;
        return typedKey;
    }
}