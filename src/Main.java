import engine.GameContainer;
import game.GameManager;
public class Main {
    public static void main(String[] args) {
        GameContainer gc = new GameContainer(new GameManager());
        gc.start();
    }
}