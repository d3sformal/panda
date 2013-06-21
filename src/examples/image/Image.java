package image;

public class Image
{
    private static int SIZE = 50;
	private Rectangle[] rectangles;
	private int[][] pixels;

    private Image() {
		pixels = new int[SIZE][SIZE];
    }
	
	public static void main(String[] args) {
		Image img = new Image();

		img.load();
		img.render();
	}
	
	public void load() {
		rectangles = new Rectangle[2];
		
		Rectangle r1 = new Rectangle();
		Rectangle r2 = new Rectangle();
		
		r1.top = 10;
		r1.left = 10;
		r1.right = 30;
		r1.bottom = 15;
		r1.color = 2;
		rectangles[0] = r1;
		
		r2.top = 5;
		r2.left = 5;
		r2.right = 25;
		r2.bottom = 25;
		r2.color = 3;
		rectangles[1] = r2;
	}

	public void render() {
		Rectangle rec = null;		
		int i, j;

		// loop over all rectangles and draw them
		for (int k = 0; k < rectangles.length; ++k) {
		
	    	// change relevant pixels to rectangle color
			for (i = rec.left; i <= rec.right; i++) {
				for (j = rec.top; j <= rec.bottom; j++) {
					pixels[i][j] = rec.color;
				}
			}
		}
	}
}

class Rectangle
{
	public int top,left,right,bottom;
	public int color;
}

