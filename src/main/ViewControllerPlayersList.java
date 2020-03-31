package main;

import DataModel.DatabaseLocal;
import DataModel.WinTittles;
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import controller.DatabaseManager;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class ViewControllerPlayersList implements Initializable {
    public TextField TF_players_search;
    public Pane PANE_players_list;
    ListView<String> playersList = new ListView<String>();
    ObservableList<String> itemsPlayersList = FXCollections.observableArrayList();
    ArrayList<Long>id_account_player_list = new ArrayList<Long>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            getAllPlayersList();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        playersList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                ContextMenu contextMenu = new ContextMenu();
                MenuItem menuItem1 = new MenuItem("Statystyki gracza");
                MenuItem menuItem2 = new MenuItem("Usuń");
                contextMenu.getItems().add(menuItem1);
                contextMenu.getItems().add(menuItem2);
                int index = playersList.getSelectionModel().getSelectedIndex();
                playersList.setContextMenu(contextMenu);

                menuItem2.setOnAction(e -> {
                    try {
                        deletePlayerFromWatchList (id_account_player_list.get(index));
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });
            }
        });
    }

    private void getAllPlayersList() throws SQLException {

        String query = "SELECT " + DatabaseLocal.tabels.players + ".id_account, " + DatabaseLocal.tabels.players + ".name, " + DatabaseLocal.tabels.players + ".server, " +
                "" + DatabaseLocal.tabels.init_players + ".clan_tag FROM " + DatabaseLocal.tabels.players +
                " INNER JOIN " + DatabaseLocal.tabels.init_players + " ON " + DatabaseLocal.tabels.players + ".id_account = " + DatabaseLocal.tabels.init_players + ".id_account" +
                " ORDER BY name DESC";
        selectAllPlayers(query);
    }

    private void getAllPlayersList(String query) throws SQLException {
        selectAllPlayers(query);
    }

    private void selectAllPlayers (String query) throws SQLException {
        itemsPlayersList.clear();
        id_account_player_list.clear();
        PANE_players_list.getChildren().clear();
        DatabaseManager databaseManager = new DatabaseManager();
        ResultSet resultSet = databaseManager.select(query);
        int counter = 1;
        while (resultSet.next()) {
            String clanTag = (resultSet.getString("clan_tag").equals("")) ? "" : " [" + resultSet.getString("clan_tag") + "] /";
            itemsPlayersList.add(counter + ". " + resultSet.getString("name") + clanTag + " [" + resultSet.getString("server") + "]");
            id_account_player_list.add(resultSet.getLong("id_account"));
            counter++;
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

    public void searchPlayer(KeyEvent keyEvent) throws SQLException {
        String searchText = TF_players_search.getText();

        if (searchText.isEmpty()) {
            getAllPlayersList();
            return;
        }

        String query = "SELECT " + DatabaseLocal.tabels.players + ".id_account, " + DatabaseLocal.tabels.players + ".name, " + DatabaseLocal.tabels.players + ".server, " +
                "" + DatabaseLocal.tabels.init_players + ".clan_tag FROM " + DatabaseLocal.tabels.players +
                " INNER JOIN " + DatabaseLocal.tabels.init_players + " ON " + DatabaseLocal.tabels.players + ".id_account = " + DatabaseLocal.tabels.init_players + ".id_account" +
                " WHERE name LIKE '%" + searchText + "%'  ORDER BY name DESC";
        selectAllPlayers(query);
    }

    private void deletePlayerFromWatchList (long id_account) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuń z listy obserwowanych");
        alert.setHeaderText(null);
        alert.setContentText("Czy chcesz usunąć tego gracza z listy obserwowanych?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            DatabaseManager databaseManager = new DatabaseManager();
            String query = "DELETE FROM " + DatabaseLocal.tabels.players + " WHERE  id_account=" + id_account;
            databaseManager.delete(query);
            query = "DELETE FROM " + DatabaseLocal.tabels.init_players + " WHERE  id_account=" + id_account;
            databaseManager.delete(query);
            databaseManager.closeConnection();

            getAllPlayersList();
        } else {
            return;
        }

    }
}
