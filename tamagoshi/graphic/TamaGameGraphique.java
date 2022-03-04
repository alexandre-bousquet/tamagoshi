package tamagoshi.graphic;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import tamagoshi.jeu.TamaGame;
import tamagoshi.tamagoshis.*;

import java.util.*;

public class TamaGameGraphique extends Application implements EventHandler {
    private TamaGame tamaGame;
    private int difficulty;
    private TextArea console;

    /**
     * Liste des tamagoshis créer au départ du jeu.
     */
    private List<TamaStage> listeTamagoshisDepart;

    /**
     * Liste des tamagoshis vivants à l'instant T.
     */
    private List<TamaStage> listeTamagoshisEnCours;

    /**
     * Liste des noms possibles pour les tamagoshis.
     */
    private final ArrayList<String> names = new ArrayList<>();

    @Override
    public void start(Stage stage) throws Exception {
        this.tamaGame = new TamaGame();
        this.initNamesList();
        this.listeTamagoshisDepart = new ArrayList<>();
        this.listeTamagoshisEnCours = new ArrayList<>();
        this.console = new TextArea("- Logs -");
        this.console.setEditable(false);
        this.console.appendText("\ntest");

        BorderPane root = new BorderPane();
        root.setTop(this.generateMenuBar());
        root.setCenter(this.console);
        Scene scene = new Scene(root, 400, 400);
        stage.setTitle("TamaGame");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        for (int i = 0; i < 2; i++) {
            TamaStage tamaStage = new TamaStage(new TamaPane(this.generateRandomTamagoshi()));
            this.listeTamagoshisDepart.add(tamaStage);
            this.listeTamagoshisEnCours.add(tamaStage);
        }
    }

    public MenuBar generateMenuBar() {
        Menu gameMenu = new Menu("Jeu");
        MenuItem newGameItem = new MenuItem("Nouvelle partie");
        gameMenu.getItems().addAll(newGameItem);

        Menu optionsMenu = new Menu("Options");
        MenuItem difficultyItem = new MenuItem("Difficulté");
        optionsMenu.getItems().addAll(difficultyItem);

        Menu helpMenu = new Menu("Aide");
        MenuItem aboutItem = new MenuItem("Informations");
        aboutItem.setOnAction(actionEvent -> {
            this.displayInformations();
        });
        MenuItem helpItem = new MenuItem("Aide");
        helpMenu.getItems().addAll(aboutItem, helpItem);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(gameMenu, optionsMenu, helpMenu);
        return menuBar;
    }

    private void displayInformations() {
        Stage infoStage = new Stage();
        infoStage.setResizable(false);
        infoStage.setTitle("Informations");
        Label infoLabel = new Label("Jeu créé par Alexandre Bousquet, étudiant en LP APIDAE à l'IUT de Montpellier.");
        infoLabel.getStyleClass().add("label");
        Group infoGroup = new Group();
        infoGroup.getChildren().add(infoLabel);
        Scene infoScene = new Scene(infoGroup);
        infoScene.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/tamagoshi/style.css")).toExternalForm());
        infoStage.setScene(infoScene);
        infoStage.show();
    }

    /**
     * Initialise la liste des noms possibles pour les tamagoshis.
     */
    private void initNamesList() {
        Scanner scan = new Scanner(Objects.requireNonNull(TamaGame.class.getResourceAsStream("/tamagoshi/names.txt")));
        while (scan.hasNextLine()) {
            String nom = scan.nextLine();
            names.add(nom);
        }
        scan.close();
        Collections.shuffle(names);
    }

    private Tamagoshi generateRandomTamagoshi() {
        double rand = Math.random();
        int indexName = new Random().nextInt(this.names.size());
        String name = this.names.get(indexName);
        this.names.remove(indexName);
        Tamagoshi t;
        if (rand <= 0.40) {
            t = new GrosJoueur(name);
        } else if (rand <= 0.8) {
            t = new GrosMangeur(name);
        } else if (rand <= 0.9) {
            t = new Cachotier(name);
        } else if (rand <= 0.95) {
            t = new Bipolaire(name);
        } else {
            t = new Suicidaire(name);
        }
        return t;
    }

    private void activerBoutons() {
        for (TamaStage tamaStage : listeTamagoshisEnCours) {
            tamaStage.activerBoutonNourrir();
            tamaStage.activerBoutonJouer();
        }
    }

    public TamaGame getTamaGame() {
        return tamaGame;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void handle(Event event) {
        System.out.println(event.getSource());
    }
}
