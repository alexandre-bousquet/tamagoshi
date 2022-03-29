package tamagoshi.graphic;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import tamagoshi.tamagoshis.Tamagoshi;

import java.util.Objects;

import static tamagoshi.jeu.TamaGame.messages;

public class TamaPane extends BorderPane {
    private final Tamagoshi tamagoshi;
    private final Label labelMessage;
    private final ImageView imageView;

    public TamaPane(Tamagoshi tamagoshi) {
        Label labelNom = new Label(tamagoshi.getName());
        labelNom.getStyleClass().add("label");
        this.tamagoshi = tamagoshi;
        this.labelMessage = new Label();
        this.imageView = new ImageView();
        this.labelMessage.setWrapText(true);
        this.labelMessage.getStyleClass().add("message");
        this.getTamagoshi().parler();
        this.updatePhase();
        this.setTop(labelNom);
    }

    private void getPhase() {
        int energy = this.getTamagoshi().getEnergy();
        int fun = this.getTamagoshi().getFun();

        if (this.getTamagoshi().getAge() >= Tamagoshi.getLifeTime()) {
            this.getImageView().setImage(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/tamagoshi/images/phase_0.png"))));

        } else if (energy <= 0 || fun <= 0) {
            this.getImageView().setImage(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/tamagoshi/images/phase_4.png"))));

        } else if (energy > 4 && fun > 4) {
            this.getImageView() .setImage(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/tamagoshi/images/phase_1.png"))));

        } else if ((energy <= 4 && fun > 4) || (energy > 4 && fun <= 4)) {
            this.getImageView().setImage(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/tamagoshi/images/phase_2.png"))));

        } else if (energy <= 4 && fun <= 4) {
            this.getImageView().setImage(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/tamagoshi/images/phase_3.png"))));
        }
    }

    protected void manger() {
        this.getTamagoshi().mange();
        this.updateMessage();
    }

    protected void jouer() {
        this.getTamagoshi().joue();
        this.updateMessage();
    }

    protected void updatePhase() {
        this.getPhase();
        this.updatePicture();
        this.updateMessage();
    }

    private void updatePicture() {
        this.getImageView().setFitWidth(400);
        this.getImageView().setFitHeight(400);
        this.setCenter(this.getImageView());
        this.getCenter().getStyleClass().add("customImageView");
    }

    private void updateMessage() {
        if (this.getTamagoshi().getAge() < Tamagoshi.getLifeTime()) {
            this.getLabelMessage().setText(this.getTamagoshi().getMessage());
        } else {
            this.getLabelMessage().setText(messages.getString("thanks"));
        }
        this.setBottom(this.getLabelMessage());
    }

    // Getters et setters

    protected Tamagoshi getTamagoshi() {
        return tamagoshi;
    }

    private ImageView getImageView() {
        return imageView;
    }

    private Label getLabelMessage() {
        return labelMessage;
    }
}
