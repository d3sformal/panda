package image;

public class Image
{
    private static int SIZE = 6;
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
		
		r1.top = 1;
		r1.left = 1;
		r1.right = 4;
		r1.bottom = 2;
		r1.color = 2;
		rectangles[0] = r1;
		
		r2.top = 0;
		r2.left = 0;
		r2.right = 3;
		r2.bottom = 3;
		r2.color = 3;
		rectangles[1] = r2;
	}

	public void render() {
		Rectangle rec = null;		

		// loop over all rectangles and draw them
		for (int k = 0; k < rectangles.length; ++k) {
			rec = rectangles[k];
		
	    	// change relevant pixels to rectangle color
			for (int i = rec.left; i <= rec.right; i++) {
				for (int j = rec.top; j <= rec.bottom; j++) {
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

