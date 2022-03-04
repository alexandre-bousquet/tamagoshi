package tamagoshi.graphic;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tamagoshi.jeu.TamaGame;
import tamagoshi.tamagoshis.*;

import java.util.*;

public class TamaGameGraphique extends Application {
    private TamaGame tamaGame;

    /**
     * Liste des tamagoshis créer au départ du jeu.
     */
    private List<TamaStage> listeTamagoshisDepart;

    /**
     * Liste des tamagoshis vivants à l'instant T.
     */
    private List<TamaStage> listeTamagoshisEnCours;

    /**
     * Liste des noms possibles pour les tamagoshis.
     */
    private final ArrayList<String> names = new ArrayList<>();

    @Override
    public void start(Stage stage) throws Exception {
        this.tamaGame = new TamaGame();
        this.initNamesList();
        this.listeTamagoshisDepart = new ArrayList<>();
        this.listeTamagoshisEnCours = new ArrayList<>();

        Group root = new Group();
        Scene scene = new Scene(root, 400, 400);
        stage.setTitle("TamaGame");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        for (int i = 0; i < 2; i++) {
            TamaStage tamaStage = new TamaStage(new TamaPane(this.generateRandomTamagoshi()));
            this.listeTamagoshisDepart.add(tamaStage);
            this.listeTamagoshisEnCours.add(tamaStage);
        }
    }

    /**
     * Initialise la liste des noms possibles pour les tamagoshis.
     */
    private void initNamesList() {
        Scanner scan = new Scanner(Objects.requireNonNull(TamaGame.class.getResourceAsStream("/tamagoshi/names.txt")));
        while (scan.hasNextLine()) {
            String nom = scan.nextLine();
            names.add(nom);
        }
        scan.close();
        Collections.shuffle(names);
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

    public TamaGame getTamaGame() {
        return tamaGame;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
