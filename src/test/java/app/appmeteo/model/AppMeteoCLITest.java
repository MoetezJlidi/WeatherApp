package app.appmeteo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import app.appmeteo.AppMeteoCLI;

import java.io.FileNotFoundException;
import app.appmeteo.controller.AppMeteoController;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;

public class AppMeteoCLITest {

    private static final String path = System.getProperty("user.dir") + "\\src\\test\\java\\app\\appmeteo\\model\\";

    @Test
    public void testGetFavoriteList() throws FileNotFoundException {
        LinkedList<String> expected = new LinkedList<>();
        expected.add("Monaco");
        expected.add("Lisbonne");
        expected.add("New York");
        assertEquals(expected, AppMeteoCLI.getFavoriteList(path + "favTestGetFavorite.txt"));
    }

    @Test
    public void testRemoveFavorite() throws IOException {
        LinkedList<String> expected = new LinkedList<>();
        expected.add("Monaco");
        expected.add("Lisbonne");
        expected.add("New York");
        AppMeteoCLI.removeFavorite("Marseille", AppMeteoCLI.getFavoriteList(path + "favTestRemoveFavorite.txt"), path + "favTestRemoveFavorite.txt");
        assertEquals(expected, AppMeteoCLI.getFavoriteList(path + "favTestRemoveFavorite.txt"));

        AppMeteoCLI.addFavorite("Marseille", AppMeteoCLI.getFavoriteList(path + "favTestRemoveFavorite.txt"), path + "favTestRemoveFavorite.txt");
    }

    @Test
    public void testAddFavorite() throws IOException {
        LinkedList<String> expected = new LinkedList<>();
        expected.add("Paris");
        expected.add("Marseille");
        AppMeteoCLI.addFavorite("Marseille", AppMeteoCLI.getFavoriteList(path + "favTestAddFavorite.txt"), path + "favTestAddFavorite.txt");
        assertEquals(expected, AppMeteoCLI.getFavoriteList(path + "favTestAddFavorite.txt"));

        AppMeteoCLI.removeFavorite("Marseille", AppMeteoCLI.getFavoriteList(path + "favTestAddFavorite.txt"), path + "favTestAddFavorite.txt");
    }
}
