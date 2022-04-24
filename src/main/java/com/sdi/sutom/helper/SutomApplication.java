package com.sdi.sutom.helper;

import java.util.Scanner;

public class SutomApplication {

    public static void main(final String[] args) {
        final var scanner = new Scanner(System.in);
        var option = "";
        do {
            performSearch(scanner);
            System.out.print("\nTapez Entrer pour faire une nouvelle recherche ou Q pour quitter : ");
            option = scanner.nextLine();
        } while (!option.equalsIgnoreCase("q"));
        System.out.println("\nA bientôt pour d'autres aventures");
    }

    private static void performSearch(Scanner scanner) {
        System.out.print("\nPattern (ex: A....) : ");
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
            if (result.isEmpty()) {
                System.out.println("Aucun résultat trouvé");
            }
            result.forEach(System.out::println);
        } else {
            final var result = service.search(new SutomRequest(pattern, inclusion, exclusion), Integer.parseInt(pageSize));
            if (!result.hasNext()) {
                System.out.println("Aucun résultat trouvé");
            }
            var option = "";
            while (result.hasNext() && !option.equalsIgnoreCase("q")) {
                result.next().forEach(System.out::println);
                System.out.print("\nTaper Entrer pour continuer ou Q pour quitter : ");
                option = scanner.nextLine();
            }
        }
    }
}
