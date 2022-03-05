package tamagoshi.graphic;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import tamagoshi.exceptions.NegativeLifeTimeException;
import tamagoshi.jeu.TamaGame;
import tamagoshi.tamagoshis.*;

import java.util.*;

public class TamaGameGraphique extends Application {
    private TamaGame tamaGame;
    private int difficulty = 2;
    private TextArea console;
    private boolean peutNourrir = true;
    private boolean peutJouer = true;
    private int cycle = 0;

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
    private void initialisation() throws NegativeLifeTimeException {
        this.initNamesList();
        this.initTamagoshis();
        this.initLifeTime();
    }

    /**
     * Lance le jeu.
     */
    public void play() throws NegativeLifeTimeException {
        this.initialisation();
        this.nextCycle();
        /*this.log("------------- " + TamaGame.messages.getString("endOfTheGame") + " ------------");
        this.log("------------ " + TamaGame.messages.getString("result") + " -------------");*/
    }

    public void prepareNextCycle() {
        if (!this.isPeutNourrir() && !this.isPeutJouer()) {
            this.getListeTamagoshisEnCours().removeIf(t -> t.getEnergy() <= 0 || t.getFun() <= 0 || t.getAge() >= Tamagoshi.getLifeTime());
            for (Tamagoshi tamagoshi : this.getListeTamagoshisEnCours()) {
                for (int i = 0; i < this.getListeTamaStage().size(); i++) {
                    System.out.println(this.getListeTamaStage().get(i).getTamaPane().getTamagoshi().equals(tamagoshi));
                }
                tamagoshi.consommeEnergy();
                tamagoshi.consommeFun();
                tamagoshi.vieillir();
            }
            this.nextCycle();
        }
    }

    public void nextCycle() {
        this.activerBoutons();
        this.incrementCycle();
        this.log("------------ " + TamaGame.messages.getString("cycle")+ " n°" + this.getCycle() + " ------------");
        for (Tamagoshi tamagoshi : this.getListeTamagoshisEnCours()) {
            tamagoshi.parler();
        }
        if (this.getCycle() >= Tamagoshi.getLifeTime() || this.getListeTamagoshisEnCours().isEmpty()) {
            // TODO
        }
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

    private void initLifeTime() throws NegativeLifeTimeException {
        Tamagoshi.setLifeTime(10);
    }

    private void activerBoutons() {
        this.setPeutNourrir(true);
        this.setPeutJouer(true);
        for (TamaStage tamaStage : this.getListeTamaStage()) {
            tamaStage.activerBoutonNourrir();
            tamaStage.activerBoutonJouer();
        }
    }

    protected void log(String message) {
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

    public boolean isPeutNourrir() {
        return peutNourrir;
    }

    public void setPeutNourrir(boolean peutNourrir) {
        this.peutNourrir = peutNourrir;
    }

    public boolean isPeutJouer() {
        return peutJouer;
    }

    public void setPeutJouer(boolean peutJouer) {
        this.peutJouer = peutJouer;
    }

    public int getCycle() {
        return cycle;
    }

    public void incrementCycle() {
       this.cycle++;
    }
}
