package app.appmeteo.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Scanner;

public class AppMeteoController implements Initializable {

    @FXML private Button plusB;
    @FXML private Button mB;
    @FXML private VBox stack;
    @FXML private TextArea favlist;
    @FXML private TextField txt_ville;
    @FXML private Label mdeg;
    @FXML private Label curdeg;
    @FXML private Label adeg;
    @FXML private Label txt_nom_ville;
    @FXML private DatePicker dateP;
    @FXML private Label datetxt;
    @FXML private ImageView imgM;
    @FXML private  Label countfav;
    private Date min;
    private int count;//Global variable to track our fav list items count
    private Date maxdate;
    @FXML private Button addB , delB;
    private static JSONArray days;
    private static JSONArray hours;
    private String fav = "";
    //    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) { // Setting the limit to 7 days max for the calendar.(Starting from the current day)
        LocalDate min = LocalDate.now();// locating the current date
        Calendar c = Calendar.getInstance();//
        Date mindate = Date.from(min.atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.min = mindate;
        c.setTime(mindate);
        c.add(Calendar.DATE, 7); // setting the maximum to 7 days.
    this.maxdate = c.getTime();
    dateP.setDayCellFactory(d -> new DateCell() {// disabling the other days of the calendar (before current date & after 7 days from the current date)
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    setDisable(item.isAfter(min.plusDays(7)) || item.isBefore(min));
                }
            }
    );
         count = 0;
        try { // Loading the data from the .txt file
            File myObj = new File(System.getProperty("user.dir") + "\\src\\main\\java\\app\\appmeteo\\controller\\fav.txt");
            Scanner myReader = new Scanner(myObj);

            while (myReader.hasNextLine()) {
                // for each city in our favourite list we will get it's data from the API and we add 2 labes and an img view to our VBOX(type : stack)
                String data = myReader.nextLine();
                if (!data.equals("")) {// if "ville" is not blank
                    count++;
                }
                JSONObject js = new JSONObject(getSimpleData(data));
                //
                VBox s = new VBox();
                Label nom = new Label(data+" : ");
                Label deg = new Label(js.getJSONObject("main").getInt("temp") +" \u00B0"+"C");
                nom.setFont(Font.font(12));
                String url = "http://openweathermap.org/img/wn/"+js.getJSONArray("weather").getJSONObject(0).getString("icon")+".png";
                Image img = new Image(url);
                ImageView v = new ImageView(img);
                HBox hbox = new HBox();
                hbox.setSpacing(25);
                hbox.getChildren().addAll(nom,deg);
                s.getChildren().add(hbox);
                s.getChildren().add(v);
                s.getChildren().add(new Label("-------------------------------"));
                stack.getChildren().add( s) ;
                fav = fav + data + "\n";
                favlist.setText(favlist.getText() + data + "\n");
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(count == 6){
            addB.setDisable(true);
        }
        else if (count ==0){
            plusB.setDisable(true);
        }
        updateCount();
    }
    @FXML
    private  void ShowCourbe(){
        Stage stage = new Stage();
        stage.setTitle("Courbe de temperature");
        //defining the axes
        final CategoryAxis xAxis = new CategoryAxis();// x axis
        xAxis.setLabel("Nombre de jours");
        final NumberAxis yAxis = new NumberAxis();//y axis
        yAxis.setLabel("Temperature");

        //creating the chart
        final LineChart<String,Number> lineChart =
                new LineChart<String,Number>(xAxis,yAxis);// creating the x and y axis

        lineChart.setTitle("Courbe de temperature/jour");// title of the chart
        //defining a series
        XYChart.Series temp = new XYChart.Series();
        temp.setName("Courbe de temperature");
        //populating the series with data
        int i = 1;//nombre of days stating by 1
        for(Object jo : this.days){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d1 = new Date(((JSONObject) jo).getLong("dt")*1000);

            String date = sdf.format(d1);// looping in our days list and from each day we get the temperature and add a new point to the chart
            temp.getData().add(new XYChart.Data(date, ((JSONObject) jo).getJSONObject("temp").getInt("day"))) ;

            i++;//incrementing the days until we break out of the loop
        }

        Scene scene  = new Scene(lineChart,800,600);
        lineChart.getData().add(temp);// linking the points to each other
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    private void ShowCourbe1(){
        Stage stage = new Stage();
        stage.setTitle("Courbe de precipitation");
        //defining the axes
        final CategoryAxis xAxis= new CategoryAxis();// x axis
        xAxis.setLabel("Nombre de jours");
        final NumberAxis yAxis = new NumberAxis();//y axis
        yAxis.setLabel("Precipitations");

        //creating the chart
        final LineChart<String,Number> lineChart =
                new LineChart<String,Number>(xAxis,yAxis);

        lineChart.setTitle("Courbe de precipitations/jour");// title of the chart
        //defining a series
        XYChart.Series precep = new XYChart.Series();
        precep.setName("Courbe de temperature");
        //populating the series with data
        int i = 1;//nombre of days stating by 1
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");



        for(Object jo : this.days){
            Date d1 = new Date(((JSONObject) jo).getLong("dt")*1000);
            String date = sdf.format(d1);// looping in our days list and from each day we get the temperature and add a new point to the chart
            try{
                precep.getData().add(new XYChart.Data<>(date,((JSONObject) jo).getFloat("rain")) );
            }
            catch(Exception e){
                precep.getData().add(new XYChart.Data<>(date,0) );
            }

            i++;//incrementing the days until we break out of the loop
        }

        Scene scene  = new Scene(lineChart,800,600);
        lineChart.getData().add(precep);// linking the points to each other
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    private void AddVille() throws IOException {
        if (count < 6) {//if we still didn't reach the max then we can add one more item to the list


            String selected = favlist.selectedTextProperty().getValue();
            if (selected.split("\n").length + count <=6) {//if the number of selected items to delete is less or equals to 6 then we can add the amount
                String[] selecvilles = selected.split("\n");
                for (int i = 0; i < selecvilles.length; i++) {// for each item in our selected list we will create a 2 labels and image view and add it to the stack(vbox)
                    JSONObject js = new JSONObject(getSimpleData(selecvilles[i]));
                    //
                    VBox s = new VBox();
                    Label nom = new Label(selecvilles[i] + " : ");
                    Label deg = new Label(js.getJSONObject("main").getInt("temp") + " \u00B0" + "C");
                    nom.setFont(Font.font(12));
                    String url = "http://openweathermap.org/img/wn/" + js.getJSONArray("weather").getJSONObject(0).getString("icon") + ".png";
                    Image img = new Image(url);
                    ImageView v = new ImageView(img);
                    HBox hbox = new HBox();
                    hbox.setSpacing(25);
                    hbox.getChildren().addAll(nom, deg);
                    s.getChildren().add(hbox);
                    s.getChildren().add(v);
                    s.getChildren().add(new Label("-------------------------------"));
                    stack.getChildren().add(s);
                }
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Meteo");
                alert.setHeaderText("Ajouter une/des ville(s):"); // add a city to the favourite list
                alert.setContentText(selected.replace("\n", ",") + " ajoutee(s)");
                //Right tick icon
                ImageView icon = new ImageView("https://ownacademy.co/wp-content/uploads/2018/10/Tick_Mark_Dark-512.png");// Icon of the "added" city
                icon.setFitHeight(48);
                icon.setFitWidth(48);
                alert.getDialogPane().setGraphic(icon);
                //App Icon
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();//creation of an alert box
                stage.getIcons().add(new Image("http://aya.io/blog/images/weather.png"));
                alert.show();
                BufferedWriter writer = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "\\src\\main\\java\\app\\appmeteo\\controller\\fav.txt"));
                writer.write(fav + selected);// update of the favorite list(fav.txt)
                fav = fav + selected; // updating the new favorite list internally (inside the )
                count += selected.split("\n").length;
                updateCount();
                writer.close();
                delB.setDisable(false);
                if (count == 6) {
                    addB.setDisable(true);
                }
            }
        }

    }
    @FXML private Button prevB;
    @FXML
    private void ShowHourly(){
        try {
            TilePane list = new TilePane();// permits to go back to line when you reach the maximum displays in one line
            list.setVgap(15);// vertical gap
            list.setHgap(50);//horizontal gap
            list.setPadding(new Insets(15, 50, 50, 50));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");// date object to string object at the format of yyyy-MM-dd HH:mm
            for (int count = 0; count < 24; count++){// for each hour in the 48 hours of the API
                JSONObject js = this.hours.getJSONObject(count);
                VBox node = new VBox();
                HBox row = new HBox();
                Date d1 = new Date(js.getLong("dt")*1000);// timestamp to date object
                String date1  =  sdf.format(d1);
                String icon = js.getJSONArray("weather").getJSONObject(0).getString("icon");
                ImageView img = new ImageView(new Image("http://openweathermap.org/img/wn/"+icon+".png"));
                Label labeld = new Label(date1);
                labeld.setFont(new Font(20));
                node.getChildren().add(labeld);
                row.getChildren().add(img);// adding the icon to the Hbox
                int temp = js.getInt("temp");
                Label label =new Label(Integer.toString(temp)+ " \u00B0"+"C");
                label.setFont(new Font(30));
                row.getChildren().add(label);//adding the temperature to the Hbox next to the icon
                node.getChildren().add(row);// adding the hbox under the text label
                list.getChildren().add(node);// adding a new node to the pane so we can start over the procedure
            }
            list.setStyle("-fx-background-color:LightBlue");
            Group root = new Group(list);
            Scene scene = new Scene(root, 1050, 500);
            Stage stage = new Stage();
            stage.setTitle("Previsions heure par heure");
            stage.setScene(scene);
            stage.getIcons().add(new Image("http://aya.io/blog/images/weather.png"));
            stage.show();
        } catch (Exception e) { }
}

    private void updateCount(){//a function to update the countfav label
        countfav.setText(count+"/6");
    }
    @FXML
    private void RemoveVille() throws IOException {
        if(count >0) {// if we got at least one element in our list then we can delete
            String selected = favlist.selectedTextProperty().getValue();
            String [] selecvilles =  selected.split("\n");
            String []  villes = favlist.getText().split("\n");
            int remcount = 0;//to keep track on how many elements we deleted from our stack
            for (int i =0;i< selecvilles.length;i++){
                fav = fav.replace(selecvilles[i] , ""); //replace a deleted city by an empty string.
                for(int j=0;j< villes.length;j++){
                    if(selecvilles[i].equals(villes[j])){//if cities match then
                        stack.getChildren().remove(j-remcount);// we remove it from the stack by it's index
                        remcount++;
                    }
                }
            }
            System.out.println(selected.split("\n").length);
            count =count - selected.split("\n").length;//updating the count variable

            favlist.setText(fav);//updating the fav variable
            BufferedWriter writer = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "\\src\\main\\java\\app\\appmeteo\\controller\\fav.txt"));
            writer.write(fav);//updating our fav file

            writer.close();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Meteo");
            alert.setHeaderText("Effacer une/des ville(s)");//delete a city from our favourite list
            alert.setContentText(selected.replace("\n", ",") + " effacee(s)");
            //deleted icon
            ImageView icon = new ImageView("https://icon-library.com/images/deleted-icon/deleted-icon-18.jpg");
            icon.setFitHeight(48);
            icon.setFitWidth(48);
            alert.getDialogPane().setGraphic(icon);
            //App Icon
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("http://aya.io/blog/images/weather.png"));
            alert.show();
            updateCount();
            addB.setDisable(false);
            if (count == 0){
                delB.setDisable(true);
            }
        }
    }
    @FXML
    private void AddDate() throws ParseException {// Adding a day in the date label
        if (mB.isDisabled()){
            mB.setDisable(false);
        }
    String date = datetxt.getText();
    Date d = new SimpleDateFormat("yyyy-MM-dd").parse(date);
    if (d.equals(new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(maxdate)))){ // if you reach the maximum date the ">" button will be disabled
            plusB.setDisable(true);
        }
    else {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DATE, 1);// adding one day to the date when pressing the ">" button .
        d = c.getTime();
        if (d.equals(new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(maxdate)))){
            plusB.setDisable(true);
        }
        datetxt.setText(new SimpleDateFormat("yyyy-MM-dd").format(d));// updating the label value to the new date.
        updateWeather(datetxt.getText());// updating the weather after clicking on the buttons
    }


    }
    @FXML
    private void MinusDate() throws ParseException {// removing a day from the date label
        if (plusB.isDisabled()){
            plusB.setDisable(false);
        }
        String date = datetxt.getText();
        Date d = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        if (d.equals(new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(min)))){// if you reach the minimum date the "<" button will be disabled
            mB.setDisable(true);
        }
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DATE, -1);
        d = c.getTime();
        if (d.equals(new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(min)))){
            mB.setDisable(true);
        }
        datetxt.setText(new SimpleDateFormat("yyyy-MM-dd").format(d));
