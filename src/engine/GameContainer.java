package engine;

public class GameContainer implements Runnable {
	private Thread thread;

	private AbstractGame game;
	private Input input;
	private Window window;
	private Renderer renderer;

	private boolean running = false;

	private final double UPDATE_CAP = 1.0 / 60.0;

	// -10 because of taskbar
	private int hdWidth = 1920, hdHeight = 1080 - 30;

	private float scale = 2f;
	private int width = (int) (hdWidth / scale), height = (int) (hdHeight / scale);
	private String title = "My Game";

	public GameContainer(AbstractGame game) {
		this.game = game;
	}

	public void start() {
		window = new Window(this);
		renderer = new Renderer(this);
		input = new Input(this);
		thread = new Thread(this);
		thread.run();
	}

	public void run() {
		running = true;

		boolean render = false;

		double firstTime = 0;
		double passedTime = 0;
		// Get ns precision time in seconds
		double lastTime = System.nanoTime() / 1_000_000_000.0;

		double unprocessedTime = 0;

		double frameTime = 0;
		int frames = 0;
		int fps = 0;

		game.init(this);

		while(running) {
			render = false;

			firstTime = System.nanoTime() / 1_000_000_000.0;
			passedTime = firstTime - lastTime;
			lastTime = firstTime;

			unprocessedTime += passedTime;
			frameTime += passedTime;

			while(unprocessedTime >= UPDATE_CAP) {
				unprocessedTime -= UPDATE_CAP;

				render = true;

				game.update(this, (float) UPDATE_CAP);

				input.update();

				if(frameTime >= 1.0) {
					frameTime = 0;
					fps = frames;
					frames = 0;
				}
			}

			if(render) {
				renderer.clear();
				game.render(this, renderer);
				renderer.process();

				renderer.setCamX(0);
				renderer.setCamY(0);
				//renderer.drawText("FPS: " + fps, 0, 0, 0xff00ffff);
				
				window.update();

				frames++;
			} else {
				try {
					Thread.sleep(1);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Window getWindow() {
		return window;
	}

	public Input getInput() {
		return input;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public float getScale() {
		return scale;
	}

	public String getTitle() {
		return title;
	}

	public Renderer getRenderer() {
		return renderer;
	}
}