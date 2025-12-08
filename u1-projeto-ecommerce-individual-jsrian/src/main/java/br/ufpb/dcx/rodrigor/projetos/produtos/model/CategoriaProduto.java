package br.ufpb.dcx.rodrigor.projetos.produtos.model;

public enum CategoriaProduto {
    INFORMATICA("doces"),
    ELETRODOMESTICOS("salgados"),
    MOVEIS("moveis");
    private final String id;

    CategoriaProduto(String id){
        this.id = id;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
