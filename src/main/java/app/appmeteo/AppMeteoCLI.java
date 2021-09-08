package app.appmeteo;

import app.appmeteo.controller.AppMeteoController;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;


public class AppMeteoCLI {

    private static final Scanner scanner = new Scanner(System.in);
    private static final String path = System.getProperty("user.dir") + "\\src\\main\\java\\app\\appmeteo\\controller\\fav.txt";

    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to the weather app\n");
        printFavoriteWeather(path);
        while (true) {
            System.out.print("\nInput your command please (tape 'help' for see all commands) : ");
            String command = scanner.nextLine();
            if(command.equals("end")) break; // if command = "end", close the app
            lookMainCommand(command);
        }
        scanner.close();
    }

    public static void lookMainCommand(String command) throws IOException {
        switch (command) {
            case "help":
                printCommandHelp(); // if command = "help", display all possible commands
                break;
            case "weather": System.out.print("\n");
                printWeather(); // if command = "weather", permits to enter a city
                break;
            case "favorite": System.out.print("\n");
                favorite(path); // if command = "favorite", permits the management of the favorite cities
                break;
            default:
                System.out.println("Command not found"); // else, display 'Command not found'
                break;
        }
    }

    public static JSONObject getWeather(String city, String date) throws IOException { // if command = 'weather' , permits the user to put a city and a date so it could display the weather afterwards
        try {
            //JSONObject coords = AppMeteoController.getCoords(city); // recover the coordinates of the city
            JSONObject data = new JSONObject(AppMeteoController.getData(city));
            data = getDayData(date, data); // recover the data of the day that interest us
            return data;
        } catch(FileNotFoundException e) {
            System.out.println("this city doesn't exist");
        }
        return null;
    }

    private static void printWeather() throws IOException {
        String date, city; JSONObject data;
        do {
            System.out.print("Input your city : "); city = scanner.nextLine();
            System.out.print("Input a date (dd/MM/yyyy) : "); date = scanner.nextLine();
            data = getWeather(city, date);
        } while (data == null);
        System.out.println("\nOn " + date + " in " + city + ", it's will be :");
        System.out.println(Math.round(data.getJSONObject("temp").getFloat("morn")) + " °C in the morning.\n"    // display the demanded data
                         + Math.round(data.getJSONObject("temp").getFloat("day")) + " °C in the afternoon.\n"
                         + Math.round(data.getJSONObject("temp").getFloat("eve")) + " °C in the evening.\n"
                         + "It will have " + data.getJSONArray("weather").getJSONObject(0).get("description") + ".");
    }

    public static void printFavoriteWeather(String path) throws IOException {  // display the actual weather of the cities in our favorite list
        LinkedList<String> cityList = getFavoriteList(path);  // recover the favorite list
        for (String favoriteCity : cityList) {
            JSONObject currentData = new JSONObject(AppMeteoController.getSimpleData(favoriteCity));
            System.out.println("In " + favoriteCity + ", it's "
                             + currentData.getJSONObject("main").getInt("temp")
                             + " °C in average with " + currentData.getJSONArray("weather").getJSONObject(0).getString("description"));
        }
        System.out.print("\n");
    }

    public static void favorite(String path) throws IOException { // permits the management of the favorite list with typing 'add' to add a city or 'remove' to remove one
        while(true) {
            LinkedList<String> cityList = getFavoriteList(path);
            for (String city : cityList) System.out.println(city);
            System.out.println("\nDo you want add or remove a city from your favorite list ? (tape 'help' for see all commands)");
            String command = scanner.nextLine();
            if (command.equals("done")) break;  // 'done' permits the user to quit the management of the favorite list
            lookFavoriteCommand(command, cityList);
        }
    }

    private static void lookFavoriteCommand(String command, LinkedList<String> cityList) {
        try {
            String city = command.substring(command.indexOf(" ") + 1); // tries to find the city otherwise display 'Command not found'
            if (command.equals("help")) printCommandHelp();
            else if (command.equals("add " + city))
                if (cityList.size() < 6) addFavorite(city, cityList, path); // add a city only  if there is less than 6 cities
                else System.out.println("You already have six cities in your list of favorite");
            else if (command.equals("remove " + city)) removeFavorite(city, cityList, path);
            else System.out.println("Command not found\n");
        } catch (Exception e) {
            System.out.println("Command not found\n");
        }
    }

    public static void addFavorite(String city, LinkedList<String> cityList, String path) throws IOException {  // add a city the favorite list
        if(!cityList.contains(city)){
            cityList.add(city);
            Path file = Paths.get(path);
            Files.write(file, cityList);
        }
    }

    public static void removeFavorite(String city, LinkedList<String> cityList, String path) throws IOException {  // take off a city of the favorite list
        Path file = Paths.get(path);
        if(cityList.contains(city)) {
            cityList.remove(city);
            Files.write(file, cityList); // Replace with the new list
        }
        else System.out.println("Your list of favorites doesn't contain this city\n");
    }

    public static void printCommandHelp() {  // display the available commands
        System.out.println("'weather' : returns the weather for a given city and date (in the next seven days)\n"
                         + "'favorite' : add or remove a city from your favorite list with these commands :\n"
                         + "    'add CITY' : add CITY in your favorite list\n"
                         + "    'remove CITY' : remove CITY from your favorite list\n"
                         + "    'done' : close favorite section\n"
                         + "'end' : close the application");
    }

    public static JSONObject getDayData(String stringDate, JSONObject cityData) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // selecting the date format
        JSONArray daysArray = cityData.getJSONArray("daily");
        for(Object j : daysArray) {  // looping through the days/weather data
            Date date = new Date(((JSONObject) j).getLong("dt") * 1000);
            String StringDate = sdf.format(date);
            if (stringDate.equals(StringDate)) {   //if we find our date in the data then we save it inside a variable data and break out from the loop.
                cityData = ((JSONObject) j);
                break;
            }
        }
        return cityData;
    }

    public static LinkedList<String> getFavoriteList(String path) throws FileNotFoundException {  // return the favorite list
        BufferedReader favoriteList = new BufferedReader(new FileReader(path));
        Scanner myFavoriteList = new Scanner(favoriteList);
        LinkedList<String> cityList = new LinkedList<>();
        while (myFavoriteList.hasNextLine()) {
            String city = myFavoriteList.nextLine();
            if (!city.equals("")) {// if "ville" is not blank
                cityList.add(city);
            }
        }
        myFavoriteList.close();
        return cityList;
    }

}

