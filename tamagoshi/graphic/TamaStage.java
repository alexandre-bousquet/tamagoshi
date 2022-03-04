package tamagoshi.graphic;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TamaStage extends Stage {
    private Group group = new Group();
    private Scene scene;
    private TamaPane tamaPane;
    private Button boutonNourrir;
    private Button boutonJouer;

    public TamaStage(TamaPane tamaPane) {
        this.tamaPane = tamaPane;
        this.initBoutons();
        this.group.getChildren().addAll(tamaPane, this.boutonNourrir, this.boutonJouer);
        this.scene = new Scene(group);
        this.scene.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/tamagoshi/style.css")).toExternalForm());
        this.setScene(this.scene);
        this.setTitle(tamaPane.getTamagoshi().getName());
        this.setResizable(false);
        this.show();
    }

    public void initBoutons() {
        this.boutonNourrir = new Button("Nourrir");
        this.boutonNourrir.getStyleClass().add("customButton");
        this.boutonNourrir.setLayoutX(50);
        this.boutonNourrir.setLayoutY(380);
        this.boutonJouer = new Button("Jouer");
        this.boutonJouer.getStyleClass().add("customButton");
        this.boutonJouer.setLayoutX(250);
        this.boutonJouer.setLayoutY(380);
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

    public void desactiverBoutonNourrir() {
        this.getBoutonNourrir().setDisable(true);
    }

    public void activerBoutonNourrir() {
        this.getBoutonNourrir().setDisable(false);
    }

    public void desactiverBoutonJouer() {
        this.getBoutonNourrir().setDisable(true);
    }

    public void activerBoutonJouer() {
        this.getBoutonNourrir().setDisable(false);
    }
}
