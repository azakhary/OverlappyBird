package com.underwater.demo.overflappy;

import java.util.ArrayList;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AddAction;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.actor.CompositeItem;
import com.uwsoft.editor.renderer.actor.LabelItem;
import com.uwsoft.editor.renderer.script.IScript;

/*
 * iScript for entire game logic
 */
public class GameScreenScript implements IScript {
	
	/*
	 * reference to GameStage
	 */
	private GameStage stage;
	
	/*
	 * Main actor that holds root of game screen
	 */
	private CompositeItem game;
	
	// Screen loader reference to be later used to retrieve prefabs from library
	private SceneLoader loader;
	
	// Game over Dialog actor
	private CompositeItem gameOverDlg;
	
	/*
	 * this will be holding 2-ground system composite item 
	 */
	private CompositeItem groundRotator;
	
	/*
	 * Instead of holding bird actor we are going to hold birdscript that will provide all bird logic and methods.
	 * Also it will have bird actor inside
	 */
	private BirdScript bird;
	
	// Hint Box actor the one that shown in begining of game
	private CompositeItem hintBox;
	
	// some helping booleans
	private boolean gameStarted = false;
	private boolean groundStop = false;
	
	// going to hold what is the possible low and high position for a pipe column
	private float minPipe;
	private float maxPipe;
	
	private int gameScore = 0;
	private LabelItem scoreLbl;
	
	// Going to hold 3 pipes here to reuse as pipe pool
	private ArrayList<CompositeItem> pipes = new ArrayList<CompositeItem>();
	
	public GameScreenScript(GameStage stage, SceneLoader loader) {
		this.stage = stage;
		this.loader = loader;
	}
	
