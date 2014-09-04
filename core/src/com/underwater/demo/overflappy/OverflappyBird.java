package com.underwater.demo.overflappy;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/*
 * This is our ApplicationListener the main thing that things start in libGDX
 */
public class OverflappyBird extends ApplicationAdapter {
	
	/*
	 * GameStage will be holding both menu and main game
	 */
	private GameStage stage;
	
	@Override
	public void create () {
		stage = new GameStage();
	}

	@Override
	public void render () {
		// Clearing the screen before each render
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// calling stage act method and passing delta time passed since last call
		stage.act(Gdx.graphics.getDeltaTime());
		// drawing all actors
		stage.draw();
		
	}
}
