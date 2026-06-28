package br.com.cache.demo.product;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("Produto " + id + " nao encontrado");
    }
}
