#include <stdlib.h>
#include <stdbool.h>

#define SIZE 5

extern void __VERIFIER_error() __attribute__ ((__noreturn__));

void __VERIFIER_assert(int cond) {
  if (!(cond)) {
    ERROR: __VERIFIER_error();
  }
  return;
}
int __VERIFIER_nondet_int();

typedef struct {
    int priority;
    bool active;
} ThreadInfo;

ThreadInfo* thread_info_new(int prio) {
    ThreadInfo* ti = (ThreadInfo*) malloc(sizeof(ThreadInfo));

    ti->active = false;
    ti->priority = prio;

    return ti;
}

int main() {
    ThreadInfo* id2thread[3];

    id2thread[0] = thread_info_new(__VERIFIER_nondet_int());
    id2thread[1] = thread_info_new(__VERIFIER_nondet_int());
    id2thread[2] = thread_info_new(__VERIFIER_nondet_int());

    id2thread[1]->active = true;
    id2thread[2]->active = true;

    size_t schedule_size = 0;
    int schedule[SIZE];

    int i, j, k;

    for (k = 0; k < 3; ++k) {
        ThreadInfo* act = id2thread[k];

        if (!act->active) continue;

        if (schedule_size == 0) {
            schedule[0] = k;
            ++schedule_size;
        } else {
            for (i = 0; i < schedule_size; ++i) {
                __VERIFIER_assert(i < SIZE);
                __VERIFIER_assert(schedule[i] < 3);

                ThreadInfo* sch = id2thread[schedule[i]];

                if (act->priority > sch->priority) {
                    for (j = schedule_size - 1; j >= i; --j) {
                        __VERIFIER_assert(j + 1 < SIZE);

                        schedule[j + 1] = schedule[j];
                    }

                    schedule[i] = k;
                    ++schedule_size;
                    break;
                }
            }
        }
    }
}
