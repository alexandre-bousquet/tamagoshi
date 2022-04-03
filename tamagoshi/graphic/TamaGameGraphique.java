package tamagoshi.graphic;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;
import tamagoshi.exceptions.NegativeLifeTimeException;
import tamagoshi.tamagoshis.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static tamagoshi.jeu.TamaGame.*;

/**
 * Classe principale du jeu en interface graphique. Elle paramètre et lance le jeu.
 */
public class TamaGameGraphique extends Application {
    /**
     * Console permettant les affichages.
     */
    private TextFlow console;

    /**
     * Booléen qui indique si on peut nourrir les tamagoshis.
     */
    private boolean peutNourrir = true;

    /**
     * Booléen qui indique si on peut jouer avec les tamagoshis.
     */
    private boolean peutJouer = true;

    /**
     * Entier qui représente quel à quel tour du jeu nous sommes.
     */
    private int cycle = 0;

    /**
     * Emplacement du fichier contenant les propriétés sauvegardées.
     */
    private final String propertiesFileLocation = System.getenv("LOCALAPPDATA") + "\\tamagoshiProperties.properties";

    /**
     * Propriétés extraites du fichier tamagoshiProperties.properties (emplacement : {@link TamaGameGraphique#propertiesFileLocation}).
     */
    private final Properties props = new Properties();

    /**
     * Stage de l'application.
     */
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

    @Override
    public void start(Stage stage) {
        this.play(stage);
    }

    /**
     * Permet de relancer le jeu.
     * @param stage {@link TamaGameGraphique#stage}
     */
    private void restart(Stage stage) {
        for (TamaStage t : this.getListeTamaStage()) {
            t.close();
        }
        this.play(stage);
    }

    /**
     * Récupère les propriétés du jeu dans le fichier de configuration. Crée les propriétés s'il ne les trouve pas avec {@link TamaGameGraphique#createProperties()}.
     */
    private void getProperties() {
        try (InputStream in = new FileInputStream(this.propertiesFileLocation)) {
            this.getProps().load(in);
            messages = ResourceBundle.getBundle("MessageBundle", new Locale(this.getProps().getProperty("language")));
            System.out.println(this.getProps().getProperty("language"));
            System.out.println(messages.getLocale());
        } catch (IOException | RuntimeException e) {
            this.createProperties();
        }
    }

