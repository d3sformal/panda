#include <stdlib.h>

extern void __VERIFIER_error() __attribute__ ((__noreturn__));

void __VERIFIER_assert(int cond) {
  if (!(cond)) {
    ERROR: __VERIFIER_error();
  }
  return;
}
int __VERIFIER_nondet_int();

typedef struct {
    int idnum;
    int time;
    int bonus;
} Cyclist;

Cyclist* cyclist_new() {
    Cyclist* cl = (Cyclist*) malloc(sizeof(Cyclist));

    return cl;
}

int main() {
    Cyclist* cyclist[3];

    Cyclist* cl = NULL;

    cl = cyclist_new();
    cl->idnum = __VERIFIER_nondet_int();
    cl->time = 3725;
    cl->bonus = 5;
    cyclist[0] = cl;

    cl = cyclist_new();
    cl->idnum = __VERIFIER_nondet_int();
    cl->time = 3569;
    cl->bonus = 10;
    cyclist[1] = cl;

    cl = cyclist_new();
    cl->idnum = __VERIFIER_nondet_int();
    cl->time = 3766;
    cl->bonus = 50;
    cyclist[2] = cl;

    Cyclist* result[3];

    int i, j;

    for (i = 0; i < 3; ++i) {
        int res = cyclist[i]->time - cyclist[i]->bonus;

        int pos = 0;

        for (j = i - 1; j >= 0; --j) {
            if (res < result[j]->time - result[j]->bonus) {
                __VERIFIER_assert(j + 1 < 3);

                result[j + 1] = result[j];
            } else {
                pos = j + 1;
            }
        }

        __VERIFIER_assert(pos < 3);

        result[pos] = cyclist[i];
    }

    int diffs[3];

    Cyclist* bestcl = result[0];
    int besttime = bestcl->time - bestcl->bonus;
    diffs[0] = besttime;

    for (i = 1; i < 3; ++i) {
        cl = result[i];

        int diff = __VERIFIER_nondet_int();
        diffs[i] = diff;
    }
}
