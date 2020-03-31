package main;

import DataModel.*;
import ModelViews.Alerts;
import controller.DatabaseManager;
import controller.NetworkManager;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import model.NetStatusObservable;
import model.NetworkObserver;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Controller implements Initializable, NetworkObserver {

    public Label LBL_netStatusInfo;
    public Circle ICOconnectStatus;
    public ChoiceBox<String> CHB_selectServer;
    public TextField TF_search;
    public Pane progressLoaderBox;
    public Pane PANE_table_playerListBox;
    public Label LBLaccountID;
    public Label LBL_playerName;
    public Pane PANE_playerBaseProperty;
    public Label LBL_battle_all;
    public Label LBL_battle_win;
    public Label LBL_battle_draw;
    public Label LBL_battle_losses;
    public Label LBL_clan_tag;
    public ImageView IMG_clanAvatar;
    public Group GR_clanPane;
    public Group GR_bootom_loader;
    public Pane PANE_bottom_loader;
    public Label LBL_id_clan;
    public Label LBL_serverTag;
    public ImageView IMG_eye;
    public Button BTN_add_to_followed;
    public Label LBL_add_delete_player_text;
    public Label LBL_battle_avg_xp;
    public Label LBL_frags;
    public Label LBL_avg_damage_assisted;
    public Label LBL_hits_percents;
    public Label LBL_spotted;
    public Label LBL_dropped_capture_points;
    public Label LBL_capture_points;
    public Label LBL_damage_dealt;
    public Label LBL_damage_received;
    public Label LBL_survived_battles;
    public Label LBL_xp;
    NetStatusObservable netStatusObservable = null;
    private INTERNET_ACCESS internet_access = INTERNET_ACCESS.INIT;
    private NetworkManager networkManager = new NetworkManager();
    public ProgressIndicator progressIndicator = new ProgressIndicator();
    public ProgressIndicator progressIndicatorBottom = new ProgressIndicator();
    private TableView tablePlayers = new TableView();
    private boolean isObservedPlayer = false;
    private String clan_emblems_url = "";
    private double tanking_factor = 0.0;

    ObservableList<Player> playerObservableList = FXCollections.observableArrayList();
    ObservableList<String> serverList = FXCollections.observableArrayList(
            SERVERS_APPLICATION.EU.tag().toLowerCase(),
            SERVERS_APPLICATION.RU.tag().toLowerCase(),
            SERVERS_APPLICATION.NA.tag().toLowerCase(),
            SERVERS_APPLICATION.ASIA.tag().toLowerCase()
    );


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        netStatusObservable = new NetStatusObservable();
        netStatusObservable.attach(this);
        ICOconnectStatus.setFill(Paint.valueOf(Colors.YELLOWhexffbd2e));
        CHB_selectServer.setItems(serverList);
        CHB_selectServer.getSelectionModel().selectFirst();

        progressIndicator.setPrefSize(20.0, 20.0);
        progressLoaderBox.getChildren().add(progressIndicator);
        progressLoaderBox.setVisible(false);

        progressIndicatorBottom.setPrefSize(10.0, 10.0);
        PANE_bottom_loader.getChildren().add(progressIndicatorBottom);
        GR_bootom_loader.setVisible(false);

        CHB_selectServer.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (internet_access == INTERNET_ACCESS.NO_CONNECTION) {
                    Alerts.simple.show(Alert.AlertType.ERROR, "Błąd!", INTERNET_ACCESS.NO_CONNECTION.message() + "!");
                    return;
                }

                CHB_selectServer.setValue(serverList.get(newValue.intValue()));
                getPlayer();
                System.out.println(CHB_selectServer.getValue());
            }
        });

        tablePlayers.setOnMouseClicked(event -> {
            if (internet_access == INTERNET_ACCESS.NO_CONNECTION) {
                Alerts.simple.show(Alert.AlertType.ERROR, "Błąd!", INTERNET_ACCESS.NO_CONNECTION.message() + "!");
                return;
            }
            Player player = (Player) tablePlayers.getSelectionModel().getSelectedItem();

            String playerNicName = player.getNickname();
            String playerAccountID = String.valueOf(player.getAccount_id());

            showPlayerPersonalData(playerNicName, playerAccountID);
        });

        tablePlayers.setMinWidth(382.0);
        tablePlayers.setMaxWidth(382.0);
        tablePlayers.setMinHeight(450.00);

        PANE_playerBaseProperty.setVisible(false);
        LBL_add_delete_player_text.setVisible(false);

    }

    private void showPlayerPersonalData (String nickName, String accountId) {
        String domain = getURLServer().domain() + URLS.account_info + "?" + URLS.id_wot_application + "&account_id=" + accountId + "&fields=statistics.all,clan_id";

        GR_clanPane.setVisible(false);
        GR_bootom_loader.setVisible(true);

        Runnable runnable = () -> {
            Object playerPersonalDataObj = networkManager.getPlayerPersonalData(domain, accountId);
            Platform.runLater(() -> {
               if (playerPersonalDataObj instanceof PlayerPersonalData) {
                   PlayerPersonalData playerPersonalData = (PlayerPersonalData) playerPersonalDataObj;
                   LBL_battle_win.setText(String.valueOf(playerPersonalData.getWins()));
                   LBL_battle_draw.setText(String.valueOf(playerPersonalData.getDraws()));
                   LBL_battle_losses.setText(String.valueOf(playerPersonalData.getLossess()));
                   long allBattle = playerPersonalData.getWins() + playerPersonalData.getDraws() + playerPersonalData.getLossess();
                   LBL_battle_all.setText(String.valueOf(allBattle));
                   LBL_battle_avg_xp.setText(String.valueOf(playerPersonalData.getBattle_avg_xp()));
                   LBL_frags.setText(String.valueOf(playerPersonalData.getFrags()));
                   LBL_avg_damage_assisted.setText(String.valueOf(playerPersonalData.getAvg_damage_assisted()));
                   LBL_hits_percents.setText(String.valueOf(playerPersonalData.getHits_percents()));
                   LBL_spotted.setText(String.valueOf(playerPersonalData.getSpotted()));
                   LBL_dropped_capture_points.setText(String.valueOf(playerPersonalData.getDropped_capture_points()));
                   LBL_capture_points.setText(String.valueOf(playerPersonalData.getCapture_points()));
                   LBL_damage_dealt.setText(String.valueOf(playerPersonalData.getDamage_dealt()));
                   LBL_damage_received.setText(String.valueOf(playerPersonalData.getDamage_received()));
                   LBL_survived_battles.setText(String.valueOf(playerPersonalData.getSurvived_battles()));
                   LBL_xp.setText(String.valueOf(playerPersonalData.getXp()));
                   tanking_factor = playerPersonalData.getTanking_factor();


                   if (!playerPersonalData.getClanTag().equals("0") || !playerPersonalData.getClanEmblemsURL().equals("0")) {
                       clan_emblems_url = playerPersonalData.getClanEmblemsURL();
                       Image emeblems = new Image(clan_emblems_url);
                       IMG_clanAvatar.setImage(emeblems);
                       LBL_clan_tag.setText(playerPersonalData.getClanTag());
                       LBL_id_clan.setText(String.valueOf(playerPersonalData.getClanID()));
                       GR_clanPane.setVisible(true);
                   } else { clan_emblems_url = ""; }

                   GR_bootom_loader.setVisible(false);
               }
                LBL_playerName.setText(nickName);
                LBLaccountID.setText(accountId);
                LBL_serverTag.setText(getURLServer().tag());
                LBL_add_delete_player_text.setVisible(false);

                try {
                    isObservedPlayer();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                PANE_playerBaseProperty.setVisible(true);
            });
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }


    private void printPlayerList () {

        tablePlayers.setEditable(true);
        tablePlayersClear();
        TableColumn<Player, String> lp = new TableColumn("Lp.");
        TableColumn<Player, String> playerName = new TableColumn("Nazwa Gracza");
        TableColumn<Player, String> playerID = new TableColumn("ID Gracza");

        lp.setMinWidth(20.0);
        playerName.setMinWidth(250.0);
        playerID.setMinWidth(80.0);

        tablePlayers.getColumns().addAll(lp, playerName, playerID);


        PANE_table_playerListBox.getChildren().add(tablePlayers);
        lp.setCellValueFactory(new PropertyValueFactory<>("lp"));
        playerName.setCellValueFactory(new PropertyValueFactory<>("nickname"));
        playerID.setCellValueFactory(new PropertyValueFactory<>("account_id"));
        tablePlayers.setItems(playerObservableList);
    }

    private void tablePlayersClear() {
        tablePlayers.getColumns().clear();
        PANE_table_playerListBox.getChildren().clear();
    }


    @Override
    public void update(INTERNET_ACCESS internetAccess) {

        internet_access = internetAccess;

        Platform.runLater(() -> {
            LBL_netStatusInfo.setText(internetAccess.message());
            if (internetAccess == INTERNET_ACCESS.CONNECTION) {
                ICOconnectStatus.setFill(Paint.valueOf(Colors.GREENhex28ca42));
            } else if (internetAccess == INTERNET_ACCESS.NO_CONNECTION) {
                ICOconnectStatus.setFill(Paint.valueOf(Colors.REDhexff6059));
            } else {
                ICOconnectStatus.setFill(Paint.valueOf(Colors.YELLOWhexffbd2e));
            }
        });
    }

    public void onSearchPlayer(KeyEvent keyEvent) {
        getPlayer();
    }

    private void getPlayer() {

        if (internet_access == INTERNET_ACCESS.NO_CONNECTION) {
            tablePlayersClear();
            tablePlayers.setEditable(true);
            PANE_playerBaseProperty.setVisible(false);
            return;
        }

        String searchText = TF_search.getText();
        searchText  = searchText.replaceAll("\\s", "");

        if (searchText.length() < 3) {
            tablePlayersClear();
            tablePlayers.setEditable(true);
            return;
        }

        String domain = getURLServer().domain() + URLS.account_list + "?" + URLS.id_wot_application + "&search=" + searchText + "&" + URLS.language;
        Runnable runnable = () -> {
            progressLoaderBox.setVisible(true);
            Object playerList = networkManager.getPlayerLogin(domain);
            if (playerList instanceof DATA_ERROR) {
               // System.out.println("DATA ERROR");
            } else {
                if (playerList instanceof PlayerList) {
                    playerObservableList.clear();
                    ((PlayerList) playerList).getPlayerList().forEach(player -> {
                        playerObservableList.add(new Player(player.getLp(), player.getNickname(), player.getAccount_id()));
                    });

                    Platform.runLater(this::printPlayerList);
                }
            }
            progressLoaderBox.setVisible(false);

        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private SERVERS_APPLICATION getURLServer() {
        String domain =  CHB_selectServer.getValue();
        SERVERS_APPLICATION servers_application = null;

        switch (domain) {
            case "eu":
                servers_application = SERVERS_APPLICATION.EU;
                break;
            case "ru":
                servers_application = SERVERS_APPLICATION.RU;
                break;
            case "na":
                servers_application = SERVERS_APPLICATION.NA;
                break;
            default:
                servers_application = SERVERS_APPLICATION.ASIA;
                break;
        }
        return servers_application;
    }

    public void onActionAddToFollowed(ActionEvent actionEvent) throws SQLException {
        int id_account =  Integer.parseInt(LBLaccountID.getText());

        DatabaseManager databaseManager = new DatabaseManager();

        if (isObservedPlayer) {
            String query = "DELETE FROM " + DatabaseLocal.tabels.players + " WHERE  id_account=" + id_account;
            databaseManager.delete(query);
            query = "DELETE FROM " + DatabaseLocal.tabels.init_players + " WHERE  id_account=" + id_account;
            databaseManager.delete(query);
            LBL_add_delete_player_text.setVisible(true);
            LBL_add_delete_player_text.setText("Gracz został usunięty z obserwowanych!");
            LBL_add_delete_player_text.setTextFill(Color.valueOf(Colors.REDhexff6059));

        } else {
            String name = LBL_playerName.getText();
            int id_clan = 0;
            String clan_tag = "";
            if (GR_clanPane.isVisible()) {
                id_clan = Integer.parseInt(LBL_id_clan.getText());
                clan_tag = LBL_clan_tag.getText();
            }
            String server_location = LBL_serverTag.getText();

            String query = "INSERT INTO " + DatabaseLocal.tabels.players + " (name, id_account, id_clan, server, time_add) " +
                    "VALUES ('" + name + "', " + id_account + ", " + id_clan + ", '" + server_location + "', datetime('now', 'localtime'))";
            databaseManager.insert(query);

            // id_account
            long wins = Long.parseLong(LBL_battle_win.getText());
            long draws = Long.parseLong(LBL_battle_draw.getText());
            long lossess = Long.parseLong(LBL_battle_losses.getText());
            // String clan_tag
           // clan_emblems_url
            //  id_clan
            long battle_avg_xp = Long.parseLong(LBL_battle_avg_xp.getText());
            long frags = Long.parseLong(LBL_frags.getText());
            double avg_damage_assisted = Double.parseDouble(LBL_avg_damage_assisted.getText());
            int hits_percents = Integer.parseInt(LBL_hits_percents.getText());
            long spotted = Long.parseLong(LBL_spotted.getText());
            long dropped_capture_points = Long.parseLong(LBL_dropped_capture_points.getText());
            long capture_points = Long.parseLong(LBL_capture_points.getText());
            long damage_dealt = Long.parseLong(LBL_damage_dealt.getText());
            long damage_received = Long.parseLong(LBL_damage_received.getText());
            long survived_battles = Long.parseLong(LBL_survived_battles.getText());
            long xp = Long.parseLong(LBL_xp.getText());

            query = "INSERT INTO " + DatabaseLocal.tabels.init_players + " (id_account, wins, draws, lossess, clan_tag, clan_emblems_url, clan_id, battle_avg_xp, frags, avg_damage_assisted," +
                    "hits_percents, spotted, dropped_capture_points, capture_points, damage_dealt, damage_received, survived_battles, xp, tanking_factor, insert_datetime) " +
                    "VALUES (" + id_account + ", " + wins + ", " + draws + ", " + lossess + ", '" + clan_tag + "', '" + clan_emblems_url + "', " + id_clan + ", " + battle_avg_xp + ", " + frags + ", " + avg_damage_assisted +
                    ", " + hits_percents + ", " + spotted + ", " + dropped_capture_points + ", " + capture_points + ", " + damage_dealt + ", " + damage_received +
                    ", " + survived_battles + ", " + xp + ", " + tanking_factor + " , datetime('now', 'localtime'))";

            databaseManager.insert(query);

            LBL_add_delete_player_text.setVisible(true);
            LBL_add_delete_player_text.setText("Gracz został dodany do obserwowanych!");
            LBL_add_delete_player_text.setTextFill(Color.valueOf(Colors.GREENhex28ca42));
        }
        databaseManager.closeConnection();
        isObservedPlayer();
    }

    private void isObservedPlayer() throws SQLException {
        int id_account =  Integer.parseInt(LBLaccountID.getText());

        DatabaseManager databaseManager = new DatabaseManager();
        String query = "SELECT COUNT(id_account) AS count FROM " + DatabaseLocal.tabels.players + " WHERE id_account=" + id_account + " LIMIT 1";
        ResultSet resultSet = databaseManager.select(query);
        if (resultSet.getInt("count") == 1) {
            Image image_eye = new Image("eye_looking.png");
            IMG_eye.setImage(image_eye);
            isObservedPlayer = true;
            BTN_add_to_followed.setText("Usuń z obserwowanych");
            BTN_add_to_followed.setStyle("-fx-background-color: " + Colors.REDhexff6059);
        } else {
            Image image_eye = new Image("eye_not_looking.png");
            IMG_eye.setImage(image_eye);
            isObservedPlayer = false;
            BTN_add_to_followed.setText("Dodaj do obserwowanych");
            BTN_add_to_followed.setStyle("-fx-background-color: " + Colors.GREENhex28ca42);
        }
        resultSet.close();
        databaseManager.closeConnection();
    }

    public void getFollowedPlayers(ActionEvent actionEvent) throws IOException {
        ViewControllerPlayersList viewControllerPlayersList = new ViewControllerPlayersList();
        viewControllerPlayersList.showView();
    }
}
