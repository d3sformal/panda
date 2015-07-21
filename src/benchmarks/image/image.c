#include <stdlib.h>

#define SIZE 6

extern void __VERIFIER_error() __attribute__ ((__noreturn__));

void __VERIFIER_assert(int cond) {
  if (!(cond)) {
    ERROR: __VERIFIER_error();
  }
  return;
}
int __VERIFIER_nondet_int();

typedef struct {
    int top;
    int left;
    int bottom;
    int right;
    int color;
} Rectangle;

Rectangle* rectangle_new() {
    Rectangle* r = (Rectangle*) malloc(sizeof(Rectangle));

    return r;
}

typedef struct Image Image;

typedef struct {
    void (*load) (Image*);
    void (*render) (Image*);
} ImageClass;

struct Image {
    ImageClass* class;

    Rectangle** rec;
    size_t reclen;

    int pixel[SIZE][SIZE];
};

void _crop(Rectangle* r) {
    if (r->top < 0) r->top = 0;
    if (r->bottom < 0) r->bottom = 0;

    if (r->top > SIZE - 1) r->top = SIZE - 1;
    if (r->bottom > SIZE - 1) r->bottom = SIZE - 1;

    if (r->left < 0) r->left = 0;
    if (r->right < 0) r->right = 0;

    if (r->left > SIZE - 1) r->left = SIZE - 1;
    if (r->right > SIZE - 1) r->right = SIZE - 1;
}

void _img_load(Image* img) {
    img->rec = (Rectangle**) malloc(2 * sizeof(Rectangle*));
    img->reclen = 2;

    Rectangle* r1 = rectangle_new();
    Rectangle* r2 = rectangle_new();

    r1->top = __VERIFIER_nondet_int();
    r1->left = __VERIFIER_nondet_int();
    r1->bottom = __VERIFIER_nondet_int();
    r1->right = __VERIFIER_nondet_int();
    r1->color = __VERIFIER_nondet_int();

    _crop(r1);

    img->rec[0] = r1;

    r2->top = __VERIFIER_nondet_int();
    r2->left = __VERIFIER_nondet_int();
    r2->bottom = __VERIFIER_nondet_int();
    r2->right = __VERIFIER_nondet_int();
    r2->color = __VERIFIER_nondet_int();

    _crop(r2);

    img->rec[1] = r2;
}

void _img_render(Image* img) {
    int i, j, k;
    Rectangle* rec = NULL;

    for (k = 0; k < img->reclen; ++k) {
        rec = img->rec[k];

        for (i = rec->left; i <= rec->right; ++i) {
            for (j = rec->top; j <= rec->bottom; ++j) {
                __VERIFIER_assert(i >= 0 && i < SIZE);
                __VERIFIER_assert(j >= 0 && j < SIZE);

                img->pixel[i][j] = rec->color;
            }
        }
    }
}

static ImageClass imgclass = {
    .load = _img_load,
    .render = _img_render
};

Image* image_new() {
    Image* img = (Image*) malloc(sizeof(Image));

    img->class = &imgclass;

    return img;
}

void img_load(Image* img) {
    img->class->load(img);
}

void img_render(Image* img) {
    img->class->render(img);
}

int main() {
    Image* img = image_new();

    // Virtual method invocation
    img_load(img);
    img_render(img);

    // Static method invocation
    //_img_load(img);
    //_img_render(img);

    return 0;
}