	@Override
	public void init(CompositeItem gameItem) {
		game = gameItem;
		
		gameScore = 0;
		
		// Creating and holding BirdScript that will hold entire bird logic.
		bird = new BirdScript(stage);
		game.getCompositeById("bird").addScript(bird);
		
		groundRotator = game.getCompositeById("groundRotator");
		hintBox = game.getCompositeById("hintBox");
		scoreLbl = game.getLabelById("scoreLbl");
		
		// Adding listeners to listen for taps to make bird jump
		game.addListener(new ClickListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				// screen tap was done
				screenTap();
			}
		});
		
		// Loading min/max positions from level editor
		minPipe = game.getCompositeById("minPipe").getY();
		maxPipe = game.getCompositeById("maxPipe").getY();

		// Retrieving 3 pipe columns from library putting them into array,
		// and adding on screen in minus coordinates so the will becom "availible"
		for(int i = 0;  i < 3; i++) {
			CompositeItem pipe = loader.getLibraryAsActor("pipeGroup");						
			pipe.setX(-pipe.getWidth());			
			game.addItem(pipe);
			
			pipes.add(pipe);
		}
		
		
		// Making sure first pipe will be added not sooner then 3 seconds from now
		game.addAction(Actions.sequence(Actions.delay(3.0f), Actions.run(new Runnable() {
			
			@Override
			public void run() {
				putPipe();
			}
		})));
		
		// hiding game over dialog
		gameOverDlg = game.getCompositeById("gameOverDlg");
		// it should not listen for taps
		gameOverDlg.setTouchable(Touchable.disabled);
		gameOverDlg.setVisible(false);
	}

	@Override
	public void act(float delta) {
		
		// if game is not yet started or started (but most importantly not ended, ground is moving) 
		if(!groundStop) {
			groundRotator.setX(groundRotator.getX() - delta * stage.gameSpeed);		
			if(groundRotator.getX() < -groundRotator.getWidth()/2) groundRotator.setX(0);
		}
		
		// if game is started, so first tap fone, then we dhould check for collisions and move pipes
		if(gameStarted) {
			for(int i = 0; i < pipes.size(); i++) {
				// get pipe
				CompositeItem pipe = pipes.get(i);
				
				// move it if it has positive coordinate
				if(pipe.getX() > -pipe.getWidth()) {
					// if pipe was right of thebird, and will now become left of the bird, add to score
					if(pipe.getX() >= bird.getBirdCenter().x && pipe.getX() - delta * stage.gameSpeed < bird.getBirdCenter().x) {
						gameScore++;
					}
					pipe.setX(pipe.getX() - delta * stage.gameSpeed);
				}
			}

			//check for collision with bird
			collisionCheck();
		}
		
		// update scorel label
		scoreLbl.setText(gameScore+"");
	}
	
	/*
	 * Check for bird versus pipe row collision
	 */
	private void collisionCheck() {
		// iterate through all 3 pipes
		for(int i = 0; i < pipes.size(); i++) {
			CompositeItem pipe = pipes.get(i);
			
			// to make it easy going to think about bird as circle with 5 radius (better use rect though if id had time)
			Vector2 birdPoint = bird.getBirdCenter();
			
			// Is there collision? if yes stop the game and allow bird to fall
			if(birdPoint.x+5 > pipe.getX() && birdPoint.x - 5 < pipe.getX() + pipe.getWidth() && (pipe.getY() + 532 > birdPoint.y - 5 || pipe.getY()+701 < birdPoint.y + 5)) {
				stopGame();	
			}
			
			// Did bird hit the ground? not only stop the game but also 
			// disable gravity to keep from further falling, and consider bird dead ( animations stop )
			if(birdPoint.y-5 < groundRotator.getY()+groundRotator.getHeight()) {
				if(!groundStop) {
					stopGame();
				}
				bird.disableGravity();
				bird.getBird().setY(groundRotator.getY()+groundRotator.getHeight()+5);
				// killitwithfire
				bird.die();
			}
		}
	}
	
	/*
	 * Stops the game
	 */
	private void stopGame() {
		gameStarted = false;
		groundStop = true;
		game.clearActions();
		
		// show end game dialog
		showEndDialog();
	}
	
	/*
	 * showing end game dialog
	 */
	public void showEndDialog() {
		// enabling touch back, showing
		gameOverDlg.setTouchable(Touchable.enabled);
		gameOverDlg.setVisible(true);
		// setting transparency to full
		gameOverDlg.getColor().a = 0;
		// and fading it in
		gameOverDlg.addAction(Actions.fadeIn(0.4f));
		
		// setting play button listener to replay the game
		gameOverDlg.getImageById("playBtn").addListener(new ClickListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				stage.initGame();
			}
		});
	}
	
	/*
	 * Called when screen is tapped
	 */
	private void screenTap() {
		// if ground is not moving then bird is dead no actin required on tapp
		if(groundStop) return;
		
		// if game started just jump the bird
		if(gameStarted) {
			bird.jump();
		} else {
			// if game is not yet started, start the game and jump the bird
			gameStarted = true;
			hintBox.addAction(Actions.fadeOut(0.3f));
			// and also enable gravity from now on
			bird.enableGravity();
			bird.jump();
		}
	}
	
	/*
	 * get's availible pipe
	 * availible pipe is any pipe that is left of screen and not visible
	 */
	public CompositeItem getAvailablePipe() {
		for(int i = 0; i < pipes.size(); i++) {
			if(pipes.get(i).getX() <= -pipes.get(i).getWidth()) {
				return pipes.get(i);
			}
		}
		
		return null;
	}
	
	/*
	 * this is called every X time to put a new pipe on the right
	 */
	public void putPipe() { 
		// getting availible pipe
		CompositeItem pipe = getAvailablePipe();
		
		// when you die at bad moment, it can be null sometimes
		if(pipe == null) return;
		
		// put pipe column on the random hight from min to max range
		pipe.setX(stage.getWidth());
		pipe.setY(MathUtils.random(minPipe, maxPipe));
		
		// schedule next pipe to be put in 1.3 seconds
		game.addAction(Actions.sequence(Actions.delay(1.3f), Actions.run(new Runnable() {
			
			@Override
			public void run() {
				// call itself
				putPipe();
			}
		})));
	}

    @Override
    public void dispose() {

    }

}
