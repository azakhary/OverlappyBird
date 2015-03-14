package com.underwater.demo.overflappy;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.uwsoft.editor.renderer.actor.CompositeItem;
import com.uwsoft.editor.renderer.actor.SpriteAnimation;
import com.uwsoft.editor.renderer.script.IScript;

/**
 * Bird Script
 * @author azakhary
 * This is brain of the bird, it's physics and everything
 */
public class BirdScript implements IScript {

	/*
	 * reference to GameStage
	 */
	private GameStage stage;
	
	// Bird composite item actor
	private CompositeItem bird;
	
	// Inside bird composite actor there is the bird sprite animation actor
	private SpriteAnimation birdAnimation;
	
	// used to wiggle the bird in the air using Sine function
	private float iterator = 0;
	
	// current vertical velocity of the bird
	private float currVerticalVelocity = 0;
	
	// boolean to know if gravity is enabled or not
	private boolean isGravityEnabled = false;
	
	// to avoid jumping to rotation bird will try to always rotate a bit towards desired rotation
	private float desiredRotation;
	
	// is it alive?
	private boolean isAlive = true;
	
	
	public BirdScript(GameStage stage) {
		this.stage = stage;
	}
	
	@Override
	public void init(CompositeItem item) {
		bird = item;
		
		// find animation from the composite
		birdAnimation = bird.getSpriteAnimationById("birdAnimation");
		
		// set origin of the bird in it's center, so it will rotate normally
		bird.setOrigin(bird.getWidth()/2, bird.getHeight()/2);
		
		// set desired rotation to current rotation which is 0
		desiredRotation = bird.getRotation();
	}

	@Override
	public void act(float delta) {
		
		if(!isGravityEnabled && isAlive) {
			// Wiggling when no gravity only
			iterator += delta*400;
			float birdOffset = MathUtils.sinDeg(iterator)*5;
			birdAnimation.setY(birdOffset);
		}
		
		// aplying gravity every frame
		gravity(delta);
		
		// moving to new position based on current vertical velocity
		bird.setY(bird.getY() + delta*currVerticalVelocity);
		
		// manage bird rotation based on it's vertical speed
		manageRotation(delta);
		
	}
	
	/*
	 * manage bird rotation based on it's vertical speed
	 * this is a part of code that is not interesting boring and whatever..
	 */
	private void manageRotation(float delta) {
		if(isGravityEnabled) {
			if(currVerticalVelocity > -200) {
				float rotation = currVerticalVelocity+200;
				desiredRotation = rotation/15f;
			}
			if(currVerticalVelocity <= -200) {
				float rotation = currVerticalVelocity+200;
				if(rotation < -400) rotation = -400;
				desiredRotation = rotation/4.4f;
			}
			
			if(desiredRotation != bird.getRotation()) {
				if(desiredRotation > bird.getRotation()) {
					bird.setRotation(bird.getRotation() + 900*delta);
					if(desiredRotation < bird.getRotation()) bird.setRotation(desiredRotation);
				}
				if(desiredRotation < bird.getRotation()) {
					bird.setRotation(bird.getRotation() - 900*delta);
					if(desiredRotation > bird.getRotation()) bird.setRotation(desiredRotation);
				}
			}
		}
	}
	
	public void enableGravity() {
		isGravityEnabled = true;
	}
	
	public void disableGravity() {
		isGravityEnabled = false;
	}
	
	public void jump() {
		// if bird is dead do not jump (I think I checked it somewhere already)
		if(!isAlive) return;
		
		// if bird is higher then screen then do not jump
		if(bird.getY() > stage.getHeight()) return;
		
		// if jumped get the custom variable jump_speed from bird actor and set it as current vertical velocity
		currVerticalVelocity = bird.getCustomVariables().getFloatVariable("jump_speed");
	}
	
	/*
	 * Apply gravity each frame (get's delta time since last frame)
	 */
	private void gravity(float delta) {
		if(isGravityEnabled) {
			// change curernt velocity based on gravity (gravity changes velocity every second by gravity amount)
			currVerticalVelocity -= delta*bird.getCustomVariables().getFloatVariable("gravity");
		}
	}
	
	public CompositeItem getBird() {
		return bird;
	}
	
	// get's the bird center coordinates as vector 2 needed for collision detection in GameScreenScript
	public Vector2 getBirdCenter() {
		Vector2 vec = new Vector2(bird.getX() + bird.getWidth()/2, bird.getY() + bird.getHeight()/2);
		return vec;
	}
	
	// Kills the bird, for reals
	public void die() {
		currVerticalVelocity = 0;	
		isAlive = false;
		desiredRotation = 0;	
		bird.setRotation(0);
		birdAnimation.pause();
	}

    @Override
    public void dispose() {

    }

}
