package Game.Menu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.Game.Engine.GameContainer;
import com.Game.Engine.Renderer;
import com.Game.Engine.gfx.buffer.ImageBuffer;
import com.Game.Enums.Mod;

import Game.GameManager;
import Game.ProgrammObject;
import Game.Menu.SympleTypes.ComplexButton;
import Game.Menu.SympleTypes.SimpleButton;
import Game.Menu.SympleTypes.SimpleFirstMenu;
import Game.Menu.SympleTypes.SimpleMenu;
import Game.Menu.SympleTypes.SimpleRelButton;
import Game.Menu.SympleTypes.SimpleSecondMenu;
import Game.Saves.ConfigStorage;
import Game.Saves.Storage;

public class SimpleMenuManager extends ProgrammObject {
	
	private HashMap<Menus, SimpleMenu> menus;
	
	private SimpleMenu activeScene;
	private Menus backUpScene;
	private boolean turnedOn, background;
	
	protected int mouseX, mouseY;
	
	public SimpleMenuManager() {
		turnedOn = true;
		background = true;
		menus = new HashMap<Menus, SimpleMenu>();
		fill();
	}
	
	@Override
	public void update(GameContainer gc, GameManager gm, float dt){
		activeScene.update(gc, gm, this);
	}

	@Override
	public void render(GameContainer gc, Renderer r) {
			try {
				if(activeScene != null)
					activeScene.render(gc, r);
			}catch(NullPointerException e) {
				updateScene(Menus.PauseMenu);
				turn();
			}
	}	
	
