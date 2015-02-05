#include <stdlib.h>
#include <stdbool.h>

extern void __VERIFIER_error() __attribute__ ((__noreturn__));

void __VERIFIER_assert(int cond) {
  if (!(cond)) {
    ERROR: __VERIFIER_error();
  }
  return;
}
int __VERIFIER_nondet_int();

typedef struct {
    int* succ;
    size_t succlen;
    int* fact;
    size_t factlen;
} NodeInfo;

NodeInfo* node_info_new(int* succ, size_t succlen) {
    NodeInfo* ni = (NodeInfo*) malloc(sizeof(NodeInfo));

    ni->succ = succ;
    ni->succlen = succlen;
    ni->fact = (int*) malloc(1 * sizeof(int));
    ni->factlen = 1;

    ni->fact[0] = __VERIFIER_nondet_int();

    return ni;
}

int main() {
    NodeInfo* cfg[5];

    int* succ;
    size_t succlen;

    succ = (int*) malloc(1 * sizeof(int));
    succlen = 1;
    succ[0] = 1;

    cfg[0] = node_info_new(succ, succlen);

    succ = (int*) malloc(2 * sizeof(int));
    succlen = 2;
    succ[0] = 2;
    succ[1] = 3;

    cfg[1] = node_info_new(succ, succlen);

    succ = (int*) malloc(1 * sizeof(int));
    succlen = 1;
    succ[0] = 4;

    cfg[2] = node_info_new(succ, succlen);

    succ = (int*) malloc(1 * sizeof(int));
    succlen = 1;
    succ[0] = 4;

    cfg[3] = node_info_new(succ, succlen);

    succ = (int*) malloc(0 * sizeof(int));
    succlen = 0;

    cfg[4] = node_info_new(succ, succlen);

    int i, j, k;

    int queue[26];

    i = 0;
    j = i + 1;
    queue[i] = 0;

    int* oldfact;
    size_t oldfactlen;

    int* newfact;
    size_t newfactlen;

    while (i != j) {
        int cfnodeid = queue[i];

        i = i + 1;

        if (i >= 26) {
            i = 0;
        }

        oldfact = cfg[cfnodeid]->fact;
        oldfactlen = cfg[cfnodeid]->factlen;

        newfact = (int*) malloc(oldfactlen * sizeof(int));
        newfactlen = oldfactlen;

        for (k = 0; k < oldfactlen; ++k) {
            newfact[k] = oldfact[k];
        }

        newfact[newfactlen - 1] = __VERIFIER_nondet_int();

        cfg[cfnodeid]->fact = newfact;
        cfg[cfnodeid]->factlen = newfactlen;

        bool equal = (oldfact[oldfactlen - 1] == newfact[newfactlen - 1]);

        if (!equal) {
            __VERIFIER_assert(cfnodeid < 5);

            succ = cfg[cfnodeid]->succ;
            succlen = cfg[cfnodeid]->succlen;

            for (k = 0; k < succlen; ++k) {
                __VERIFIER_assert(j < 26);

                queue[j] = succ[k];

                j = j + 1;

                if (j >= 26) {
                    j = 0;
                }
            }
        }
    }

    return EXIT_SUCCESS;
}
