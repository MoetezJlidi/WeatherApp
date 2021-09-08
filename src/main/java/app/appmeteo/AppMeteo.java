package app.appmeteo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class AppMeteo extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.getIcons().add(new Image("http://aya.io/blog/images/weather.png"));
        Parent root = FXMLLoader.load(getClass().getResource("/app/appmeteo/view/appmeteo.fxml"));
        primaryStage.setTitle("Meteo GROUPE S");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }
}
