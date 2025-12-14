package br.ufpb.dcx.rodrigor.projetos.carrinho.model;

import br.ufpb.dcx.rodrigor.projetos.produtos.model.Produto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Carrinho {
    private String id;
    private List<ItemCarrinho> itens = new ArrayList<>();


    public void adicionarItem(ItemCarrinho novoItem){
        Optional<ItemCarrinho> itemExiste = itens.stream().filter
                (i -> i.getProduto().getId().
                        equals(novoItem.getProduto().getId())).findFirst();
        if (itemExiste.isPresent()){
            ItemCarrinho item = itemExiste.get();
            item.setQuantidade(item.getQuantidade()+ novoItem.getQuantidade());
        }else{
            this.itens.add(novoItem);
        }
    }

    public void removerItemPeloId(String produtoId) {

        final String idURL = (produtoId != null) ? produtoId.trim() : null;
        itens.removeIf(item -> {
            Produto produtoEmMemoria = item.getProduto();
            if (produtoEmMemoria == null) {
                return false;
            }
            String idEmMemoria = produtoEmMemoria.getId();
            boolean match = (idEmMemoria != null) && idEmMemoria.trim().equals(idURL);
            return match;
        });
    }

    public List<ItemCarrinho> getItens() {
        return itens;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getTotal() {
        return itens.stream()
                .map(ItemCarrinho::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