	private void fill() {
		/*Basic buttons*/
		SimpleButton exitButton = new SimpleButton("Exit") {
			public void act(GameManager gm, GameContainer gc, SimpleMenuManager menuManager) {
				gc.dispose();
			}
		};
		
		SimpleButton settings = new SimpleButton("Settings") {
			public void act(GameManager gm, GameContainer gc, SimpleMenuManager menuManager) {
				updateScene(Menus.Settings);
			}
		};
		
		/*Temp menu. 
		 * 1.Set up buttons 
		 * 2. add to scene*/

		SimpleMenu tempMenu = null;
		
		/*MAIN MENU*/
		tempMenu = new SimpleFirstMenu(Menus.MainMenu);
		
		tempMenu.addButton(new SimpleRelButton("WR", 0.8f, 0.8f, 4, 2) {
			 
			@Override
			public void act(GameManager gm, GameContainer gc, SimpleMenuManager menuManager) {
				updateScene(Menus.WorldCreater);
			}
			});
		
		tempMenu.addButton(new SimpleButton("Start Game") {
			
			public void act(GameManager gm, GameContainer gc, SimpleMenuManager menuManager) {
				if(!gm.isGameStarted()){
					gm.startNewGame();
					backUpScene = Menus.PauseMenu;
					updateScene(Menus.PauseMenu);
					turnedOn = false;
				}
			}
			});
		
		tempMenu.addButton(new SimpleButton("Load") {
				public void act(GameManager gm, GameContainer gc, SimpleMenuManager menuManager) {	
					updateScene(Menus.LoadSave);
				}
			});
		
		tempMenu.addButton(settings);
		tempMenu.addButton(exitButton);
		
		menus.put(Menus.MainMenu, tempMenu);
		
		/*PAUSE MENU*/
		tempMenu = new SimpleFirstMenu(Menus.PauseMenu);
		
		tempMenu.addButton(new SimpleButton("Back to game") {
				public void act(GameManager gm, GameContainer gc, SimpleMenuManager menuManager) {
					turnedOn = false;
				}
			});
		
		tempMenu.addButton(settings);
		
		tempMenu.addButton(new SimpleButton("Main Menu") {
			public void act(GameManager gm, GameContainer gc, SimpleMenuManager menuManager) {
				backUpScene = Menus.MainMenu;
				updateScene(Menus.MainMenu);
				gm.stopGame();
			}
			});

		tempMenu.addButton(exitButton);
		
		menus.put(Menus.PauseMenu, tempMenu);
		
		/*SETTINGS*/
		tempMenu = new SimpleSecondMenu(Menus.Settings) {
				
			public void refresh() {
				super.refresh();
				SimpleButton back = paragraphs.get(paragraphs.size() - 1);
				paragraphs.clear();
				
				addButton(new SimpleButton("Menu Visibility") {
					public void act(GameManager gm, GameContainer gc, SimpleMenuManager menuManager) {
						background = !background;
					}
					@Override
					public int getColor() {
						if(background)
							return 0xffa0a268;
						return super.getColor();
					}
				});
				
				ComplexButton compBtn = new ComplexButton("Resolution");
				
				try {
					@SuppressWarnings("resource")
					BufferedReader br = new BufferedReader(new FileReader(GameContainer.getMainPath() + "settings/preps/ResolutionButtons.cfg"));
					
					String line_ = "", 
						   temp = br.readLine();
					
					while(temp != null) {
						line_ += temp + "\n";
						temp = br.readLine();
					}
					
					for(String line : line_.split("\n")){
						
						int w = Integer.parseInt(line.split("x")[0]);
						int h = Integer.parseInt(line.split("x")[1]);
						float f = (((float) 900/h) + ((float) 1600/w))/2;
						int t = (int) Math.floor(w/25);
						compBtn.addSubButton(new SimpleButton(line) {
		
							public void act(GameManager gm, GameContainer gc, SimpleMenuManager menuManager) {
								gc.setBounds(w, h, f);
								gm.setTS(t);
								ImageBuffer.resize();
								gc.refresh();
								menuManager.activeScene.refresh();
								ConfigStorage.updateConfig();
							}
							
							public int getColor() {
								if(GameContainer.getWidth() == w && GameContainer.getHeight()==h)
									return 0xffa0a268;
								return super.getColor();
							}
						});
					}
				} catch (IOException e) {
					GameContainer.addError(e);
					e.printStackTrace();
				}
				
				addButton(compBtn);
				
				compBtn = new ComplexButton("Scale");
				
				try {
					@SuppressWarnings("resource")
					BufferedReader br = new BufferedReader(new FileReader(GameContainer.getMainPath()+"settings/preps/ScaleButtons.cfg"));
					String line_ = "", 
						   temp = br.readLine();
					while(temp != null) {
						line_ += temp + "\n";
						temp = br.readLine();
					}
					
					for(String line : line_.split("\n")){
						compBtn.addSubButton(new SimpleButton(line) {
		
							public void act(GameManager gm, GameContainer gc, SimpleMenuManager menuManager) {
								gc.setScale(Float.parseFloat(getName()));
								gc.refresh();
								menuManager.activeScene.refresh();
							}
						});
					}
					
				} catch (IOException e) {
					GameContainer.addError(e);
					e.printStackTrace();
				}
				
				addButton(compBtn);
				addButton(back);
			}
		};
		
		menus.put(Menus.Settings, tempMenu);
		
		/*LOAD SAVE*/
		tempMenu = new SimpleSecondMenu(Menus.LoadSave) {
			
			@Override
			public void refresh() {
				super.refresh();
				SimpleButton back = paragraphs.get(paragraphs.size() - 1);
				paragraphs.clear();
				
		        ComplexButton tempButton = null;
		        
		        File folder = new File(GameContainer.getMainPath() + "saves");
		        if(!folder.exists()) {
		        	folder.mkdir();
		        }
		        
		        File[] listOfFiles = folder.listFiles();
		        
		        for(int i = 0; i < listOfFiles.length; i++) {
		        	tempButton = new ComplexButton(listOfFiles[i].getName().substring(0, listOfFiles[i].getName().length()-5)) {

		        		@Override
		        		public void act(GameManager gm, GameContainer gc, SimpleMenuManager menuManager) {
		        			Storage.loadSave(name, gm, GameContainer.getMainPath() + "saves/" + getName() + ".save");
		        			backUpScene = Menus.PauseMenu;
							updateScene(Menus.PauseMenu);
							turnedOn = false;
		        		}
		        	};

        			String tempName;
		        	if(tempButton.getName().length() >= 3)
        				tempName = tempButton.getName().substring(0,3);
        			else
        				tempName = tempButton.getName();
		        	
		        	tempButton.addSubButton(new SimpleButton("Delete " + tempName + "...", tempButton.getName()) {
        				
        				@Override
		        		public void act(GameManager gm, GameContainer gc, SimpleMenuManager menuManager) {			        					
		        			Storage.deleteSave(GameContainer.getMainPath() + "saves/" + getCommand() + ".save");
        					deleteButton(getCommand());
		        		}
        			});
		        	addButton(tempButton);
		        }
		        
				addButton(back);
			}
		};
		
		((SimpleSecondMenu) tempMenu).setSubOffY((int) (GameContainer.getHeight()/4));
		
		menus.put(Menus.LoadSave, tempMenu);
		
		/*World Creater*/
		
		tempMenu = new SimpleSecondMenu(Menus.WorldCreater) {
			
			@Override
			public void refresh() {
				super.refresh();
				SimpleButton back = paragraphs.get(0);
				paragraphs.clear();
				
				File folder = new File(GameContainer.getMainPath() + "/levels/building/");
		        if(folder.exists()) {
			        File[] listOfFiles = folder.listFiles();
			        ComplexButton tempButton = null;
			        for(int i = 0; i < listOfFiles.length; i++) {
			    		if(listOfFiles[i].getName().substring(listOfFiles[i].getName().length()-3).equals("png")) {
				        	tempButton = new ComplexButton(listOfFiles[i].getName().substring(0, listOfFiles[i].getName().length()-4)) {
				        		
				        		@Override
				        		public void act(GameManager gm, GameContainer gc, SimpleMenuManager menuManager) {
				        			backUpScene = Menus.PauseMenu;
									updateScene(Menus.PauseMenu);
									turnedOn = false;
				        			gm.loadGameLevel(GameContainer.getMainPath() + "levels/building/" + getName(), Mod.creater);
				        		}

				        		@Override
				    			public void refresh() {
				        			subButtons.clear();
				        			String tempName ;
				        			if(getName().length() >= 3)
				        				tempName = getName().substring(0,3);
				        			else
				        				tempName = getName();
						        	
						        	addSubButton(new SimpleButton("Delete " + tempName + "...", getName()){
				        				@Override
						        		public void act(GameManager gm, GameContainer gc, SimpleMenuManager menuManager) {
						        			Storage.deleteSave(GameContainer.getMainPath() + "levels/building/" + getCommand() +".png");
						        			deleteButton(getCommand());
						        		}
				        			});
				        		}
				        	};
				        	addButton(tempButton);
				        }
			        }
					super.refresh();
		        }

				addButton(back);
		    }
		};
		
		menus.put(Menus.WorldCreater, tempMenu);
		/*END*/
		backUpScene = Menus.MainMenu;
		activeScene = menus.get(Menus.MainMenu);
		activeScene.refresh();
	}
	
	public void updateScene(Menus name) {
			SimpleMenu scene = menus.get(name);
			
			if(scene instanceof SimpleSecondMenu && activeScene.getEnumName() != name) {
				((SimpleSecondMenu) (scene)).changeBackButton(activeScene.getEnumName());
			}
			
			if((scene != null && !activeScene.equals(scene)) || name == Menus.PauseMenu || name == Menus.LoadSave){
				activeScene = scene;
				activeScene.refresh();
			}else {
				updateScene(backUpScene);
			}
	}
	
	//for external scenes such as machine interfaces
	public void updateScene(SimpleMenu scene) {
			if((scene != null && !activeScene.equals(scene)) || scene.getEnumName().equals(Menus.PauseMenu)){
				activeScene = scene;
				activeScene.refresh();
			}else
				updateScene(backUpScene);
	}
	
	/**
	 * Changes current status to opposite
	 */
	public void turn() {
		turnedOn = !turnedOn;
	}
	
	public boolean isTurnedOn() {
		return turnedOn;
	}
	

	public void setTurnedOn(boolean turnedOn) {
		this.turnedOn = turnedOn;
	}

	public boolean isBackground() {
		return background;
	}
}