    /**
     * Crée les propriétés du jeu avec les valeurs par défaut (difficulté = 3, lifetime = 10, langue = "fr_fr").
     */
    private void createProperties() {
        try (OutputStream out = new FileOutputStream(this.propertiesFileLocation)) {
            this.updateSettingsProperties(3, 10, "fr_fr");
            this.getProps().store(out, "TamaGameGraphique config file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Met à jour les propriétés du jeu avec les éléments en paramètre.
     * @param difficulty Nombre de tamagoshis.
     * @param lifeTime Durée de la partie (durée de vie des tamagoshis).
     * @param language Langue du jeu (FR ou EN).
     */
    private void updateSettingsProperties(int difficulty, int lifeTime, String language) {
        try (OutputStream out = new FileOutputStream(this.propertiesFileLocation)) {
            this.getProps().setProperty("difficulty", String.valueOf(difficulty));
            this.getProps().setProperty("lifeTime", String.valueOf(lifeTime));
            this.getProps().setProperty("language", language);
            this.getProps().store(out, "TamaGameGraphique config file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ajour un le score obtenu à al liste des scores sauvegardés s'il est meilleur que l'ancien enregistré (s'il existe).
     * @param newScore Score obtenu à la fin de la dernière partie.
     */
    private void ajouterScore(int newScore) {
        try (OutputStream out = new FileOutputStream(this.propertiesFileLocation)) {
            String difficulty_lifetime = this.getProps().getProperty("difficulty") + "-" + this.getProps().getProperty("lifeTime");
            try {
                int storedScore = Integer.parseInt(this.getProps().getProperty(difficulty_lifetime));
                if (newScore > storedScore) {
                    this.getProps().setProperty(difficulty_lifetime, String.valueOf(newScore));
                }
            } catch (NullPointerException | NumberFormatException e) {
                this.getProps().setProperty(difficulty_lifetime, String.valueOf(newScore));
            }
            this.getProps().store(out, "Score added");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche une fenêtre avec les meilleurs scores pour chaque difficulté/durée de vie jouée.
     */
    private void afficherScores() {
        // Configuration de la fenêtre
        Stage scoreStage = new Stage();
        scoreStage.setResizable(false);
        scoreStage.setTitle(messages.getString("bestScores"));

        // Ajout d'un label indiquant "Meilleurs scores"
        Label scoreLabel = new Label(messages.getString("bestScores"));
        scoreLabel.getStyleClass().add("label");
        scoreLabel.setMinWidth(200);

        // Ajout d'un TextFlow et d'une ScrollPane pour afficher le meilleur score dans chaque difficulté (s'il existe)
        TextFlow scoreTextFlow = new TextFlow();
        int nbScore = 0;
        for (int i = 3; i <= 9; i++) {
            for (int j = 10; j <= 30; j++) {
                int score;
                try {
                    score = Integer.parseInt(this.getProps().getProperty(i + "-" + j));
                    scoreTextFlow.getChildren().add(new Text(messages.getString("difficulty") + " (" +  i + ") / " + messages.getString("lifetime") + " (" +  j + ") : " + score + "%\n"));
                    nbScore++;
                } catch (NullPointerException | NumberFormatException e) {
                    e.getMessage();
                }
            }
        }
        if (nbScore == 0) {
            scoreTextFlow.getChildren().add(new Text(messages.getString("noScoreRegistered")));
        }
        ScrollPane scoreScrollPane = new ScrollPane();
        scoreScrollPane.setVvalue(1);
        scoreScrollPane.setContent(scoreTextFlow);
        scoreScrollPane.setPadding(new Insets(10));
        scoreScrollPane.vvalueProperty().bind(scoreScrollPane.heightProperty());

        // Assemblage de la fenêtre
        int tailleFenetre = nbScore * 25;
        Scene scoreScene = new Scene(scoreScrollPane, 300, tailleFenetre);
        scoreScene.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/tamagoshi/style.css")).toExternalForm());
        scoreStage.setScene(scoreScene);
        scoreStage.show();
    }

    /**
     * Initialise le jeu avec les méthodes précédentes.
     */
    private void initialisation() {
        this.initLifeTime();
        this.initTamagoshis();
    }

    /**
     * Paramètre la fenêtre du jeu et le lance.
     * @param stage {@link javafx.stage.Stage} passé par {@link TamaGameGraphique#start(Stage)}.
     */
    public void play(Stage stage) {
        // Récupération des propriétés
        this.getProperties();

        // Configuration des variables
        this.listeTamagoshisDepart = new ArrayList<>();
        this.listeTamagoshisEnCours = new ArrayList<>();
        this.listeTamaStage = new ArrayList<>();
        this.cycle = 0;

        // Configuration de la console
        this.initConsole();

        // Configuration du jeu
        this.initialisation();

        // Configuration de la fenêtre
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setVvalue(1);
        scrollPane.setContent(this.console);
        scrollPane.setPadding(new Insets(10));
        scrollPane.vvalueProperty().bind(this.console.heightProperty());
        BorderPane root = new BorderPane();
        root.setTop(this.generateMenuBar());
        root.setCenter(scrollPane);
        Scene scene = new Scene(root, 600, 600);

        // Configuration du stage
        this.stage = stage;
        this.stage.setOnCloseRequest(ev -> Platform.exit());
        this.stage.setTitle("TamaGame");
        this.stage.setResizable(false);
        this.stage.setScene(scene);
        this.stage.show();

        // Lancement des cycles
        this.nextCycle();
    }

    private void initConsole() {
        this.console = new TextFlow();
        this.log("[Logs]", 25);
        this.log("");
    }

    /**
     * Vérifie si on peut passer au cycle suivant si on a nourri et jouer avec un tamagoshi et lance {@link TamaGameGraphique#nextCycle()}.
     */
    protected void prepareNextCycle() {
        if (!this.isPeutNourrir() && !this.isPeutJouer()) {
            this.getListeTamagoshisEnCours().removeIf(t -> !t.consommeEnergy() || !t.consommeFun() || !t.vieillir()); // Itérateur qui enlève les tamagoshis morts
            this.log("");
            this.nextCycle();
        }
    }

    /**
     * Lance le prochain cycle de jeu. Si la partie est terminée, lance {@link TamaGameGraphique#resultat()}.
     */
    private void nextCycle() {
        if (this.getCycle() < Tamagoshi.getLifeTime() && !this.getListeTamagoshisEnCours().isEmpty()) {
            this.activerBoutons();
            this.incrementCycle();
            this.log("[" + messages.getString("cycle")+ " n°" + this.getCycle() + "]", 20, Color.BLACK);
            for (Tamagoshi tamagoshi : this.getListeTamagoshisEnCours()) {
                tamagoshi.parler(); // Met à jour le message des tamagoshis
            }
            for (TamaStage tamaStage : this.getListeTamaStage()) {
                tamaStage.getTamaPane().updatePhase(); // Met à jour l'image des tamagoshis
            }
        } else {
            this.resultat(); // Si la partie est terminée.
        }
    }

    /**
     * Calcule le score et le retourne.
     * Il est égal à ((âge des tamagoshis en vie * 100) / âge total de tous les tamagoshis).
     * @return Le score calculé ({@link Integer}).
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
        this.log("[" + messages.getString("endOfTheGame") + "]", 20, Color.BLACK);
        this.log("");
        this.log("[" + messages.getString("result") + "]", 20, Color.BLACK);
        Color couleur;
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
                couleur = Color.GREEN;
            } else {
                str.append(messages.getString("hasNotSurvived"));
                couleur = Color.RED;
            }
            this.log(String.valueOf(str), 15, couleur);
        }
        for (TamaStage tamaStage : this.getListeTamaStage()) {
            tamaStage.getTamaPane().updatePhase();
        }
        this.log(messages.getString("difficultyLevel") + " : " + this.getListeTamagoshisDepart().size(), 15, Color.BLACK);
        this.log(messages.getString("finalScore") + " : " + this.score() + "%", 15, Color.BLACK);
        this.ajouterScore(this.score());
    }

    /**
     * Génère la barre de menu avec la possibilité de lancer une nouvelle partie, de changer les paramètres, obtenir des informations sur le jeu et bien d'autres...
     * @return La barre de menu générée ({@link MenuBar }).
     */
    private MenuBar generateMenuBar() {
        // Menu "Jeu"
        Menu gameMenu = new Menu(messages.getString("game"));
        MenuItem newGameItem = new MenuItem(messages.getString("newGame"));
        newGameItem.setOnAction(actionEvent -> this.restart(this.getStage()));
        MenuItem bestScores = new MenuItem(messages.getString("bestScores"));
        bestScores.setOnAction(actionEvent -> this.afficherScores());
        gameMenu.getItems().addAll(newGameItem, bestScores);

        // Menu "Options"
        Menu optionsMenu = new Menu(messages.getString("settings"));
        MenuItem optionsItem = new MenuItem(messages.getString("settings"));
        optionsItem.setOnAction(actionEvent -> this.displayOptions());
        optionsMenu.getItems().addAll(optionsItem);

        // Menu "Aide"
        Menu helpMenu = new Menu(messages.getString("help"));
        MenuItem aboutItem = new MenuItem(messages.getString("informations"));
        aboutItem.setOnAction(actionEvent -> this.displayInformations());
        //MenuItem helpItem = new MenuItem(messages.getString("help"));
        helpMenu.getItems().addAll(aboutItem/*, helpItem*/);

        // Assemblage du menu
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(gameMenu, optionsMenu, helpMenu);

        return menuBar;
    }

    /**
     * Affiche une fenêtre sur laquelle on peut modifier les options du jeu.
     */
    private void displayOptions() {
        // Configuration de la fenêtre
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
        languageMap.put(messages.getString("french"), "fr_fr");
        languageMap.put(messages.getString("english"), "en_us");
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
            this.updateSettingsProperties((int) difficultySlider.getValue(), (int) lifeTimeSlider.getValue(), langageSelected.get());
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
        // Configuration de la fenêtre
        Stage infoStage = new Stage();
        infoStage.setResizable(false);
        infoStage.setTitle(messages.getString("informations"));

        // Ajout d'un label d'information
        Label infoLabel = new Label(messages.getString("gameInfos"));
        infoLabel.getStyleClass().add("label");
        infoLabel.setMinWidth(950);

        // Assemblage de la fenêtre
        Group infoGroup = new Group();
        infoGroup.getChildren().add(infoLabel);
        Scene infoScene = new Scene(infoGroup, 950, 50);
        infoScene.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/tamagoshi/style.css")).toExternalForm());
        infoStage.setScene(infoScene);
        infoStage.show();
    }

    /**
     * Initialise les tamagoshis du jeu avec une fabrique (voir {@link FabriqueTamagoshi}).
     */
    private void initTamagoshis() {
        FabriqueTamagoshi fabriqueTamagoshi = FabriqueTamagoshi.getInstance();
        double screenSize = Screen.getPrimary().getBounds().getWidth();
        double x = 0;
        double y = 0;
        for (int i = 0; i < Integer.parseInt(this.getProps().getProperty("difficulty")); i++) {
            Tamagoshi tamagoshi = fabriqueTamagoshi.generateRandomTamagoshi();
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
     * Initialise la durée de vie des Tamagoshis (durée de la partie) avec {@link Tamagoshi#setLifeTime(int)}.
     */
    private void initLifeTime() {
        try {
            Tamagoshi.setLifeTime(Integer.parseInt(this.getProps().getProperty("lifeTime")));
        } catch (NegativeLifeTimeException e) {
            logger.severe(messages.getString("lifeTimeExceptionMessage"));
        }
    }

    /**
     * Réactive les boutons des tamagoshis sur leur {@link TamaStage}
     */
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

    /**
     * Affiche un message dans la console (voir {@link TamaGameGraphique#console}).
     * @param message Le message à afficher (peut être vide).
     */
    protected void log(String message) {
        Text text = new Text(message + "\n");
        this.log(text);
    }

    /**
     * Affiche un message dans la console (voir {@link TamaGameGraphique#console}).
     * @param message Le message à afficher (peut être vide).
     * @param taillePolice La taille du message.
     */
    protected void log(String message, int taillePolice) {
        Text text = new Text(message + "\n");
        text.setFont(new Font(taillePolice));
        this.log(text);
    }

    /**
     * Affiche un message dans la console (voir {@link TamaGameGraphique#console}).
     * @param message Le message à afficher (peut être vide).
     * @param taillePolice La taille du message.
     * @param couleur La couleur du message.
     */
    protected void log(String message, int taillePolice, Color couleur) {
        Text text = new Text(message + "\n");
        text.setFont(new Font(taillePolice));
        text.setFill(couleur);
        this.log(text);
    }

    /**
     * Ajoute le texte (voir {@link javafx.scene.text.Text}) à la console (voir {@link TamaGameGraphique#console}).
     * @param text Texte ajouté à la console.
     */
    protected void log(Text text) {
        this.getConsole().getChildren().add(text);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    // Getters et setters

    private TextFlow getConsole() {
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
}
