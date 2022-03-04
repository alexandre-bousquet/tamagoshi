package tamagoshi.graphic;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import tamagoshi.jeu.TamaGame;
import tamagoshi.tamagoshis.*;

import java.util.*;

public class TamaGameGraphique extends Application {
    private TamaGame tamaGame;
    private int difficulty = 2;
    private int lifeTime;
    private TextArea console;

    /**
     * Liste des tamagoshis créer au départ du jeu.
     */
    private List<Tamagoshi> listeTamagoshisDepart;

    /**
     * Liste des tamagoshis vivants à l'instant T.
     */
    private List<Tamagoshi> listeTamagoshisEnCours;

    /**
     * Liste des fenêtres de tamagoshis.
     */
    private List<TamaStage> listeTamaStage;

    /**
     * Liste des noms possibles pour les tamagoshis.
     */
    private final ArrayList<String> names = new ArrayList<>();

    @Override
    public void start(Stage stage) throws Exception {
        this.tamaGame = new TamaGame();
        this.listeTamagoshisDepart = new ArrayList<>();
        this.listeTamagoshisEnCours = new ArrayList<>();
        this.listeTamaStage = new ArrayList<>();
        this.console = new TextArea("- Logs -");
        this.console.setEditable(false);

        BorderPane root = new BorderPane();
        root.setTop(this.generateMenuBar());
        root.setCenter(this.console);
        Scene scene = new Scene(root, 500, 500);
        stage.setTitle("TamaGame");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        EventHandler<MouseEvent> eventHandler = e -> {
            System.out.println("TEST");
        };
        stage.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);

        this.play();
    }

    /**
     * Initialise le jeu avec les méthodes précédentes.
     */
    private void initialisation() {
        this.initNamesList();
        this.initTamagoshis();
        this.initLifeTime();
    }

    /**
     * Lance le jeu.
     */
    public void play() {
        this.initialisation();
        int cycle = 1;
        /*while (!this.listeTamagoshisEnCours.isEmpty() && cycle <= Tamagoshi.getLifeTime()) {
            //Tamagoshi tamagoshi =
            this.listeTamagoshisEnCours.removeIf(t -> t.getTamaPane().getTamagoshi().getEnergy() <= 0 || t.getFun() <= 0 || t.getAge() >= Tamagoshi.getLifeTime());
            if (this.listeTamagoshisEnCours.isEmpty()) {
                break;
            }
            this.log("------------ " + TamaGame.messages.getString("cycle")+ " n°" + cycle + " ------------");
            for (TamaStage t : this.listeTamagoshisEnCours) {
                t.parler();
            }
            this.nourrir();
            this.jouer();
            for (Tamagoshi t : this.listeTamagoshisEnCours) {
                t.consommeEnergy();
                t.consommeFun();
                t.vieillir();
            }
            cycle++;
        }*/
        this.log("------------- " + TamaGame.messages.getString("endOfTheGame") + " ------------");
        this.log("------------ " + TamaGame.messages.getString("result") + " -------------");
        //this.resultat();
    }

    public void nextCycle() {

    }

    private MenuBar generateMenuBar() {
        Menu gameMenu = new Menu("Jeu");
        MenuItem newGameItem = new MenuItem("Nouvelle partie");
        gameMenu.getItems().addAll(newGameItem);

        Menu optionsMenu = new Menu("Options");
        MenuItem difficultyItem = new MenuItem("Difficulté");
        optionsMenu.getItems().addAll(difficultyItem);

        Menu helpMenu = new Menu("Aide");
        MenuItem aboutItem = new MenuItem("Informations");
        aboutItem.setOnAction(actionEvent -> this.displayInformations());
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
        Scanner scan = new Scanner(Objects.requireNonNull(this.getClass().getResourceAsStream("/tamagoshi/names.txt")));
        while (scan.hasNextLine()) {
            String nom = scan.nextLine();
            this.names.add(nom);
        }
        scan.close();
        Collections.shuffle(this.names);
    }

    public void initTamagoshis() {
        for (int i = 0; i < this.getDifficulty(); i++) {
            Tamagoshi tamagoshi = this.generateRandomTamagoshi();
            TamaStage tamaStage = new TamaStage(new TamaPane(tamagoshi), this);
            this.getListeTamagoshisDepart().add(tamagoshi);
            this.getListeTamagoshisEnCours().add(tamagoshi);
            this.getListeTamaStage().add(tamaStage);
        }
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

    private void initLifeTime() {
        this.lifeTime = 10;
    }

    private void activerBoutons() {
        for (TamaStage tamaStage : this.listeTamaStage) {
            tamaStage.activerBoutonNourrir();
            tamaStage.activerBoutonJouer();
        }
    }

    private void log(String message) {
        this.getConsole().appendText("\n" + message);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    // Getters et setters

    private TextArea getConsole() {
        return console;
    }

    private List<Tamagoshi> getListeTamagoshisEnCours() {
        return listeTamagoshisEnCours;
    }

    private List<Tamagoshi> getListeTamagoshisDepart() {
        return listeTamagoshisDepart;
    }

    public List<TamaStage> getListeTamaStage() {
        return listeTamaStage;
    }

    private int getDifficulty() {
        return difficulty;
    }

    private TamaGame getTamaGame() {
        return tamaGame;
    }
}
