package org.supplychain.mysupply.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.supplychain.mysupply.approvisionnement.enums.SupplyOrderStatus;
import org.supplychain.mysupply.approvisionnement.model.RawMaterial;
import org.supplychain.mysupply.approvisionnement.model.Supplier;
import org.supplychain.mysupply.approvisionnement.model.SupplyOrder;
import org.supplychain.mysupply.approvisionnement.model.SupplyOrderLine;
import org.supplychain.mysupply.approvisionnement.repository.RawMaterialRepository;
import org.supplychain.mysupply.approvisionnement.repository.SupplierRepository;
import org.supplychain.mysupply.approvisionnement.repository.SupplyOrderLineRepository;
import org.supplychain.mysupply.approvisionnement.repository.SupplyOrderRepository;
import org.supplychain.mysupply.livraison.enums.CustomerOrderStatus;
import org.supplychain.mysupply.livraison.enums.DeliveryStatus;
import org.supplychain.mysupply.livraison.model.Customer;
import org.supplychain.mysupply.livraison.model.CustomerOrder;
import org.supplychain.mysupply.livraison.model.CustomerOrderLine;
import org.supplychain.mysupply.livraison.model.Delivery;
import org.supplychain.mysupply.livraison.repository.CustomerOrderLineRepository;
import org.supplychain.mysupply.livraison.repository.CustomerOrderRepository;
import org.supplychain.mysupply.livraison.repository.CustomerRepository;
import org.supplychain.mysupply.livraison.repository.DeliveryRepository;
import org.supplychain.mysupply.production.enums.Priority;
import org.supplychain.mysupply.production.enums.ProductionOrderStatus;
import org.supplychain.mysupply.production.model.BillOfMaterial;
import org.supplychain.mysupply.production.model.Product;
import org.supplychain.mysupply.production.model.ProductionOrder;
import org.supplychain.mysupply.production.repository.BillOfMaterialRepository;
import org.supplychain.mysupply.production.repository.ProductRepository;
import org.supplychain.mysupply.production.repository.ProductionOrderRepository;
import org.supplychain.mysupply.user.enums.Role;
import org.supplychain.mysupply.user.model.User;
import org.supplychain.mysupply.user.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final SupplyOrderRepository supplyOrderRepository;
    private final SupplyOrderLineRepository supplyOrderLineRepository;
    private final ProductRepository productRepository;
    private final BillOfMaterialRepository billOfMaterialRepository;
    private final ProductionOrderRepository productionOrderRepository;
    private final CustomerRepository customerRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final CustomerOrderLineRepository customerOrderLineRepository;
    private final DeliveryRepository deliveryRepository;

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.count() > 0) {
            System.out.println("Database already contains data. Skipping initialization.");
            return;
        }

        System.out.println("Initializing database with fake data...");

        createUsers();
        createSuppliers();
        createRawMaterials();
        createSupplyOrders();
        createProducts();
        createBillOfMaterials();
        createProductionOrders();
        createCustomers();
        createCustomerOrders();
        createDeliveries();

        System.out.println("Database initialization completed successfully!");
    }

    private void createUsers() {
        List<User> users = new ArrayList<>();

        users.add(new User(null, "Admin", "User", "admin@supply.com", "admin123", Role.ADMIN));

        users.add(new User(null, "Gestionnaire", "Approvisionnement", "gestionnaire.appro@supply.com", "appro123", Role.GESTIONNAIRE_APPROVISIONNEMENT));
        users.add(new User(null, "Responsable", "Achats", "responsable.achats@supply.com", "achats123", Role.RESPONSABLE_ACHATS));
        users.add(new User(null, "Superviseur", "Logistique", "superviseur.logistique@supply.com", "logistique123", Role.SUPERVISEUR_LOGISTIQUE));

        users.add(new User(null, "Chef", "Production", "chef.production@supply.com", "production123", Role.CHEF_PRODUCTION));
        users.add(new User(null, "Planificateur", "Prod", "planificateur@supply.com", "planif123", Role.PLANIFICATEUR));
        users.add(new User(null, "Superviseur", "Production", "superviseur.production@supply.com", "superprod123", Role.SUPERVISEUR_PRODUCTION));

        users.add(new User(null, "Gestionnaire", "Commercial", "gestionnaire.commercial@supply.com", "commercial123", Role.GESTIONNAIRE_COMMERCIAL));
        users.add(new User(null, "Responsable", "Logistique", "responsable.logistique@supply.com", "respolog123", Role.RESPONSABLE_LOGISTIQUE));
        users.add(new User(null, "Superviseur", "Livraisons", "superviseur.livraisons@supply.com", "livraison123", Role.SUPERVISEUR_LIVRAISONS));

        userRepository.saveAll(users);
        System.out.println("✓ Created " + users.size() + " users");
    }

    private void createSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();

        suppliers.add(new Supplier(null, "MetalCorp Industries", "Available 9AM-5PM", "contact@metalcorp.com", "+212-600-111111", 4.5, 7, new ArrayList<>(), new ArrayList<>()));
        suppliers.add(new Supplier(null, "PlasticWorld Supply", "24/7 Support", "info@plasticworld.com", "+212-600-222222", 4.2, 5, new ArrayList<>(), new ArrayList<>()));
        suppliers.add(new Supplier(null, "ChemTech Solutions", "Mon-Fri 8AM-6PM", "sales@chemtech.com", "+212-600-333333", 4.8, 10, new ArrayList<>(), new ArrayList<>()));
        suppliers.add(new Supplier(null, "WoodWorks Premium", "Available weekdays", "contact@woodworks.com", "+212-600-444444", 4.0, 14, new ArrayList<>(), new ArrayList<>()));
        suppliers.add(new Supplier(null, "ElectroComponents Ltd", "24/7 Emergency", "orders@electrocomp.com", "+212-600-555555", 4.6, 8, new ArrayList<>(), new ArrayList<>()));

        supplierRepository.saveAll(suppliers);
        System.out.println("✓ Created " + suppliers.size() + " suppliers");
    }

    private void createRawMaterials() {
        List<RawMaterial> materials = new ArrayList<>();

        materials.add(new RawMaterial(null, "Steel Sheets", "High-quality steel for construction", 500, 0, 100, "KG", LocalDate.now().minusDays(30), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        materials.add(new RawMaterial(null, "Aluminum Bars", "Lightweight aluminum material", 300, 0, 80, "KG", LocalDate.now().minusDays(20), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        materials.add(new RawMaterial(null, "Plastic Granules", "Recyclable plastic material", 800, 0, 150, "KG", LocalDate.now().minusDays(15), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        materials.add(new RawMaterial(null, "Copper Wire", "Electrical grade copper", 200, 0, 50, "M", LocalDate.now().minusDays(25), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        materials.add(new RawMaterial(null, "Rubber Sheets", "Industrial rubber material", 400, 0, 100, "KG", LocalDate.now().minusDays(10), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        materials.add(new RawMaterial(null, "Glass Panels", "Tempered safety glass", 150, 0, 30, "UNIT", LocalDate.now().minusDays(40), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        materials.add(new RawMaterial(null, "Wood Planks", "Premium oak wood", 600, 0, 120, "UNIT", LocalDate.now().minusDays(35), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        materials.add(new RawMaterial(null, "Chemical Adhesive", "Strong bonding adhesive", 250, 0, 60, "L", LocalDate.now().minusDays(5), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));

        rawMaterialRepository.saveAll(materials);
        System.out.println("✓ Created " + materials.size() + " raw materials");
    }

    private void createSupplyOrders() {
        List<Supplier> suppliers = supplierRepository.findAll();
        List<RawMaterial> materials = rawMaterialRepository.findAll();

        List<SupplyOrder> orders = new ArrayList<>();
        List<SupplyOrderLine> orderLines = new ArrayList<>();

        SupplyOrder order1 = new SupplyOrder(null, "SO-2024-001", suppliers.get(0), new ArrayList<>(), LocalDate.now().minusDays(10), SupplyOrderStatus.RECUE, new BigDecimal("50000.00"));
        orders.add(order1);
        orderLines.add(new SupplyOrderLine(null, order1, materials.get(0), 200, new BigDecimal("150.00")));
        orderLines.add(new SupplyOrderLine(null, order1, materials.get(1), 100, new BigDecimal("200.00")));

        SupplyOrder order2 = new SupplyOrder(null, "SO-2024-002", suppliers.get(1), new ArrayList<>(), LocalDate.now().minusDays(5), SupplyOrderStatus.EN_COURS, new BigDecimal("15000.00"));
        orders.add(order2);
        orderLines.add(new SupplyOrderLine(null, order2, materials.get(2), 300, new BigDecimal("50.00")));

        SupplyOrder order3 = new SupplyOrder(null, "SO-2024-003", suppliers.get(2), new ArrayList<>(), LocalDate.now().minusDays(2), SupplyOrderStatus.EN_ATTENTE, new BigDecimal("65000.00"));
        orders.add(order3);
        orderLines.add(new SupplyOrderLine(null, order3, materials.get(3), 150, new BigDecimal("300.00")));
        orderLines.add(new SupplyOrderLine(null, order3, materials.get(4), 200, new BigDecimal("100.00")));

        supplyOrderRepository.saveAll(orders);
        supplyOrderLineRepository.saveAll(orderLines);
        System.out.println("✓ Created " + orders.size() + " supply orders with " + orderLines.size() + " order lines");
    }

    private void createProducts() {
        List<Product> products = new ArrayList<>();

        products.add(new Product(null, "Premium Chair", "Ergonomic office chair with lumbar support", 8, new BigDecimal("150.00"), 50, 10, "UNIT", new ArrayList<>(), new ArrayList<>()));
        products.add(new Product(null, "Office Desk", "Modern office desk with cable management", 12, new BigDecimal("300.00"), 30, 5, "UNIT", new ArrayList<>(), new ArrayList<>()));
        products.add(new Product(null, "Storage Cabinet", "Steel cabinet with 4 drawers", 10, new BigDecimal("200.00"), 40, 8, "UNIT", new ArrayList<>(), new ArrayList<>()));
        products.add(new Product(null, "Conference Table", "Large table for 10 people", 16, new BigDecimal("500.00"), 20, 3, "UNIT", new ArrayList<>(), new ArrayList<>()));
        products.add(new Product(null, "Executive Chair", "Leather executive chair", 10, new BigDecimal("250.00"), 35, 7, "UNIT", new ArrayList<>(), new ArrayList<>()));

        productRepository.saveAll(products);
        System.out.println("✓ Created " + products.size() + " products");
    }

    private void createBillOfMaterials() {
        List<Product> products = productRepository.findAll();
        List<RawMaterial> materials = rawMaterialRepository.findAll();

        List<BillOfMaterial> boms = new ArrayList<>();

        boms.add(new BillOfMaterial(null, products.get(0), materials.get(0), 5));
        boms.add(new BillOfMaterial(null, products.get(0), materials.get(2), 3));
        boms.add(new BillOfMaterial(null, products.get(0), materials.get(4), 2));

        boms.add(new BillOfMaterial(null, products.get(1), materials.get(0), 10));
        boms.add(new BillOfMaterial(null, products.get(1), materials.get(6), 5));

        boms.add(new BillOfMaterial(null, products.get(2), materials.get(0), 8));
        boms.add(new BillOfMaterial(null, products.get(2), materials.get(5), 2));

        billOfMaterialRepository.saveAll(boms);
        System.out.println("✓ Created " + boms.size() + " bill of materials");
    }

    private void createProductionOrders() {
        List<Product> products = productRepository.findAll();

        List<ProductionOrder> orders = new ArrayList<>();

        orders.add(new ProductionOrder(null, "PO-2024-001", products.get(0), 20, ProductionOrderStatus.TERMINE, Priority.STANDARD, LocalDate.now().minusDays(15), LocalDate.now().minusDays(10), LocalDate.now().minusDays(5), LocalDate.now().minusDays(5), 160));
        orders.add(new ProductionOrder(null, "PO-2024-002", products.get(1), 15, ProductionOrderStatus.EN_PRODUCTION, Priority.URGENT, LocalDate.now().minusDays(5), LocalDate.now().minusDays(3), LocalDate.now().plusDays(2), null, 180));
        orders.add(new ProductionOrder(null, "PO-2024-003", products.get(2), 25, ProductionOrderStatus.EN_ATTENTE, Priority.STANDARD, LocalDate.now(), null, LocalDate.now().plusDays(7), null, 250));
        orders.add(new ProductionOrder(null, "PO-2024-004", products.get(3), 10, ProductionOrderStatus.EN_ATTENTE, Priority.URGENT, LocalDate.now().plusDays(1), null, LocalDate.now().plusDays(10), null, 160));

        productionOrderRepository.saveAll(orders);
        System.out.println("✓ Created " + orders.size() + " production orders");
    }

    private void createCustomers() {
        List<Customer> customers = new ArrayList<>();

        customers.add(new Customer(null, "TechCorp Solutions", "contact@techcorp.ma", "+212-600-001111", "123 Tech Street", "Casablanca", "20000", new ArrayList<>()));
        customers.add(new Customer(null, "Office Plus", "info@officeplus.ma", "+212-600-002222", "456 Business Avenue", "Rabat", "10000", new ArrayList<>()));
        customers.add(new Customer(null, "Furniture World", "sales@furnitureworld.ma", "+212-600-003333", "789 Commerce Road", "Marrakech", "40000", new ArrayList<>()));
        customers.add(new Customer(null, "Modern Spaces", "hello@modernspaces.ma", "+212-600-004444", "321 Design Boulevard", "Tangier", "90000", new ArrayList<>()));
        customers.add(new Customer(null, "Corporate Interiors", "contact@corporate.ma", "+212-600-005555", "654 Professional Lane", "Fes", "30000", new ArrayList<>()));

        customerRepository.saveAll(customers);
        System.out.println("✓ Created " + customers.size() + " customers");
    }

    private void createCustomerOrders() {
        List<Customer> customers = customerRepository.findAll();
        List<Product> products = productRepository.findAll();

        List<CustomerOrder> orders = new ArrayList<>();
        List<CustomerOrderLine> orderLines = new ArrayList<>();

        CustomerOrder order1 = new CustomerOrder(null, "CO-2024-001", customers.get(0), new ArrayList<>(), LocalDate.now().minusDays(8), new BigDecimal("3000.00"), CustomerOrderStatus.LIVREE, null, "First order from TechCorp");
        orders.add(order1);
        orderLines.add(new CustomerOrderLine(null, order1, products.get(0), 10, new BigDecimal("150.00"), new BigDecimal("1500.00")));
        orderLines.add(new CustomerOrderLine(null, order1, products.get(1), 5, new BigDecimal("300.00"), new BigDecimal("1500.00")));

        CustomerOrder order2 = new CustomerOrder(null, "CO-2024-002", customers.get(1), new ArrayList<>(), LocalDate.now().minusDays(4), new BigDecimal("1600.00"), CustomerOrderStatus.EN_ROUTE, null, "Bulk order");
        orders.add(order2);
        orderLines.add(new CustomerOrderLine(null, order2, products.get(2), 8, new BigDecimal("200.00"), new BigDecimal("1600.00")));

        CustomerOrder order3 = new CustomerOrder(null, "CO-2024-003", customers.get(2), new ArrayList<>(), LocalDate.now().minusDays(1), new BigDecimal("3000.00"), CustomerOrderStatus.EN_PREPARATION, null, "Conference room setup");
        orders.add(order3);
        orderLines.add(new CustomerOrderLine(null, order3, products.get(3), 3, new BigDecimal("500.00"), new BigDecimal("1500.00")));
        orderLines.add(new CustomerOrderLine(null, order3, products.get(4), 6, new BigDecimal("250.00"), new BigDecimal("1500.00")));

        customerOrderRepository.saveAll(orders);
        customerOrderLineRepository.saveAll(orderLines);
        System.out.println("✓ Created " + orders.size() + " customer orders with " + orderLines.size() + " order lines");
    }

    private void createDeliveries() {
        List<CustomerOrder> orders = customerOrderRepository.findAll();

        List<Delivery> deliveries = new ArrayList<>();

        deliveries.add(new Delivery(null, orders.get(0), "123 Tech Street, Casablanca", "Casablanca", "Mohammed Ali", "Truck-001", DeliveryStatus.LIVREE, LocalDate.now().minusDays(5), LocalDate.now().minusDays(3), new BigDecimal("150.00"), "TRACK-001", "Delivered successfully"));
        deliveries.add(new Delivery(null, orders.get(1), "456 Business Avenue, Rabat", "Rabat", "Fatima Zahra", "Van-045", DeliveryStatus.EN_COURS, LocalDate.now(), null, new BigDecimal("120.00"), "TRACK-002", "In transit"));
        deliveries.add(new Delivery(null, orders.get(2), "789 Commerce Road, Marrakech", "Marrakech", "Youssef Hassan", "Truck-002", DeliveryStatus.PLANIFIEE, LocalDate.now().plusDays(2), null, new BigDecimal("200.00"), "TRACK-003", "Scheduled for delivery"));

        deliveryRepository.saveAll(deliveries);
        System.out.println("✓ Created " + deliveries.size() + " deliveries");
    }
}