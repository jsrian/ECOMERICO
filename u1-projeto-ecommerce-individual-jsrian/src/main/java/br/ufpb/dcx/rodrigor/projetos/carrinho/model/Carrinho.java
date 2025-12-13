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

     public void removerItem(Produto produto){
        itens.removeIf(i-> i.getProduto().equals(produto));
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
