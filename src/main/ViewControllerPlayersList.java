package main;

import DataModel.DatabaseLocal;
import DataModel.WinTittles;
import controller.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ViewControllerPlayersList implements Initializable {
    public TextField TF_players_search;
    public Pane PANE_players_list;
    ListView<String> playersList = new ListView<String>();
    ObservableList<String> itemsPlayersList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            getAllPlayersList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getAllPlayersList() throws SQLException {
        DatabaseManager databaseManager = new DatabaseManager();
        String query = "SELECT " + DatabaseLocal.tabels.players + ".id_account, " + DatabaseLocal.tabels.players +
                ".name, " + DatabaseLocal.tabels.players + ".server, " + DatabaseLocal.tabels.init_players + ".clan_tag FROM " + DatabaseLocal.tabels.players +
                " INNER JOIN " + DatabaseLocal.tabels.init_players + " ON " + DatabaseLocal.tabels.players + ".id_account = " + DatabaseLocal.tabels.init_players + ".id_account" +
                " ORDER BY " + DatabaseLocal.tabels.players + ".name";
        ResultSet resultSet = databaseManager.select(query);
        int counter = 1;
        while (resultSet.next()) {
            String clan_tag_ = (resultSet.getString("clan_tag").equals("")) ? " " : " [" + resultSet.getString("clan_tag") + "]";
            itemsPlayersList.add(counter + ". " +  resultSet.getString("name") + clan_tag_);
            counter++;
        }
        resultSet.close();
        databaseManager.closeConnection();
        playersList.setItems(itemsPlayersList);
        playersList.setPrefWidth(380);
        playersList.setPrefHeight(480);
        PANE_players_list.getChildren().add(playersList);
    }

    private void getAllPlayersList(String query) throws SQLException {
        DatabaseManager databaseManager = new DatabaseManager();
        query = "SELECT name FROM " + DatabaseLocal.tabels.players;
        ResultSet resultSet = databaseManager.select(query);
        while (resultSet.next()) {
            itemsPlayersList.add(resultSet.getString("name"));
        }
        resultSet.close();
        databaseManager.closeConnection();
        playersList.setItems(itemsPlayersList);
        playersList.setPrefWidth(380);
        playersList.setPrefHeight(480);
        PANE_players_list.getChildren().add(playersList);
    }

    public void showView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("vc_players_list.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root, 400, 600));
        stage.setTitle(WinTittles.PlayersList);
        stage.show();
    }
}
