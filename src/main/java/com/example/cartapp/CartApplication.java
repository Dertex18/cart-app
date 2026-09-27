package com.example.cartapp;

import com.example.cartapp.model.Cart;
import com.example.cartapp.model.Product;
import com.example.cartapp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@SpringBootApplication
public class CartApplication implements CommandLineRunner {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ProductRepository productRepository;

    public static void main(String[] args) {

        System.setProperty("spring.main.banner-mode", "off");
        SpringApplication.run(CartApplication.class, args);
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);


        Cart cart = context.getBean(Cart.class);

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> showProducts();
                case "2" -> addToCart(scanner, cart);
                case "3" -> removeFromCart(scanner, cart);
                case "4" -> showCart(cart);
                case "5" -> demonstratePrototypeScope();
                case "0" -> {
                    running = false;
                    System.out.println("До побачення!");
                }
                default -> System.out.println("Невірний пункт меню, спробуйте ще раз.");
            }
        }
        scanner.close();
    }

    private void printMenu() {
        System.out.println();
        System.out.println("===== КОШИК ПОКУПОК =====");
        System.out.println("1. Показати список товарів");
        System.out.println("2. Додати товар до кошика");
        System.out.println("3. Видалити товар з кошика");
        System.out.println("4. Показати вміст кошика");
        System.out.println("5. Демонстрація: prototype scope Cart");
        System.out.println("0. Вихід");
        System.out.print("Ваш вибір: ");
    }

    private void showProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            System.out.println("Товарів немає.");
            return;
        }
        System.out.println("--- Список товарів ---");
        for (Product p : products) {
            System.out.printf("id=%d | %s | %.2f грн%n", p.getId(), p.getName(), p.getPrice());
        }
    }

    private void addToCart(Scanner scanner, Cart cart) {
        showProducts();
        System.out.print("Введіть id товару, який додати в кошик: ");
        Long id = readId(scanner);
        if (id == null) return;

        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            System.out.println("Товар з таким id не знайдено.");
            return;
        }
        cart.addProduct(product.get());
        System.out.println("Товар \"" + product.get().getName() + "\" додано до кошика.");
    }

    private void removeFromCart(Scanner scanner, Cart cart) {
        if (cart.isEmpty()) {
            System.out.println("Кошик порожній, видаляти нічого.");
            return;
        }
        showCart(cart);
        System.out.print("Введіть id товару, який видалити з кошика: ");
        Long id = readId(scanner);
        if (id == null) return;

        boolean removed = cart.removeProductById(id);
        System.out.println(removed
                ? "Товар видалено з кошика."
                : "У кошику немає товару з таким id.");
    }

    private void showCart(Cart cart) {
        List<Product> items = cart.getItems();
        if (items.isEmpty()) {
            System.out.println("Кошик порожній.");
            return;
        }
        System.out.println("--- Вміст кошика ---");
        for (Product p : items) {
            System.out.printf("id=%d | %s | %.2f грн%n", p.getId(), p.getName(), p.getPrice());
        }
        System.out.printf("Разом: %.2f грн%n", cart.getTotal());
    }


    private void demonstratePrototypeScope() {
        Cart first = context.getBean(Cart.class);
        Cart second = context.getBean(Cart.class);
        System.out.println("Перший виклик context.getBean(Cart.class):  " + first);
        System.out.println("Другий виклик context.getBean(Cart.class):  " + second);
        System.out.println("Це різні об'єкти: " + (first != second));
    }

    private Long readId(Scanner scanner) {
        String input = scanner.nextLine().trim();
        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            System.out.println("Некоректний id.");
            return null;
        }
    }
}
