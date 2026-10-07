package client;

import SaleSys.SaleSys;
import business.DiscountType;
import business.exception.ApplicationException;
import dataaccess.exception.PersistenceException;
import dataaccess.rdgw.ProductRowDataGateway;
import dbutils.SetupDatabase;
import presentation.CustomerService;
import presentation.SaleService;

public class SimpleClient {

    public static void main(String[] args) {
        // --- 1. SETUP THE DATABASE (runs only once on first start) ---
        try {
            SetupDatabase.run();
        } catch (PersistenceException e) {
            System.err.println("FATAL: Could not setup the database.");
            e.printStackTrace();
            return; // Exit if DB setup fails
        }

        SaleSys app = new SaleSys();
        try {
            // --- 2. START THE APPLICATION SERVICES ---
            app.run();
            System.out.println("Application services started successfully.");

            // TESTE DO ProductRowDataGateway
            // Adicione este bloco em SimpleClient.java para testar a sua implementação
            try {
                System.out.println("A testar a sua implementação de ProductRowDataGateway...");

                // Terá de criar um construtor público no seu ProductRowDataGateway
                // para que a linha seguinte funcione!
                ProductRowDataGateway newProduct = new ProductRowDataGateway(999, "O Meu Produto de Teste", 19.99, 50);
                newProduct.insert();
                System.out.println("Produto inserido com sucesso com o ID: " + newProduct.getProductId());

                System.out.println("\nA procurar o produto com o código 999...");
                ProductRowDataGateway foundProduct = ProductRowDataGateway.findWithProdCod(999);
                System.out.println("Produto encontrado: " + foundProduct.getDescription());

            } catch (PersistenceException e) {
                System.err.println("Erro ao testar o ProductRowDataGateway!");
                e.printStackTrace();
            }

            // --- 3. REGISTER A SHUTDOWN HOOK ---
            // This code will run when the application is terminated (e.g., by Ctrl+C)
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nShutdown hook initiated. Closing resources...");
                app.stopRun();
                System.out.println("Application stopped gracefully.");
            }));

            // --- 4. RUN THE INITIAL BUSINESS TRANSACTION (for demonstration) ---
            CustomerService cs = app.getCustomerService();
            SaleService ss = app.getSaleService();

            System.out.println("\n--- Starting initial business transaction ---");
            cs.addCustomer(168027852, "Customer 1", 217500255, DiscountType.SALE_AMOUNT);
            System.out.println("Customer added.");

            int saleId = ss.newSale(168027852);
            System.out.println("New sale created with ID: " + saleId);

            ss.addProductToSale(saleId, 123, 10);
            ss.addProductToSale(saleId, 124, 5);
            System.out.println("Products added to sale.");

            double discount = ss.getSaleDiscount(saleId);
            System.out.println("Calculated Sale Discount: " + discount);
            System.out.println("--- Initial business transaction finished ---\n");

            // --- 5. KEEP THE APPLICATION ALIVE INDEFINITELY ---
            System.out.println("Application is now running. Press Ctrl+C to stop.");
            Thread.sleep(Long.MAX_VALUE); // This will pause the main thread forever

        } catch (ApplicationException e) {
            System.err.println("A critical error occurred during the application lifecycle:");
            e.printStackTrace();
        } catch (InterruptedException e) {
            // This block is entered when the sleep is interrupted, usually during shutdown.
            System.out.println("Application sleep interrupted. Shutting down.");
            Thread.currentThread().interrupt();
        }
    }
}