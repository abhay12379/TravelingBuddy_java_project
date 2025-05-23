import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class IntegratedProject {

    static class Edge {
        String destination;
        int distance;

        Edge(String destination, int distance) {
            this.destination = destination;
            this.distance = distance;
        }
    }

    static class Graph {
        private final Map<String, List<Edge>> roadMap = new HashMap<>();
        private final Map<String, List<Edge>> trainMap = new HashMap<>();
        private final Map<String, List<Edge>> planeMap = new HashMap<>();
        private final Map<String, List<Edge>> busMap = new HashMap<>();

        public void addEdge(Map<String, List<Edge>> map, String from, String to, int distance) {
            map.putIfAbsent(from, new ArrayList<>());
            map.putIfAbsent(to, new ArrayList<>());
            map.get(from).add(new Edge(to, distance));
            map.get(to).add(new Edge(from, distance));
        }

        public void addRoadEdge(String from, String to, int distance) {
            addEdge(roadMap, from, to, distance);
        }

        public void addTrainEdge(String from, String to) {
            addEdge(trainMap, from, to, 1);
        }

        public void addPlaneEdge(String from, String to) {
            addEdge(planeMap, from, to, 1);
        }

        public void addBusEdge(String from, String to) {
            addEdge(busMap, from, to, 1);
        }

        public List<String> findRoutes(String start, String end, String mode) {
            Map<String, List<Edge>> selectedMap = switch (mode) {
                case "Road" -> roadMap;
                case "Train" -> trainMap;
                case "Plane" -> planeMap;
                case "Bus" -> busMap;
                default -> null;
            };

            if (selectedMap == null) {
                return List.of("Invalid transport mode selected.");
            }

            List<String> routes = new ArrayList<>();
            boolean showDistance = mode.equals("Road");

            findRoutesRecursive(selectedMap, start, end, new ArrayList<>(), new HashSet<>(), routes, 0, showDistance);

            if (routes.isEmpty()) {
                routes.add("No routes found between " + start + " and " + end);
            }
            return routes;
        }

        private void findRoutesRecursive(Map<String, List<Edge>> map, String current, String end,
                                         List<String> path, Set<String> visited, List<String> routes,
                                         int distance, boolean showDistance) {
            visited.add(current);
            path.add(current);

            if (current.equals(end)) {
                if (showDistance) {
                    routes.add(String.format("Route: %s || Distance: %d", path, distance));
                } else {
                    routes.add(String.format("Route: %s", path));
                }
            } 
			else {
                for (Edge edge : map.getOrDefault(current, new ArrayList<>())) {
                    if (!visited.contains(edge.destination)) {
                        findRoutesRecursive(map, edge.destination, end, path, visited, routes,
                                distance + edge.distance, showDistance);
                    }
                }
            }

            path.remove(path.size() - 1);
            visited.remove(current);
        }
    }

    static class FoodItem {
        String name;
        int price;

        FoodItem(String name, int price) {
            this.name = name;
            this.price = price;
        }
    }

    static class DestinationFood {
        String destination;
        Map<String, List<FoodItem>> restaurantMenu;

        DestinationFood(String destination) {
            this.destination = destination;
            this.restaurantMenu = new HashMap<>();
        }

        public void addRestaurant(String restaurantName, List<FoodItem> menu) {
            restaurantMenu.put(restaurantName, menu);
        }

        public String getFoodDetails() {
            StringBuilder details = new StringBuilder("Famous Food in " + destination + ":\n");
            for (String restaurant : restaurantMenu.keySet()) {
                details.append(restaurant).append(":\n");
                for (FoodItem item : restaurantMenu.get(restaurant)) {
                    details.append("   - ").append(item.name).append(" (Rs.").append(item.price).append(")\n");
                }
                details.append("\n");
            }
            return details.toString();
        }
    }

    static class RoomCategory {
        String category;
        int pricePerDay;

        RoomCategory(String category, int pricePerDay) {
            this.category = category;
            this.pricePerDay = pricePerDay;
        }
    }

    static class Hotel {
        String name;
        Map<String, RoomCategory> roomCategories;

        Hotel(String name) {
            this.name = name;
            this.roomCategories = new HashMap<>();
        }

        public void addRoomCategory(String category, int pricePerDay) {
            roomCategories.put(category, new RoomCategory(category, pricePerDay));
        }

        public String getHotelDetails() {
            StringBuilder details = new StringBuilder("Hotel: " + name + "\n");
            for (Map.Entry<String, RoomCategory> entry : roomCategories.entrySet()) {
                RoomCategory category = entry.getValue();
                details.append("   - ").append(category.category)
                        .append(" Room: Rs.").append(category.pricePerDay)
                        .append(" per day\n");
            }
            details.append("\n");
            return details.toString();
        }
    }

    static class DestinationHotel {
        String destination;
        List<Hotel> hotels;

        DestinationHotel(String destination) {
            this.destination = destination;
            this.hotels = new ArrayList<>();
        }

        public void addHotel(Hotel hotel) {
            hotels.add(hotel);
        }

        public String getHotelDetails() {
            StringBuilder details = new StringBuilder("Hotels in " + destination + ":\n");
            for (Hotel hotel : hotels) {
                details.append(hotel.getHotelDetails());
            }
            return details.toString();
        }
    }

    public static void main(String[] args) {
        Graph graph = new Graph();
        setupSampleData(graph);

        Map<String, DestinationFood> foodData = new HashMap<>();
        Map<String, DestinationHotel> hotelData = new HashMap<>();
        setupFoodAndHotelData(foodData, hotelData);

        JFrame frame = new JFrame("Smart Trip Planner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel routeFinderPanel = createRouteFinderPanel(graph);
        tabbedPane.addTab("Route Finder", routeFinderPanel);

        JPanel budgetPlannerPanel = createBudgetPlannerPanel();
        tabbedPane.addTab("Budget Planner", budgetPlannerPanel);

        JPanel foodAndHotelExplorerPanel = createFoodAndHotelExplorerPanel(foodData, hotelData);
        tabbedPane.addTab("Food & Hotels", foodAndHotelExplorerPanel);

        frame.add(tabbedPane);
        frame.setVisible(true);
    }

    private static void setupSampleData(Graph graph) {
        graph.addRoadEdge("Delhi", "UP", 50);
		graph.addRoadEdge("Delhi", "Haryana", 100);
        graph.addRoadEdge("UP", "Bihar", 50);
        graph.addRoadEdge("UP", "Rajasthan", 150);
		graph.addRoadEdge("UP", "Haryana", 80);
		graph.addRoadEdge("UP", "Rajasthan", 150);
		graph.addRoadEdge("UP", "Jharkhand", 90);
        graph.addRoadEdge("MP", "Maharashtra", 130);
        graph.addRoadEdge("MP", "Gujarat", 400);
		graph.addRoadEdge("MP", "Rajasthan", 230);
        graph.addRoadEdge("Bihar", "West Bengal", 200);
        graph.addRoadEdge("Bihar", "Jharkhand", 80);
        
        
		graph.addTrainEdge("Delhi", "UP");
		graph.addTrainEdge("Delhi", "Haryana");
        graph.addTrainEdge("UP", "Bihar");
        graph.addTrainEdge("UP", "Rajasthan");
		graph.addTrainEdge("UP", "Haryana");
		graph.addTrainEdge("UP", "Rajasthan");
		graph.addTrainEdge("UP", "Jharkhand");
        graph.addTrainEdge("MP", "Maharashtra");
        graph.addTrainEdge("MP", "Gujarat");
		graph.addTrainEdge("MP", "Rajasthan");
        graph.addTrainEdge("Bihar", "West Bengal");
        graph.addTrainEdge("Bihar", "Jharkhand");
        
        
    
		graph.addPlaneEdge("Delhi", "UP");
		graph.addPlaneEdge("Delhi", "Haryana");
		graph.addPlaneEdge("Delhi", "West Bengal");
		graph.addPlaneEdge("Delhi", "Rajasthan");
		graph.addPlaneEdge("Delhi", "Jharkhand");
		graph.addPlaneEdge("Delhi", "Maharashtra");
		graph.addPlaneEdge("Delhi", "Gujarat");
		graph.addPlaneEdge("Delhi", "Bihar");
		graph.addPlaneEdge("Delhi", "MP");
        graph.addPlaneEdge("UP", "Bihar");
        graph.addPlaneEdge("UP", "Rajasthan");
		graph.addPlaneEdge("UP", "Haryana");
		graph.addPlaneEdge("UP", "Rajasthan");
		graph.addPlaneEdge("UP", "Jharkhand");
        graph.addPlaneEdge("MP", "Maharashtra");
        graph.addPlaneEdge("MP", "Gujarat");
		graph.addPlaneEdge("MP", "Rajasthan");
        graph.addPlaneEdge("Bihar", "West Bengal");
        graph.addPlaneEdge("Bihar", "Jharkhand");
 
    }

    private static void setupFoodAndHotelData(Map<String, DestinationFood> foodData, Map<String, DestinationHotel> hotelData) {
        //DestinationFood delhiFood = new DestinationFood("Delhi");
        //delhiFood.addRestaurant("Anand Stall", Arrays.asList(new FoodItem("Chole Bhature", 50), new FoodItem("Samosa", 20)));
        //delhiFood.addRestaurant("Haldiram's", Arrays.asList(new FoodItem("Rajma Chawal", 120), new FoodItem("Paneer Pakora", 80)));
	
DestinationFood delhiFood = new DestinationFood("Delhi");		
delhiFood.addRestaurant("Anand Stall", Arrays.asList(
    new FoodItem("Chole Bhature", 50),
    new FoodItem("Aloo Tikki", 40),
    new FoodItem("Samosa", 20)
));
delhiFood.addRestaurant("Sardar Pav Bhaji", Arrays.asList(
    new FoodItem("Pav Bhaji", 90),
    new FoodItem("Masala Pav", 50),
    new FoodItem("Cheese Pav", 70)
));
delhiFood.addRestaurant("Bademiya", Arrays.asList(
    new FoodItem("Seekh Kebab", 180),
    new FoodItem("Chicken Bhuna", 220),
    new FoodItem("Mutton Korma", 250)
));
delhiFood.addRestaurant("Britannia", Arrays.asList(
    new FoodItem("Berry Pulao", 240),
    new FoodItem("Mutton Dhansak", 260),
    new FoodItem("Pulao", 150)
));
delhiFood.addRestaurant("Cafe Madras", Arrays.asList(
    new FoodItem("Idli", 40),
    new FoodItem("Medu Vada", 45),
    new FoodItem("Sambar", 30)
));

DestinationFood mumbaiFood = new DestinationFood("Mumbai");
mumbaiFood.addRestaurant("Anand Stall", Arrays.asList(
    new FoodItem("Vada Pav", 25),
    new FoodItem("Sabudana Vada", 30),
    new FoodItem("Batata Vada", 35)
));
mumbaiFood.addRestaurant("Sardar Pav Bhaji", Arrays.asList(
    new FoodItem("Pav Bhaji", 90),
    new FoodItem("Masala Pav", 50),
    new FoodItem("Cheese Pav Bhaji", 100)
));
mumbaiFood.addRestaurant("Bademiya", Arrays.asList(
    new FoodItem("Seekh Kebab", 180),
    new FoodItem("Chicken Bhuna", 220),
    new FoodItem("Mutton Seekh", 210)
));
mumbaiFood.addRestaurant("Britannia", Arrays.asList(
    new FoodItem("Berry Pulao", 240),
    new FoodItem("Mutton Dhansak", 260),
    new FoodItem("Mutton Pulao", 230)
));
mumbaiFood.addRestaurant("Cafe Madras", Arrays.asList(
    new FoodItem("Idli", 40),
    new FoodItem("Medu Vada", 45),
    new FoodItem("Dosa", 60)
));

DestinationFood tamilNaduFood = new DestinationFood("Tamil Nadu");
tamilNaduFood.addRestaurant("Anand Stall", Arrays.asList(
    new FoodItem("Dosa", 30),
    new FoodItem("Idli", 20),
    new FoodItem("Vada", 25)
));
tamilNaduFood.addRestaurant("Sardar Pav Bhaji", Arrays.asList(
    new FoodItem("Pav Bhaji", 70),
    new FoodItem("Masala Pav", 40),
    new FoodItem("Cheese Pav", 80)
));

DestinationFood uttarPradeshFood = new DestinationFood("UP");
uttarPradeshFood.addRestaurant("Tunday Kababi", Arrays.asList(
    new FoodItem("Tunday Kebab", 250),
    new FoodItem("Biryani", 200),
    new FoodItem("Mutton Korma", 270)
));
uttarPradeshFood.addRestaurant("Shree Chappal", Arrays.asList(
    new FoodItem("Chappal", 50),
    new FoodItem("Tandoori Roti", 30),
    new FoodItem("Paneer Butter Masala", 120)
));

DestinationFood westBengalFood = new DestinationFood("West Bengal");
westBengalFood.addRestaurant("Oh Calcutta", Arrays.asList(
    new FoodItem("Macher Jhol", 220),
    new FoodItem("Shorshe Ilish", 300),
    new FoodItem("Luchi", 150)
));
westBengalFood.addRestaurant("Benaras Biryani", Arrays.asList(
    new FoodItem("Benaras Biryani", 230),
    new FoodItem("Shahi Korma", 260)
));

DestinationFood punjabFood = new DestinationFood("Punjab");
punjabFood.addRestaurant("Amritsari", Arrays.asList(
    new FoodItem("Amritsari Kulcha", 90),
    new FoodItem("Chole Bhature", 70),
    new FoodItem("Paneer Tikka", 150)
));
punjabFood.addRestaurant("Punjabi Tandoor", Arrays.asList(
    new FoodItem("Tandoori Chicken", 200),
    new FoodItem("Lassi", 100)
));

DestinationFood rajasthanFood = new DestinationFood("Rajasthan");
rajasthanFood.addRestaurant("Rawat", Arrays.asList(
    new FoodItem("Pyaaz Kachori", 50),
    new FoodItem("Dal Baati Churma", 150),
    new FoodItem("Ghevar", 100)
));

DestinationFood gujaratFood = new DestinationFood("Gujarat");
gujaratFood.addRestaurant("Khaman", Arrays.asList(
    new FoodItem("Dhokla", 40),
    new FoodItem("Khandvi", 50),
    new FoodItem("Farsan", 60)
));

		
		
		

        DestinationHotel delhiHotel = new DestinationHotel("Delhi");
        Hotel hotel1Delhi = new Hotel("The Oberoi");
        hotel1Delhi.addRoomCategory("Luxury", 10000);
        Hotel hotel2Delhi = new Hotel("Taj Palace");
        hotel2Delhi.addRoomCategory("Deluxe", 8000);
        delhiHotel.addHotel(hotel1Delhi);
        delhiHotel.addHotel(hotel2Delhi);

        foodData.put("delhi", delhiFood);
		foodData.put("mumbai", mumbaiFood);
		foodData.put("tamil nadu", tamilNaduFood);
		foodData.put("up", uttarPradeshFood);
		foodData.put("west bengal", westBengalFood);
		foodData.put("punjab", punjabFood);
		foodData.put("rajasthan", rajasthanFood);
		foodData.put("gujarat", gujaratFood);
		
        hotelData.put("delhi", delhiHotel);
    }
	
	
	

    private static JPanel createRouteFinderPanel(Graph graph) {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
		
		
		
        JTextField sourceField = new JTextField();
        JTextField destinationField = new JTextField();
        JComboBox<String> transportModeBox = new JComboBox<>(new String[]{"Road", "Train", "Plane", "Bus"});
        JButton findRoutesButton = new JButton("Find Routes");
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);

        findRoutesButton.addActionListener(e -> {
            String source = sourceField.getText().trim();
            String destination = destinationField.getText().trim();
            String mode = (String) transportModeBox.getSelectedItem();

            if (source.isEmpty() || destination.isEmpty()) {
                resultArea.setText("Source and destination must not be empty.");
                return;
            }

            List<String> routes = graph.findRoutes(source, destination, mode);
            resultArea.setText(String.join("\n", routes));
        });

        inputPanel.add(new JLabel("Source:"));
        inputPanel.add(sourceField);
        inputPanel.add(new JLabel("Destination:"));
        inputPanel.add(destinationField);
        inputPanel.add(new JLabel("Transport Mode:"));
        inputPanel.add(transportModeBox);
        inputPanel.add(new JLabel());
        inputPanel.add(findRoutesButton);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        return panel;
    }

    private static JPanel createBudgetPlannerPanel() {
   		
		JPanel panel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
		
		
        JComboBox<String> destinationType = new JComboBox<>(new String[]{"India", "Foreign"});
        JTextField budgetField = new JTextField();
        JButton calculateButton = new JButton("Plan & Suggest");
        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);

        calculateButton.addActionListener(e -> {
            try {
                String type = (String) destinationType.getSelectedItem();
                double budget = Double.parseDouble(budgetField.getText());

                double food = budget * 0.30;
                double travel = budget * 0.40;
                double shopping = budget * 0.20;
                double misc = budget * 0.10;

                StringBuilder breakdown = new StringBuilder();
                breakdown.append(String.format("Budget Breakdown (Total: Rs. %.2f):\n", budget));
                breakdown.append(String.format("  Food: Rs. %.2f\n", food));
                breakdown.append(String.format("  Travel: Rs. %.2f\n", travel));
                breakdown.append(String.format("  Shopping: Rs. %.2f\n", shopping));
                breakdown.append(String.format("  Miscellaneous: Rs. %.2f\n\n", misc));

                String suggestions = "";

                if (type.equals("India")) {
                    if (budget < 10000) {
                        suggestions = "Budget-friendly Indian trips:\n- Rishikesh\n- Jaipur\n- Varanasi";
                    } else if (budget < 50000) {
                        suggestions = "Mid-range Indian trips:\n- Goa\n- Kerala\n- Himachal Pradesh";
                    } else {
                        suggestions = "Luxury Indian trips:\n- Andaman\n- Ladakh\n- Kashmir";
                    }
                } else {
                    if (budget < 50000) {
                        suggestions = "Budget-friendly foreign trips:\n- Nepal\n- Sri Lanka\n- Bhutan";
                    } else if (budget < 200000) {
                        suggestions = "Mid-range foreign trips:\n- Southeast Asia\n- Middle East\n- Eastern Europe";
                    } else {
                        suggestions = "Luxury foreign trips:\n- Western Europe\n- Australia\n- USA";
                    }
                }

                outputArea.setText(breakdown + suggestions);
            } catch (NumberFormatException ex) {
                outputArea.setText("Invalid budget amount. Please enter a numeric value.");
            }
        });


		inputPanel.add(new JLabel("Destination Type:"));
		inputPanel.add(destinationType);
		inputPanel.add(new JLabel("Budget (?):"));
		inputPanel.add(budgetField);
		inputPanel.add(new JLabel());
		inputPanel.add(calculateButton);
		inputPanel.add(new JScrollPane(outputArea));


		panel.add(inputPanel, BorderLayout.NORTH);
		panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        return panel;
    }

    private static JPanel createFoodAndHotelExplorerPanel(Map<String, DestinationFood> foodData, Map<String, DestinationHotel> hotelData) {
        JPanel panel = new JPanel(new BorderLayout());

        JTextField destinationInput = new JTextField(20);
        JTextArea foodArea = new JTextArea(10, 30);
        JTextArea hotelArea = new JTextArea(10, 30);
        foodArea.setEditable(false);
        hotelArea.setEditable(false);

        JButton foodButton = new JButton("Show Food Details");
        JButton hotelButton = new JButton("Show Hotel Details");

        foodButton.addActionListener(e -> {
            String destination = destinationInput.getText().trim().toLowerCase();
            if (foodData.containsKey(destination)) {
                foodArea.setText(foodData.get(destination).getFoodDetails());
            } else {
                foodArea.setText("No food information available for this destination.");
            }
        });

        hotelButton.addActionListener(e -> {
            String destination = destinationInput.getText().trim().toLowerCase();
            if (hotelData.containsKey(destination)) {
                hotelArea.setText(hotelData.get(destination).getHotelDetails());
            } else {
                hotelArea.setText("No hotel information available for this destination.");
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Enter Destination:"));
        inputPanel.add(destinationInput);
        inputPanel.add(foodButton);
        inputPanel.add(hotelButton);

        panel.add(inputPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(foodArea), new JScrollPane(hotelArea));
        splitPane.setDividerLocation(400);

        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }
}

