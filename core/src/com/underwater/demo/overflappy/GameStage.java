package com.underwater.demo.overflappy;

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.resources.ResourceManager;

/*
 * GameStage 
 */
public class GameStage extends Stage {
	
	// Speed of pixels per second of how fast things move left (required both for menu and the game, thus put here)
	public float gameSpeed = 200;

	// Overlap2D  provides this easy asset manager that loads things as they are provided by default when exporting from overlap
	private ResourceManager resourceManager;
	
	public GameStage() {
		super();
		
		// Set this is input processor so all actors would be able to listen to touch events
		Gdx.input.setInputProcessor(this);
		
		// Initializing asset manager
        resourceManager = new ResourceManager();
		
		// loading assets into memory
        resourceManager.initAllResources();
		
		// Menu goes first
		initMenu();
	}
	
	public void initMenu() {
		clear();
		
		// Creating Scene loader which can load an Overlap2D scene
		SceneLoader menuLoader = new SceneLoader(resourceManager);
		
		// loading MenuScene.dt from assets folder
		menuLoader.loadScene("MenuScene");
		
		// Initializing iScript MenuSceneScript that will be holding all menu logic, and passing this stage for later use
		MenuScreenScript menuScript = new MenuScreenScript(this);
		
		// adding this script to the root scene of menu which is hold in menuLoader.sceneActor
		menuLoader.sceneActor.addScript(menuScript);
		
		// Adding root actor to stage
		addActor(menuLoader.sceneActor);
		
		
	}
	
	public void initGame() {
		clear();
		
		// Creating Scene loader which can load an Overlap2D scene
		SceneLoader mainLoader = new SceneLoader(resourceManager);
		
		// loading MainScene.dt from assets folder
		mainLoader.loadScene("MainScene");
		
		// Initializing iScript GameSceneScript that will be holding all game, and passing this stage for later use
		GameScreenScript gameScript = new GameScreenScript(this, mainLoader);
		
		// adding this script to the root scene of game which is hold in mainLoader.sceneActor
		mainLoader.sceneActor.addScript(gameScript);
		
		// Adding root actor to stage
		addActor(mainLoader.sceneActor);
	}

}
