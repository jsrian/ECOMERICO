package br.ufpb.dcx.rodrigor.projetos.produtos.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.ufpb.dcx.rodrigor.projetos.carrinho.repository.CarrinhoRepository;
import br.ufpb.dcx.rodrigor.projetos.produtos.model.Produto;
import br.ufpb.dcx.rodrigor.projetos.produtos.repository.ProdutoRepository;
import io.javalin.http.UploadedFile;

public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private static final String UPLOADS_DIR = "uploads-data";
    private final CarrinhoRepository carrinhoRepository = new CarrinhoRepository();

    public ProdutoService() {
        this.produtoRepository = new ProdutoRepository();
    }

    public List<Produto> listarProdutos() {
        return produtoRepository.listarProdutos();
    }

    public void cadastrarProduto(Produto produto, UploadedFile uploadedFile) {
        String caminhoImagemSalvo = null;
        if (uploadedFile != null) {
            if (uploadedFile.size() > 0) {
                try {
                    caminhoImagemSalvo = salvarImagemNoDisco(uploadedFile);
                } catch (RuntimeException e) {
                    System.err.println("Falha ao salvar a imagem no disco: " + e.getMessage());
                    throw new RuntimeException("Falha ao salvar a imagem no disco.", e);
                }
            } else {
                System.out.println("DEBUG: Arquivo de imagem enviado, mas tem 0 bytes.");
            }
        } else {
            System.out.println("DEBUG: Nenhuma imagem foi enviada pelo formulário.");
        }
        if (produto == null) {
            throw new IllegalArgumentException("Produto invalido");
        }
        if (produto.getPreco() == null || produto.getPreco().doubleValue() <= 0) {
            throw new IllegalArgumentException("Preço inválido");
        }
        produto.setCaminhoImagem(caminhoImagemSalvo);
        produtoRepository.salvarProduto(produto);
    }

    public Optional<Produto> produtoServiceFindById(String id) {
        Produto produtoDoBD = produtoRepository.buscarPorId(id);
        return Optional.ofNullable(produtoDoBD);
    }

    public void removerProduto(String id) {

        Produto produto = produtoRepository.buscarPorId(id);

        if (produto != null) {
            removerImagemDoDisco(produto.getCaminhoImagem());
        }

        carrinhoRepository.removerItensDoCarrinhoPorProdutoId(id); // <--- Chamada Nova

        produtoRepository.removerProduto(id);
    }

    private String salvarImagemNoDisco(UploadedFile uploadedFile) {
        String uploadDir = "./uploads-data/";
        File directory = new File(uploadDir);

        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new RuntimeException("Não foi possível criar o diretório de uploads: " + uploadDir);
            }
        }
        String extensao = uploadedFile.extension();
        String nomeUnico = UUID.randomUUID().toString() + (extensao != null ? extensao : "");
        Path filePath = Paths.get(uploadDir, nomeUnico);

        try (var inputStream = uploadedFile.content()) {
            Files.copy(inputStream, filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            return nomeUnico;

        } catch (IOException e) {
            throw new RuntimeException("Erro de I/O ao salvar o arquivo no disco.", e);
        }
    }

    private void removerImagemDoDisco(String nomeImagem) {
        if (nomeImagem == null) return;
        String nomeImagemLimpa = nomeImagem.trim().replaceAll("[\\p{Cntrl}]", "");
        if (nomeImagemLimpa.isEmpty()) return;
        Path caminhoCompleto = Paths.get(UPLOADS_DIR, nomeImagemLimpa);

        try {
            if (Files.exists(caminhoCompleto)) {
                Files.delete(caminhoCompleto);
            }
        } catch (IOException e) {
            System.out.println("ERRO ao tentar remover arquivo ");
        }
    }

}