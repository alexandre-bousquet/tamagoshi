package tamagoshi.graphic;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.util.Objects;

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
        this.setScene(scene);
        this.setTitle(tamaPane.getTamagoshi().getName());
        this.setResizable(false);
        this.show();
    }

    public void initBoutons() {
        this.boutonNourrir = new Button("Nourrir");
        this.boutonNourrir.getStyleClass().add("customButton");
        this.boutonNourrir.setLayoutX(50);
        this.boutonNourrir.setLayoutY(380);
        this.boutonNourrir.setOnAction(actionEvent -> this.nourrir());
        this.boutonJouer = new Button("Jouer");
        this.boutonJouer.getStyleClass().add("customButton");
        this.boutonJouer.setLayoutX(250);
        this.boutonJouer.setLayoutY(380);
        this.boutonJouer.setOnAction(actionEvent -> this.jouer());
    }

    public void nourrir() {
        this.getTamaPane().manger();
        for (TamaStage tamaStage : this.tamaGameGraphique.getListeTamaStage()) {
            tamaStage.desactiverBoutonNourrir();
        }
    }

    public void jouer() {
        this.getTamaPane().jouer();
        for (TamaStage tamaStage : this.tamaGameGraphique.getListeTamaStage()) {
            tamaStage.desactiverBoutonJouer();
        }
    }

    public void desactiverBoutonNourrir() {
        this.getBoutonNourrir().setDisable(true);
    }

    public void activerBoutonNourrir() {
        this.getBoutonNourrir().setDisable(false);
    }

    public void desactiverBoutonJouer() {
        this.getBoutonJouer().setDisable(true);
    }

    public void activerBoutonJouer() {
        this.getBoutonJouer().setDisable(false);
    }

    public TamaPane getTamaPane() {
        return this.tamaPane;
    }

    public Button getBoutonNourrir() {
        return this.boutonNourrir;
    }

    public Button getBoutonJouer() {
        return this.boutonJouer;
    }
}
