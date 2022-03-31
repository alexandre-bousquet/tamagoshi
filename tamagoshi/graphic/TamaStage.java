package tamagoshi.graphic;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.Objects;

import static tamagoshi.jeu.TamaGame.messages;

public class TamaStage extends Stage {
    private final TamaGameGraphique tamaGameGraphique;
    private final TamaPane tamaPane;
    private Button boutonNourrir;
    private Button boutonJouer;

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

    private void nourrir() {
        this.getTamaPane().manger();
        for (TamaStage tamaStage : this.getTamaGameGraphique().getListeTamaStage()) {
            tamaStage.desactiverBoutonNourrir();
        }
        this.getTamaGameGraphique().log(messages.getString("youFed") + " " + this.getTamaPane().getTamagoshi().getName(), 15, Color.BLACK);
        this.getTamaGameGraphique().setPeutNourrir(false);
        this.getTamaGameGraphique().prepareNextCycle();
    }

    private void jouer() {
        this.getTamaPane().jouer();
        for (TamaStage tamaStage : this.getTamaGameGraphique().getListeTamaStage()) {
            tamaStage.desactiverBoutonJouer();
        }
        this.getTamaGameGraphique().log(messages.getString("youPlayedWith") + " " + this.getTamaPane().getTamagoshi().getName(), 15, Color.BLACK);
        this.getTamaGameGraphique().setPeutJouer(false);
        this.getTamaGameGraphique().prepareNextCycle();
    }

    private void desactiverBoutonNourrir() {
        this.getBoutonNourrir().setDisable(true);
    }

    protected void activerBoutonNourrir() {
        this.getBoutonNourrir().setDisable(false);
    }

    private void desactiverBoutonJouer() {
        this.getBoutonJouer().setDisable(true);
    }

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
