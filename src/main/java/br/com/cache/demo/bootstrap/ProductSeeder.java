package br.com.cache.demo.bootstrap;

import br.com.cache.demo.product.Product;
import br.com.cache.demo.product.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ProductSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    public ProductSeeder(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        productRepository.saveAll(List.of(
                new Product("Notebook Dell Inspiron", "Notebook com 16GB RAM e SSD 512GB", "Informatica", new BigDecimal("3599.90"), 8),
                new Product("Monitor LG UltraWide", "Monitor 29 polegadas com resolucao Full HD", "Informatica", new BigDecimal("1299.90"), 15),
                new Product("Teclado Mecanico Redragon", "Teclado mecanico RGB com switches brown", "Perifericos", new BigDecimal("249.90"), 30),
                new Product("Mouse Logitech MX Master", "Mouse sem fio ergonomico para produtividade", "Perifericos", new BigDecimal("469.90"), 12),
                new Product("Headset HyperX Cloud", "Headset gamer com microfone removivel", "Audio", new BigDecimal("399.90"), 18),
                new Product("Smartphone Samsung Galaxy", "Smartphone Android com 256GB de armazenamento", "Telefonia", new BigDecimal("2899.90"), 10),
                new Product("Tablet Lenovo Tab", "Tablet com tela 10 polegadas e Wi-Fi", "Telefonia", new BigDecimal("1199.90"), 7),
                new Product("Cadeira Ergonomica", "Cadeira de escritorio com apoio lombar", "Moveis", new BigDecimal("899.90"), 6),
                new Product("Webcam Logitech C920", "Webcam Full HD para reunioes e streaming", "Perifericos", new BigDecimal("349.90"), 22),
                new Product("SSD Kingston 1TB", "SSD SATA 1TB para computadores e notebooks", "Componentes", new BigDecimal("379.90"), 25),
                new Product("Memoria RAM 16GB", "Modulo DDR4 16GB 3200MHz", "Componentes", new BigDecimal("299.90"), 20),
                new Product("Roteador TP-Link Archer", "Roteador dual band com Wi-Fi 6", "Redes", new BigDecimal("449.90"), 14),
                new Product("Impressora HP Laser", "Impressora laser monocromatica", "Impressoras", new BigDecimal("1099.90"), 5),
                new Product("HD Externo Seagate 2TB", "Disco externo USB 3.0 para backup", "Armazenamento", new BigDecimal("489.90"), 16),
                new Product("Caixa de Som JBL", "Caixa Bluetooth portatil resistente a agua", "Audio", new BigDecimal("599.90"), 11),
                new Product("Carregador USB-C 65W", "Carregador rapido para notebook e smartphone", "Acessorios", new BigDecimal("189.90"), 28),
                new Product("Cabo HDMI 2.1", "Cabo HDMI com suporte a 4K e 120Hz", "Acessorios", new BigDecimal("59.90"), 40),
                new Product("Placa de Video RTX 4060", "GPU para jogos e aplicacoes graficas", "Componentes", new BigDecimal("2199.90"), 4),
                new Product("Fonte Corsair 650W", "Fonte ATX 80 Plus Bronze", "Componentes", new BigDecimal("399.90"), 9),
                new Product("Gabinete Gamer", "Gabinete mid tower com vidro temperado", "Componentes", new BigDecimal("329.90"), 13)
        ));
    }
}
