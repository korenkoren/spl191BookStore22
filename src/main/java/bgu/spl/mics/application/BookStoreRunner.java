package bgu.spl.mics.application;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BookStoreRunner {
    public static void main(String[] args) {
        ArrayList<Thread> threads = new ArrayList<>();
        JsonParser parser = new JsonParser();
        try {
            JsonObject obj = (JsonObject) parser.parse(new FileReader(args[0]));
            JsonArray jsonArray = obj.get("initialInventory").getAsJsonArray();
            BookInventoryInfo[] bookInventory = initialInventoryList(jsonArray);
            jsonArray = obj.get("initialResources").getAsJsonArray().get(0).getAsJsonObject().get("vehicles").getAsJsonArray();
            DeliveryVehicle[] deliveryVehicles = initialResources(jsonArray);
            JsonArray json = obj.get("services").getAsJsonObject().get("customers").getAsJsonArray();
            Customer[] customers = customers(json);
            Map<Integer, Customer> customerMap = new HashMap<>();
            for (Customer customer : customers)
                customerMap.put(customer.getId(), customer);
            ResourcesHolder resourcesHolder = ResourcesHolder.getInstance();
            resourcesHolder.load(deliveryVehicles);
            Inventory inventory = Inventory.getInstance();
            inventory.load(bookInventory);

            initilizeServices(obj, threads,customers);
            createSerializedObject((Serializable) customerMap, args[1]);
            Inventory.getInstance().printInventoryToFile(args[2]);
            MoneyRegister.getInstance().printOrderReceipts(args[3]);
            createSerializedObject((Serializable) MoneyRegister.getInstance(), args[4]);
            /*System.out.println(testSeriliazedObject(customerMap, args[1]));
            System.out.println(testSeriliazedObject(MoneyRegister.getInstance(), args[4]));*/
//            printSerializedObject(args[1]);
//            printSerializedObject(args[2]);
//            printSerializedObject(args[3]);
//            printSerializedObject(args[4]);



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static BookInventoryInfo[] initialInventoryList(JsonArray obj) {
        Gson gson = new Gson();
        return gson.fromJson(obj, BookInventoryInfo[].class);
    }

    private static DeliveryVehicle[] initialResources(JsonArray obj) {
        Gson gson = new Gson();
        return gson.fromJson(obj, DeliveryVehicle[].class);
    }

    public static Customer[] customers(JsonArray obj) {
        Gson gson = new Gson();
        return gson.fromJson(obj, Customer[].class);
    }

    public static void initilizeServices(JsonObject obj, ArrayList<Thread> threads,Customer[] customers) {
        int timeDuration = obj.get("services").getAsJsonObject().get("time").getAsJsonObject().get("duration").getAsInt();
        int numberOfSellingServices = obj.get("services").getAsJsonObject().get("selling").getAsInt();
        int numberOfInventoryServices = obj.get("services").getAsJsonObject().get("inventoryService").getAsInt();
        int numberOfLogistics = obj.get("services").getAsJsonObject().get("logistics").getAsInt();
        int numberOfResourceService = obj.get("services").getAsJsonObject().get("resourcesService").getAsInt();
        int timeSpeed = obj.get("services").getAsJsonObject().get("time").getAsJsonObject().get("speed").getAsInt();
        Thread timeThread = new Thread(new TimeService(timeSpeed, timeDuration));
        CountDownLatch countDownLatch = new CountDownLatch(numberOfInventoryServices+numberOfLogistics+numberOfResourceService+numberOfSellingServices+customers.length);
        for (int i = 1; i <= customers.length; i++)
            threads.add(new Thread(new APIService(customers[i - 1],countDownLatch)));
        for (int i = 1; i <= numberOfInventoryServices; i++)
            threads.add(new Thread(new InventoryService(countDownLatch)));
        for (int i = 1; i <= numberOfSellingServices; i++)
            threads.add(new Thread(new SellingService(countDownLatch)));
        for (int i = 1; i <= numberOfLogistics; i++)
            threads.add(new Thread(new LogisticsService(countDownLatch)));
        for (int i = 1; i <= numberOfResourceService; i++) {
            threads.add(new Thread(new ResourceService(countDownLatch)));
        }

        for (Thread t : threads)
            t.start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        timeThread.start();
        try {
            for (Thread t : threads)
                t.join();
            timeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void createSerializedObject(Serializable s, String filename) {
        try {
            FileOutputStream out = new FileOutputStream(filename);
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(s);
            objOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printSerializedObject(String filename){
        try {
            ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(filename));
            Object o2 = objIn.readObject();
            System.out.println(o2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean testSeriliazedObject(Object o1, String path) {
        try {
            ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(path));
            Object o2 = objIn.readObject();
            boolean output =  o1.equals(o2);
            return output;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    public static String customers2string(Customer[] customers) {
        String str = "";
        for (Customer customer : customers)
            str += customer2string(customer) + "\n---------------------------\n";
        return str;
    }

    public static String customer2string(Customer customer) {
        String str = "id    : " + customer.getId() + "\n";
        str += "name  : " + customer.getName() + "\n";
        str += "addr  : " + customer.getAddress() + "\n";
        str += "dist  : " + customer.getDistance() + "\n";
        str += "card  : " + customer.getCreditNumber() + "\n";
        str += "money : " + customer.getAvailableCreditAmount();
        return str;
    }

    public static String books2string(BookInventoryInfo[] books) {
        String str = "";
        for (BookInventoryInfo book : books)
            str += book2string(book) + "\n---------------------------\n";
        return str;
    }

    public static String book2string(BookInventoryInfo book) {
        String str = "";
        str += "title  : " + book.getBookTitle() + "\n";
        str += "amount : " + book.getAmountInInventory() + "\n";
        str += "price  : " + book.getPrice();
        return str;
    }


    public static String receipts2string(OrderReceipt[] receipts) {
        String str = "";
        for (OrderReceipt receipt : receipts)
            str += receipt2string(receipt) + "\n---------------------------\n";
        return str;
    }
    public static String receipt2string(OrderReceipt receipt) {
        String str = "";
        str += "customer   : " + receipt.getCustomerId() + "\n";
        str += "order tick : " + receipt.getOrderTick() + "\n";
        str += "id         : " + receipt.getOrderId() + "\n";
        str += "price      : " + receipt.getPrice() + "\n";
        str += "seller     : " + receipt.getSeller();
        return str;
    }

    public static void Print(String str, String filename) {
        try {
            try (PrintStream out = new PrintStream(new FileOutputStream(filename))) {
                out.print(str);
            }
        } catch (IOException e) {
            System.out.println("Exception: " + e.getClass().getSimpleName());
        }
    }
}


