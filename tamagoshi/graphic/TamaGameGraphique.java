package tamagoshi.graphic;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import tamagoshi.exceptions.NegativeLifeTimeException;
import tamagoshi.tamagoshis.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static tamagoshi.jeu.TamaGame.*;

public class TamaGameGraphique extends Application {
    private TextArea console;
    private boolean peutNourrir = true;
    private boolean peutJouer = true;
    private int cycle = 0;
    private final String propertiesFileLocation = System.getenv("LOCALAPPDATA") + "\\tamagoshiProperties.properties";
    private final Properties props = new Properties();
    private Stage stage;

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
    public void start(Stage stage) throws NegativeLifeTimeException {
        this.play(stage);
    }

    private void restart(Stage stage) throws NegativeLifeTimeException {
        for (TamaStage t : this.getListeTamaStage()) {
            t.close();
        }
        this.play(stage);
    }

    private void getProperties() {
        try (InputStream in = new FileInputStream(this.propertiesFileLocation)) {
            this.getProps().load(in);
        } catch (IOException e1) {
            /*this.getProps().setProperty("difficulty", String.valueOf(3));
            this.getProps().setProperty("lifeTime", String.valueOf(10));
            this.getProps().setProperty("language", "fr_FR");*/
            this.createProperties();
        }
    }

