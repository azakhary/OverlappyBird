package com.underwater.demo.overflappy;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.uwsoft.editor.renderer.actor.CompositeItem;
import com.uwsoft.editor.renderer.actor.ImageItem;
import com.uwsoft.editor.renderer.actor.SpriteAnimation;
import com.uwsoft.editor.renderer.script.IScript;

/*
 * iScript for menu logic
 */
public class MenuScreenScript implements IScript {
	
	/*
	 * reference to GameStage
	 */
	private GameStage stage;
	
	/*
	 * this is the main root menu actor to work with
	 */
	private CompositeItem menu;
	
	/*
	 * this will be holding 2-ground system composite item 
	 */
	private CompositeItem groundRotator;
	
	/*
	 * this will be the bird sprite animation displayed in center of screen
	 */
	private SpriteAnimation bird;
	
	// this variables are used to wiggle bird up and down with sin function
	private float iterator = 0;
	private float birdInitialPos;
	
	public MenuScreenScript(GameStage stage) {
		this.stage = stage;
	}

	public void init(CompositeItem menuItem) {
		menu = menuItem;
		
		// Finding playButton by id and storing in variable
		ImageItem playBtn = menuItem.getImageById("playBtn");
		
		// Finding ground composite and storing in variable 
		groundRotator = menuItem.getCompositeById("groundRotator");
		
		// Finding bird and storing in variable
		bird = menuItem.getSpriteAnimationById("bird");
		
		// let's remember where bird was initially
		birdInitialPos = bird.getY();
		
		// Adding a Click listener to playButton so we can start game when clicked
		playBtn.addListener(new ClickListener() {
			// Need to keep touch down in order for touch up to work normal (libGDX awkwardness)
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				// when finger is up, ask stage to load the game
				stage.initGame();
			}
		});
	}
	
	/*
	 * This is called every frame
	 */
	public void act(float delta) {
		// moving ground left with game speed multiplied by delta as delta shows what part of second was passed since last call
		groundRotator.setX(groundRotator.getX() - delta * stage.gameSpeed);		
		
		// if ground rotator got half way left, we can just put it back to 0, and to eye it will look like it endlessly moves
		if(groundRotator.getX() < -groundRotator.getWidth()/2) groundRotator.setX(0);
		
		// Now this part is to wiggle bird up and down, we are going change iterator based on time passed
		iterator += delta*400;
		
		// Then figure out the bird offset from it's original position based on iterator which is based on time passed, and do it with sinus function
		float birdOffset = MathUtils.sinDeg(iterator)*5;
		
		// put bird on it's original pos + offset
		bird.setY(birdInitialPos + birdOffset); 
	}

    @Override
    public void dispose() {

    }
	
}
