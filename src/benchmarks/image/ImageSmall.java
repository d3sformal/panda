package image;

import gov.nasa.jpf.abstraction.Verifier;

public class ImageSmall {
    private static int SIZE = 3;
    private Rectangle[] rectangles;
    private Object[] pixels;

    private ImageSmall() {
        pixels = new Object[SIZE];
        for (int i = 0; i < SIZE; ++i) {
            pixels[i] = new int[SIZE];
        }
    }

    public static void main(String[] args) {
        ImageSmall img = new ImageSmall();

        img.load();
        img.render();
    }

    public void load() {
        rectangles = new Rectangle[2];

        Rectangle r1 = new Rectangle();
        Rectangle r2 = new Rectangle();

        r1.top = Verifier.unknownInt();
        r1.left = Verifier.unknownInt();
        r1.right = Verifier.unknownInt();
        r1.bottom = Verifier.unknownInt();
        r1.color = Verifier.unknownInt();

        crop(r1);

        rectangles[0] = r1;

        r2.top = Verifier.unknownInt();
        r2.left = Verifier.unknownInt();
        r2.right = Verifier.unknownInt();
        r2.bottom = Verifier.unknownInt();
        r2.color = Verifier.unknownInt();

        crop(r2);

        rectangles[1] = r2;
    }

    public static void crop(Rectangle r) {
        if (r.top < 0) r.top = 0;

        if (r.bottom < 0) r.bottom = 0;

        if (r.top > SIZE - 1) r.top = SIZE - 1;
        if (r.bottom > SIZE - 1) r.bottom = SIZE - 1;

        if (r.left < 0) r.left = 0;
        if (r.right < 0) r.right = 0;

        if (r.left > SIZE - 1) r.left = SIZE - 1;
        if (r.right > SIZE - 1) r.right = SIZE - 1;
    }

    public void render() {
        Rectangle rec = null;

        // loop over all rectangles and draw them
        for (int k = 0; k < rectangles.length; ++k) {
            rec = rectangles[k];

            // change relevant pixels to rectangle color
            for (int i = rec.left; i <= rec.right; i++) {
                for (int j = rec.top; j <= rec.bottom; j++) {
                    ((int[]) pixels[i])[j] = rec.color;
                }
            }
        }
    }

}