    private void createProperties() {
        try (OutputStream out = new FileOutputStream(this.propertiesFileLocation)) {
            //this.updateOptions(Integer.parseInt(this.getProps().getProperty("difficulty")), Integer.parseInt(this.getProps().getProperty("lifeTime")), this.getProps().getProperty("language"));
            this.updateProperties(3, 10, "fr_FR");
            this.getProps().store(out, "TamaGameGraphique config file");
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private void updateProperties(int difficulty, int lifeTime, String language) {
        try (OutputStream out = new FileOutputStream(this.propertiesFileLocation)) {
            this.getProps().setProperty("difficulty", String.valueOf(difficulty));
            this.getProps().setProperty("lifeTime", String.valueOf(lifeTime));
            this.getProps().setProperty("language", language);
            this.getProps().store(out, "TamaGameGraphique config file");
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    /**
     * Initialise le jeu avec les méthodes précédentes.
     */
    private void initialisation() throws NegativeLifeTimeException {
        messages = ResourceBundle.getBundle("MessageBundle", new Locale(this.getProps().getProperty("language")));
        initNamesList(this.getNames());
        this.initLifeTime();
        this.initTamagoshis();
    }

    /**
     * Lance le jeu.
     */
    public void play(Stage stage) throws NegativeLifeTimeException {
        this.getProperties();

        this.stage = stage;
        this.listeTamagoshisDepart = new ArrayList<>();
        this.listeTamagoshisEnCours = new ArrayList<>();
        this.listeTamaStage = new ArrayList<>();
        this.console = new TextArea("- Logs -");
        this.console.setEditable(false);
        this.cycle = 0;
        this.initialisation();

        BorderPane root = new BorderPane();
        root.setTop(this.generateMenuBar());
        root.setCenter(this.getConsole());
        Scene scene = new Scene(root, 500, 500);

        this.stage.setOnCloseRequest(ev -> Platform.exit());
        this.stage.setTitle("TamaGame");
        this.stage.setResizable(false);
        this.stage.setScene(scene);
        this.stage.show();

        this.nextCycle();
    }

    protected void prepareNextCycle() {
        if (!this.isPeutNourrir() && !this.isPeutJouer()) {
            this.getListeTamagoshisEnCours().removeIf(t -> !t.consommeEnergy() || !t.consommeFun() || !t.vieillir());
            this.nextCycle();
        }
    }

    private void nextCycle() {
        if (this.getCycle() < Tamagoshi.getLifeTime() && !this.getListeTamagoshisEnCours().isEmpty()) {
            this.activerBoutons();
            this.incrementCycle();
            this.log("------------ " + messages.getString("cycle")+ " n°" + this.getCycle() + " ------------");
            for (Tamagoshi tamagoshi : this.getListeTamagoshisEnCours()) {
                tamagoshi.parler();
            }
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
        Menu gameMenu = new Menu(messages.getString("game"));
        MenuItem newGameItem = new MenuItem(messages.getString("newGame"));
        newGameItem.setOnAction(actionEvent -> {
            try {
                this.restart(this.getStage());
            } catch (NegativeLifeTimeException e) {
                e.printStackTrace();
            }
        });
        gameMenu.getItems().addAll(newGameItem);

        Menu optionsMenu = new Menu(messages.getString("settings"));
        MenuItem optionsItem = new MenuItem(messages.getString("settings"));
        optionsItem.setOnAction(actionEvent -> this.displayOptions());
        optionsMenu.getItems().addAll(optionsItem);

        Menu helpMenu = new Menu(messages.getString("help"));
        MenuItem aboutItem = new MenuItem(messages.getString("informations"));
        aboutItem.setOnAction(actionEvent -> this.displayInformations());
        MenuItem helpItem = new MenuItem(messages.getString("help"));
        helpMenu.getItems().addAll(aboutItem, helpItem);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(gameMenu, optionsMenu, helpMenu);
        return menuBar;
    }

    private void displayOptions() {
        Stage optionsStage = new Stage();
        optionsStage.setResizable(false);
        optionsStage.setTitle(messages.getString("settings"));
        Group infoGroup = new Group();

        // Difficulté (nombre de tamagoshis)
        Group difficultyGroup = new Group();
        Label difficultyLabel = new Label(messages.getString("difficulty"));
        difficultyLabel.getStyleClass().add("label");
        Slider difficultySlider = new Slider(3,8, Integer.parseInt(this.getProps().getProperty("difficulty")));
        difficultySlider.setShowTickLabels(true);
        difficultySlider.setShowTickMarks(true);
        difficultySlider.setMajorTickUnit(1);
        difficultySlider.setMinorTickCount(0);
        difficultySlider.setBlockIncrement(1);
        difficultySlider.setSnapToTicks(true);
        difficultySlider.setLayoutX(20);
        difficultySlider.setLayoutY(50);
        difficultyGroup.getChildren().addAll(difficultyLabel, difficultySlider);

        // Durée de la partie
        Group lifeTimeGroup = new Group();
        Label lifeTimeLabel = new Label(messages.getString("lifetime"));
        lifeTimeLabel.setLayoutY(90);
        lifeTimeLabel.getStyleClass().add("label");
        Slider lifeTimeSlider = new Slider(10,30, Integer.parseInt(this.getProps().getProperty("lifeTime")));
        lifeTimeSlider.setShowTickLabels(true);
        lifeTimeSlider.setShowTickMarks(true);
        lifeTimeSlider.setMajorTickUnit(5);
        lifeTimeSlider.setMinorTickCount(0);
        lifeTimeSlider.setBlockIncrement(1);
        lifeTimeSlider.setSnapToTicks(true);
        lifeTimeSlider.setLayoutX(20);
        lifeTimeSlider.setLayoutY(140);
        lifeTimeGroup.getChildren().addAll(lifeTimeLabel, lifeTimeSlider);

        // Langues (FR et EN)
        Group langageGroup = new Group();
        Map<String, String> languageMap = new HashMap<>();
        languageMap.put(messages.getString("french"), "fr_FR");
        languageMap.put(messages.getString("english"), "en_US");
        Label langageLabel = new Label(messages.getString("language"));
        langageLabel.setLayoutY(175);
        langageLabel.getStyleClass().add("label");
        AtomicReference<String> langageSelected = new AtomicReference<>(this.getProps().getProperty("language"));
        final ToggleGroup langageToggleGroup = new ToggleGroup();
        int index = 0;
        for (String s : languageMap.keySet()) {
            index++;
            RadioButton radioButton = new RadioButton(s);
            radioButton.setToggleGroup(langageToggleGroup);
            radioButton.setLayoutX(20);
            radioButton.setLayoutY(langageLabel.getLayoutY() + 20 + (index * 30));
            radioButton.setOnAction(actionEvent -> langageSelected.set(languageMap.get(s)));
            if (langageSelected.get().equalsIgnoreCase(languageMap.get(s))) {
                radioButton.setSelected(true);
            }
            langageGroup.getChildren().add(radioButton);
        }
        langageGroup.getChildren().add(langageLabel);

        // Bouton de sauvegarde des options
        Group saveGroup = new Group();
        Label saveInfoLabel = new Label(messages.getString("changesNextLaunch"));
        saveInfoLabel.getStyleClass().add("message");
        saveInfoLabel.getStyleClass().add("messageRed");
        saveInfoLabel.setLayoutY(310);
        Button saveButton = new Button(messages.getString("save"));
        saveButton.setLayoutX(20);
        saveButton.setLayoutY(300);
        saveButton.setOnAction(actionEvent -> {
            this.updateProperties((int) difficultySlider.getValue(), (int) lifeTimeSlider.getValue(), langageSelected.get());
            optionsStage.close();
        });
        saveGroup.getChildren().addAll(saveButton, saveInfoLabel);

        // Paramétrage de la fenêtre
        infoGroup.getChildren().addAll(difficultyGroup, lifeTimeGroup, langageGroup, saveGroup);
        Scene infoScene = new Scene(infoGroup, 360, 360);
        infoScene.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/tamagoshi/style.css")).toExternalForm());
        optionsStage.setScene(infoScene);
        optionsStage.show();
    }

    /**
     * Affiche diverses informations sur le jeu comme son auteur.
     */
    private void displayInformations() {
        Stage infoStage = new Stage();
        infoStage.setResizable(false);
        infoStage.setTitle(messages.getString("informations"));
        Label infoLabel = new Label(messages.getString("gameInfos"));
        infoLabel.getStyleClass().add("label");
        Group infoGroup = new Group();
        infoGroup.getChildren().add(infoLabel);
        Scene infoScene = new Scene(infoGroup);
        infoScene.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/tamagoshi/style.css")).toExternalForm());
        infoStage.setScene(infoScene);
        infoStage.show();
    }

    /**
     * Initialise les tamagoshis du jeu.
     */
    private void initTamagoshis() {
        double screenSize = Screen.getPrimary().getBounds().getWidth();
        double x = 0;
        double y = 0;
        for (int i = 0; i < Integer.parseInt(this.getProps().getProperty("difficulty")); i++) {
            Tamagoshi tamagoshi = generateRandomTamagoshi(this.getNames());
            TamaStage tamaStage = new TamaStage(new TamaPane(tamagoshi), this);
            tamaStage.setX(x);
            tamaStage.setY(y);
            x += screenSize / 4;
            if (x == screenSize) {
                x = 0;
                y = tamaStage.getHeight() + 20;
            }
            this.getListeTamagoshisDepart().add(tamagoshi);
            this.getListeTamagoshisEnCours().add(tamagoshi);
            this.getListeTamaStage().add(tamaStage);
        }
    }

    /**
     * Initialise la durée de vie des Tamagoshis (durée de la partie)
     * @throws NegativeLifeTimeException Si durée de vie négative
     */
    private void initLifeTime() throws NegativeLifeTimeException {
        Tamagoshi.setLifeTime(Integer.parseInt(this.getProps().getProperty("lifeTime")));
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

    // TODO : Voir si je remplace par un textFlow
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

    protected List<TamaStage> getListeTamaStage() {
        return listeTamaStage;
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

    private Stage getStage() {
        return stage;
    }

    private Properties getProps() {
        return props;
    }

    private ArrayList<String> getNames() {
        return names;
    }
}
