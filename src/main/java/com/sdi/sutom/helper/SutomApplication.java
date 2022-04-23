package com.sdi.sutom.helper;

import java.util.Scanner;

public class SutomApplication {

    public static void main(final String[] args) {
        final var scanner = new Scanner(System.in);

        System.out.print("Pattern (ex: A....) : ");
        final var pattern = scanner.nextLine();

        System.out.print("Caractères inclus (ex: JT) : ");
        final var inclusion = scanner.nextLine();

        System.out.print("Caractères exclus (ex : BLG) : ");
        final var exclusion = scanner.nextLine();

        System.out.print("Nombre d'éléments à afficher : ");
        final var pageSize = scanner.nextLine();

        final var service = new SutomService();
        service.init();

        if (pageSize.isEmpty()) {
            final var result = service.search(new SutomRequest(pattern, inclusion, exclusion));
            result.forEach(System.out::println);
        } else {
            final var result = service.search(new SutomRequest(pattern, inclusion, exclusion), Integer.parseInt(pageSize));
            result.forEachRemaining(subList -> {
                subList.forEach(System.out::println);
                System.out.print("\nTaper Entrer pour continuer ou Q pour quitter : ");
                final var in = scanner.nextLine();
                if (in.equalsIgnoreCase("q")) {
                    System.exit(0);
                }
                System.out.println();
            });
        }
    }
}
