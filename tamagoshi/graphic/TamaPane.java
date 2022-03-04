package tamagoshi.graphic;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import tamagoshi.tamagoshis.Tamagoshi;

import java.util.Objects;

public class TamaPane extends BorderPane {
    private final Tamagoshi tamagoshi;
    private Image image;
    private String message;
    private Button boutonNourrir;
    private Button boutonJouer;
    private final Label labelNom;
    private ImageView imageView;

    public TamaPane(Tamagoshi tamagoshi) {
        this.tamagoshi = tamagoshi;
        this.labelNom = new Label(tamagoshi.getName());
        this.imageView = new ImageView(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("tamagoshi/images/phase_1.png"))));
        this.setTop(labelNom);
        this.setCenter(this.imageView);
    }

    public void getPhase() {
        if (tamagoshi.getEnergy() > 4 && tamagoshi.getFun() > 4) {
            this.imageView = new ImageView(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("tamagoshi/images/phase_1.png"))));
        } else if (tamagoshi.getEnergy() <= 4 || tamagoshi.getFun() <= 4) {
            this.imageView = new ImageView(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("tamagoshi/images/phase_2.png"))));
        } else if (tamagoshi.getEnergy() <= 4 && tamagoshi.getFun() <= 4) {
            this.imageView = new ImageView(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("tamagoshi/images/phase_3.png"))));
        } else if (tamagoshi.getEnergy() <= 0 || tamagoshi.getFun() <= 0) {
            this.imageView = new ImageView(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("tamagoshi/images/phase_4.png"))));
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
