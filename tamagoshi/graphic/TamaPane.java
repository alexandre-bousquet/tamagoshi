package tamagoshi.graphic;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import tamagoshi.tamagoshis.Tamagoshi;

import java.util.Objects;

public class TamaPane extends BorderPane {
    private final Tamagoshi tamagoshi;
    private String message;
    private final Label labelNom;
    private ImageView imageView;

    public TamaPane(Tamagoshi tamagoshi) {
        this.tamagoshi = tamagoshi;
        this.labelNom = new Label(tamagoshi.getName());
        this.labelNom.getStyleClass().add("label");
        this.updatePhase();
        this.setTop(this.labelNom);
        this.setBottom(new Text(message));
    }

    public void getPhase() {
        if (this.getTamagoshi().getEnergy() > 4 && this.getTamagoshi().getFun() > 4) {
            this.imageView = new ImageView(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/tamagoshi/images/phase_1.jpg"))));
        } else if (this.getTamagoshi().getEnergy() <= 4 && this.getTamagoshi().getFun() <= 4) {
            this.imageView = new ImageView(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/tamagoshi/images/phase_3.png"))));
        } else if (this.getTamagoshi().getEnergy() <= 4 || this.getTamagoshi().getFun() <= 4) {
            this.imageView = new ImageView(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/tamagoshi/images/phase_2.jpg"))));
        } else if (this.getTamagoshi().getEnergy() <= 0 || this.getTamagoshi().getFun() <= 0) {
            this.imageView = new ImageView(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/tamagoshi/images/phase_4.png"))));
        }
    }

    public void manger() {
        this.getTamagoshi().mange();
        this.updatePhase();
    }

    public void jouer() {
        this.getTamagoshi().joue();
        this.updatePhase();
    }

    public void updatePhase() {
        this.getPhase();
        this.updatePicture();
        this.updateMessage();
    }

    public void updatePicture() {
        this.getImageView().setFitWidth(400);
        this.getImageView().setFitHeight(400);
        this.setCenter(this.getImageView());
    }

    public void updateMessage() {
        this.setBottom(new Text(this.getMessage()));
    }

    // Getters et setters

    public Tamagoshi getTamagoshi() {
        return tamagoshi;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