updateWeather(datetxt.getText());

    }
    public static String getSimpleData(String ville) throws IOException {
        URL obj = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + ville + "&appid=b1e3aa7c6915e2bf7366d5fd4a280b7c&units=metric");// to parse the url format
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();// to establish a connection with the specified URL
        con.setRequestMethod("GET");// Set the request method to GET
        con.setRequestProperty("User-Agent", "Mozilla/5.");// Defining the User agent
        BufferedReader in = new BufferedReader(new InputStreamReader(  //Executing the request
                con.getInputStream()));// Reading the request response
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();

    }
    @FXML
    private void Showdata() throws IOException, ParseException
    {
        plusB.setDisable(false);
        mB.setDisable(true);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateval = dateP.getValue().toString();
            Date d = sdf.parse(dateval);// parsing the date in this format : yyyy-MM-dd
            long s = d.getTime()/1000; // date in s
            System.out.println(s);

        String ville = txt_ville.getText();
        String data = getData(ville);
        txt_nom_ville.setText(ville);
            datetxt.setText(dateval);
            updateWeather(dateval);
        String date = datetxt.getText();
        Date dt = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        if (dt.equals(new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(min)))){// if you reach the minimum date the "<" button will be disabled
            mB.setDisable(true);
        }
        else{
            mB.setDisable(false);
        }
        if (dt.equals(new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(maxdate)))){
            plusB.setDisable(true);
        }
        else{
            plusB.setDisable(false);
        }

    }

    private void updateWeather(String date) throws ParseException {// updating the weather to the specified date.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // selecting the date format
        Date d  = sdf.parse(date);// parsing the date
        JSONObject data = new JSONObject() ;
        for(Object j : this.days){// looping through the days/weather data
            Date d1 = new Date(((JSONObject) j).getLong("dt")*1000);
            String date1  =  sdf.format(d1);
            System.out.println(date + " "+ date1);
            if (date.equals(date1)){//if we find our date in the data then we save it inside a variable data and break out from the loop.
                data = ((JSONObject) j);
                break;
            }
        }
        //updating the scene elements with the new values .
        System.out.println(this.days.toString());
        System.out.println(data.toString());

        curdeg.setText(data.getJSONObject("temp").getInt("max") + " \u00B0"+"C");
        mdeg.setText(data.getJSONObject("temp").getInt("morn") + " \u00B0"+"C");
        adeg.setText(data.getJSONObject("temp").getInt("eve") + " \u00B0"+"C");
        System.out.println(data.toString());
        String icon = data.getJSONArray("weather").getJSONObject(0).getString("icon");
        SetIcon(icon);
//
    }
    private void SetIcon(String iconid){// icon of the weather
        String url = "http://openweathermap.org/img/wn/"+iconid+".png";
        Image img = new Image(url);
        imgM.setImage(img);

    }
    public static String getData(String ville) throws IOException {
        JSONObject cords = getCoords(ville);
        URL obj = new URL("https://api.openweathermap.org/data/2.5/onecall?lat="+cords.getLong("lat")+"&lon="+cords.getLong("lon")+"&exclude=minutely&units=metric&appid=b1e3aa7c6915e2bf7366d5fd4a280b7c");// to parse the url format
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();// to establish a connection with the specified URL
        con.setRequestMethod("GET");// Set the request method to GET
        con.setRequestProperty("User-Agent", "Mozilla/5.");// Defining the User agent
        BufferedReader in = new BufferedReader(new InputStreamReader(  //Executing the request
                con.getInputStream()));// Reading the request response
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
    days = new JSONObject(response.toString()).getJSONArray("daily");
    hours = new JSONObject(response.toString()).getJSONArray("hourly");
        // print result
        return response.toString(); //returning the request response in a string format.
    }
    public static JSONObject getCoords(String ville) throws IOException {
        URL obj = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + ville + "&appid=b1e3aa7c6915e2bf7366d5fd4a280b7c&units=metric");// to parse the url format
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();// to establish a connection with the specified URL
        con.setRequestMethod("GET");// Set the request method to GET
        con.setRequestProperty("User-Agent", "Mozilla/5.");// Defining the User agent
        BufferedReader in = new BufferedReader(new InputStreamReader(  //Executing the request
                con.getInputStream()));// Reading the request response
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        //System.out.println(response.toString());
        JSONObject jobj = new JSONObject(response.toString());
        //System.out.println(jobj.toString());
        return jobj.getJSONObject("coord");
    }
    public static String readFromFile(String path){ // reading a file from a specific path.
        StringBuilder result = new StringBuilder();
        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                result.append(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return result.toString();
    }

}
