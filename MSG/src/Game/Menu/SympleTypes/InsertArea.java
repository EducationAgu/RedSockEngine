package Game.Menu.SympleTypes;

import com.Game.Engine.GameContainer;
import com.Game.Engine.Renderer;

import Game.GameManager;
import Game.Menu.SimpleMenuManager;

public class InsertArea extends SimpleButton{

	protected boolean focused;
	protected String lastButton;
	private int pointerChanger = 0, pointerChangerMax = 60;
	private int size = 9, pointer = 0;
	private int pointerX; 
	public InsertArea() {
		super("");
		name = "";
		setUpColors();
	}
	
	public InsertArea(int innerColor, int outerColor) {
		super("");
		name = "";
		this.colorInner = innerColor;
		this.colorOuter = outerColor;
	}
	
	public InsertArea(int x, int y, int w, int h, int innerColor, int outerColor) {
		super("");
		name = "";
		this.x = x;
		this.y = y;
		width = w;
		height = h;
		this.colorInner = innerColor;
		this.colorOuter = outerColor;
	}
	
	@Override
	public void update(int mouseX, int mouseY, boolean mouseManaging, GameContainer gc, GameManager gm) {
		super.update(mouseX, mouseY, mouseManaging, gc, gm);
		if(focused) {
			lastButton = gc.getInput().getPressed();
			if(lastButton.length() == 1 && name.length() < size) {
				name += lastButton;
				pointer++;
				pointerX = gc.getRenderer().getFont().getWorldLength(name.substring(0, pointer));
			}
			else {
				switch(lastButton) {
				case "Left":
					if(pointer>0) {
						pointer--;
						pointerX = gc.getRenderer().getFont().getWorldLength(name.substring(0, pointer));
					}
					break;
				case "Right":
					if(pointer<name.length()) {
						pointer++;
						pointerX = gc.getRenderer().getFont().getWorldLength(name.substring(0, pointer));
					}
					break;
				case "Space":
					name += " ";
					pointerX = gc.getRenderer().getFont().getWorldLength(name.substring(0, pointer));
					break;
				case "Enter":
					focused = false;
					break;
				case "Backspace":
					if(pointer>0) {
						name = name.substring(0, pointer-1) + name.substring(pointer);
						pointer--;
						pointerX = gc.getRenderer().getFont().getWorldLength(name.substring(0, pointer));
					}
					break;
				case "Escape":
					focused = false;
					break;
				
				case "Period":
					insertText(".");
					break;
				}if(!focused) {
					gc.getInput().turnOffInserting();
				}
			}
		}
		if(pointerChanger < pointerChangerMax)
			pointerChanger++;
		else
			pointerChanger = 0;
	}
	
	@Override
	public void render(GameContainer gc, Renderer r) {
		r.drawFillRect(x, y, width, height, colorInner);
		r.drawRect(x, y, width, height, colorOuter);
		r.drawText(name, x + width/2, y+height/3, 0xff000000, true);
		if(pointerChanger < pointerChangerMax/2)
			r.drawText("|", x + width/2 + pointerX, (int) (y+ (double) (height*2)/7), 0xff000000, true);
	}
	
	@Override
	public void act(GameManager gm, GameContainer gc, SimpleMenuManager menuManager) {
		focused = !focused;
		flip();
		gc.getInput().turn();
	}
	
	private void insertText(String t) {
		name = name.substring(0, pointer) + t + name.substring(pointer);
	}
	
	protected void setUpColors() {
		colorInner = 0xff00ff00;
		colorOuter = 0xffff00ff;
	}
	
	private void flip() {
		int buf = colorInner;
		colorInner = colorOuter;
		colorOuter = buf;
	}
	
	@Override
	public void refresh() {
		name = "";
		focused = false;
		pointer = 0;
		setUpColors();
	}
	
	public void setFocused(boolean focused) {
		this.focused = focused;
	}
	
}
