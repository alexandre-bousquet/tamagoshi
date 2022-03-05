package tamagoshi.graphic;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import tamagoshi.exceptions.NegativeLifeTimeException;
import tamagoshi.tamagoshis.*;

import java.io.*;
import java.util.*;

import static tamagoshi.jeu.TamaGame.messages;

public class TamaGameGraphique extends Application {
    private int difficulty = 0;
    private TextArea console;
    private boolean peutNourrir = true;
    private boolean peutJouer = true;
    private int cycle = 0;
    private final String propertiesFileLocation = System.getenv("LOCALAPPDATA") + "\\tamagoshiProperties.properties";
    Properties props = new Properties();

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
        this.getProperties();

        this.listeTamagoshisDepart = new ArrayList<>();
        this.listeTamagoshisEnCours = new ArrayList<>();
        this.listeTamaStage = new ArrayList<>();
        this.console = new TextArea("- Logs -");
        this.console.setEditable(false);

        BorderPane root = new BorderPane();
        root.setTop(this.generateMenuBar());
        root.setCenter(this.console);
        Scene scene = new Scene(root, 500, 500);
        stage.setOnCloseRequest(ev -> Platform.exit());
        stage.setTitle("TamaGame");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        this.play();
    }

    private void getProperties() {
        try (InputStream in = new FileInputStream(this.propertiesFileLocation)) {
            this.props.load(in);
        } catch (IOException e1) {
            props.setProperty("difficulty", "3");
            props.setProperty("lifeTime", "10");
            try (OutputStream out = new FileOutputStream(this.propertiesFileLocation)) {
                props.store(out, "Config");
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    /**
     * Initialise le jeu avec les méthodes précédentes.
     */
    private void initialisation() throws NegativeLifeTimeException {
        this.initNamesList();
        this.initLifeTime();
        this.initTamagoshis();
    }

    /**
     * Lance le jeu.
     */
    public void play() throws NegativeLifeTimeException {
        this.initialisation();
        this.nextCycle();
    }

    protected void prepareNextCycle() {
        if (!this.isPeutNourrir() && !this.isPeutJouer()) {
            this.getListeTamagoshisEnCours().removeIf(t -> !t.consommeEnergy() || !t.consommeFun() || !t.vieillir());
            //this.getListeTamaStage().removeIf(t -> !this.getListeTamagoshisEnCours().contains(t.getTamaPane().getTamagoshi()));
            this.nextCycle();
        }
    }

    private void nextCycle() {
        if (this.getCycle() < Tamagoshi.getLifeTime() && !this.getListeTamagoshisEnCours().isEmpty()) {
            this.activerBoutons();
            this.incrementCycle();
            this.log("------------ " + messages.getString("cycle")+ " n°" + this.getCycle() + " ------------");
            for (TamaStage tamaStage : this.getListeTamaStage()) {
                tamaStage.getTamaPane().updatePhase();
            }
        } else {
            this.resultat();
        }
    }

    /**
     * Calcule le score et le retourne.
     * Il est égal à ((âge des tamagoshis en vie * 100) / âge total de tous les tamagoshis)
     * @return {int} = score calculé
     */
    private int score() {
        int maxAge = Tamagoshi.getLifeTime() * this.getListeTamagoshisDepart().size();
        int currentAge = 0;
        for (Tamagoshi t : this.getListeTamagoshisDepart()) {
            currentAge += t.getAge();
        }
        return currentAge * 100 / maxAge;
    }

    /**
     * Affiche le score, la difficulté et l'état de chaque tamagoshi à la fin de la partie.
     */
    private void resultat() {
        this.log("------------- " + messages.getString("endOfTheGame") + " ------------");
        this.log("------------ " + messages.getString("result") + " -------------");
        for (Tamagoshi t : this.getListeTamagoshisDepart()) {
            StringBuilder str = new StringBuilder()
                    .append(t.getName())
                    .append(" ")
                    .append(messages.getString("whoWasA"))
                    .append(" ")
                    .append(t.getClass().getSimpleName())
                    .append(" ");
            if (t.getAge() >= Tamagoshi.getLifeTime()) {
                str.append(messages.getString("hasSurvived"));
            } else {
                str.append(messages.getString("hasNotSurvived"));
            }
            this.log(String.valueOf(str));
        }
        for (TamaStage tamaStage : this.getListeTamaStage()) {
            tamaStage.getTamaPane().updatePhase();
        }
        this.log(messages.getString("difficultyLevel") + " : " + this.getListeTamagoshisDepart().size());
        this.log(messages.getString("finalScore") + " : " + this.score() + "%");
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

    private void initTamagoshis() {
        for (int i = 0; i < Integer.parseInt(this.props.getProperty("difficulty")); i++) {
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
            if (this.getListeTamagoshisEnCours().contains(tamaStage.getTamaPane().getTamagoshi())) {
                tamaStage.activerBoutonNourrir();
                tamaStage.activerBoutonJouer();
            }
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

    private boolean isPeutNourrir() {
        return peutNourrir;
    }

    protected void setPeutNourrir(boolean peutNourrir) {
        this.peutNourrir = peutNourrir;
    }

    private boolean isPeutJouer() {
        return peutJouer;
    }

    protected void setPeutJouer(boolean peutJouer) {
        this.peutJouer = peutJouer;
    }

    private int getCycle() {
        return cycle;
    }

    private void incrementCycle() {
       this.cycle++;
    }
}
