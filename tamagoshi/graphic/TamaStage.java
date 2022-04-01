package tamagoshi.graphic;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.Objects;
import tamagoshi.tamagoshis.Tamagoshi;

import static tamagoshi.jeu.TamaGame.messages;

/**
 * Classe qui gère les {@link TamaPane} ainsi que les boutons {@link TamaStage#boutonNourrir} et {@link TamaStage#boutonJouer} qui permettent de les nourrir ou de jouer avec eux.
 */
public class TamaStage extends Stage {
    /**
     * {@link TamaGameGraphique} auquel appartient le {@link TamaStage}
     */
    private final TamaGameGraphique tamaGameGraphique;

    /**
     * {@link TamaPane} contenant un {@link Tamagoshi} aléatoire généré par une fabrique.
     */
    private final TamaPane tamaPane;

    /**
     * Bouton permettant de nourrir le {@link Tamagoshi} de {@link TamaStage#tamaPane}.
     */
    private Button boutonNourrir;

    /**
     * Bouton permettant de jouer avec le {@link Tamagoshi} de {@link TamaStage#tamaPane}.
     */
    private Button boutonJouer;

    /**
     * Constructeur de la classe {@link TamaStage}.
     * @param tamaPane {@link TamaPane} contenant un {@link Tamagoshi} aléatoire généré par une fabrique.
     * @param tamaGameGraphique {@link TamaGameGraphique} auquel appartient le {@link TamaStage}.
     */
    public TamaStage(TamaPane tamaPane, TamaGameGraphique tamaGameGraphique) {
        this.tamaGameGraphique = tamaGameGraphique;
        this.tamaPane = tamaPane;
        this.initBoutons();
        Group group = new Group();
        group.getChildren().addAll(tamaPane, this.boutonNourrir, this.boutonJouer);
        Scene scene = new Scene(group);
        scene.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/tamagoshi/style.css")).toExternalForm());
        this.setOnCloseRequest(ev -> Platform.exit());
        this.setScene(scene);
        this.setTitle(tamaPane.getTamagoshi().getName());
        this.setResizable(false);
        this.show();
    }

    /**
     * Initialise les boutons pour nourrir et jouer avec le tamagoshi.
     */
    private void initBoutons() {
        this.boutonNourrir = new Button(messages.getString("feed"));
        this.boutonNourrir.getStyleClass().add("customButton");
        this.boutonNourrir.setLayoutX(50);
        this.boutonNourrir.setLayoutY(380);
        this.boutonNourrir.setOnAction(actionEvent -> this.nourrir());
        this.boutonJouer = new Button(messages.getString("play"));
        this.boutonJouer.getStyleClass().add("customButton");
        this.boutonJouer.setLayoutX(250);
        this.boutonJouer.setLayoutY(380);
        this.boutonJouer.setOnAction(actionEvent -> this.jouer());
    }

    /**
     * Nourri le tamagoshi du TamaPane (voir {@link TamaPane#getTamagoshi()}) et affiche dans la console qu'on la nourrit.
     */
    private void nourrir() {
        this.getTamaPane().manger();
        for (TamaStage tamaStage : this.getTamaGameGraphique().getListeTamaStage()) {
            tamaStage.desactiverBoutonNourrir();
        }
        this.getTamaGameGraphique().log(messages.getString("youFed") + " " + this.getTamaPane().getTamagoshi().getName(), 15, Color.BLACK);
        this.getTamaGameGraphique().setPeutNourrir(false);
        this.getTamaGameGraphique().prepareNextCycle();
    }

    /**
     * Joue avec le tamagoshi du TamaPane (voir {@link TamaPane#getTamagoshi()}) et affiche dans la console qu'on a joué avec lui.
     */
    private void jouer() {
        this.getTamaPane().jouer();
        for (TamaStage tamaStage : this.getTamaGameGraphique().getListeTamaStage()) {
            tamaStage.desactiverBoutonJouer();
        }
        this.getTamaGameGraphique().log(messages.getString("youPlayedWith") + " " + this.getTamaPane().getTamagoshi().getName(), 15, Color.BLACK);
        this.getTamaGameGraphique().setPeutJouer(false);
        this.getTamaGameGraphique().prepareNextCycle();
    }

    /**
     * Désactive le bouton {@link TamaStage#boutonNourrir}.
     */
    private void desactiverBoutonNourrir() {
        this.getBoutonNourrir().setDisable(true);
    }

    /**
     * Active le bouton {@link TamaStage#boutonNourrir}.
     */
    protected void activerBoutonNourrir() {
        this.getBoutonNourrir().setDisable(false);
    }

    /**
     * Désactive le bouton {@link TamaStage#boutonJouer}.
     */
    private void desactiverBoutonJouer() {
        this.getBoutonJouer().setDisable(true);
    }

    /**
     * Active le bouton {@link TamaStage#boutonJouer}.
     */
    protected void activerBoutonJouer() {
        this.getBoutonJouer().setDisable(false);
    }

    // Getters et setters

    protected TamaPane getTamaPane() {
        return this.tamaPane;
    }

    private Button getBoutonNourrir() {
        return this.boutonNourrir;
    }

    private Button getBoutonJouer() {
        return this.boutonJouer;
    }

    private TamaGameGraphique getTamaGameGraphique() {
        return tamaGameGraphique;
    }
}
