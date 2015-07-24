#include <stdlib.h>

#define assert(c) {if(!(c)) __VERIFIER_error();}

static const int false = 0;
static const int true = 1;

typedef struct _client_t client_t;
typedef struct _node_t node_t;

int __VERIFIER_nondet_int();

static int unknown() {
    int u = __VERIFIER_nondet_int();

    if (u < 0) {
        u = -u;
    }

    return u;
}

struct _node_t {
    client_t* c;
    int waketime;
    node_t* next;
};

typedef node_t* list_t;

static list_t list_create() {
    return NULL;
}

static node_t* node_create(client_t* c, int t) {
    node_t* n = (node_t*) malloc(sizeof(node_t));

    n->c = c;
    n->waketime = t;
    n->next = NULL;

    return n;
}

static void node_destroy(list_t l) {
    if (l) {
        node_destroy(l->next);
        free(l);
    }
}

static int list_empty(list_t l) {
    return l == NULL;
}

static list_t list_add(list_t l, int t, client_t* c) {
    node_t* p = l;
    node_t* n = node_create(c, t);

    if (list_empty(l)) {
        return n;
    } else {
        if (p->waketime > t) {
            n->next = p;
            return n;
        } else {
            while (p->next && p->waketime < t) {
                p = p->next;
            }

            n->next = p->next;
            p->next = n;
        }
    }

    return l;
}

static client_t* list_first(list_t l) {
    return l->c;
}

static int list_first_wake_time(list_t l) {
    return l->waketime;
}

static list_t list_remove_first(list_t l) {
    node_t* n = l->next;

    l->next = NULL;

    node_destroy(l);

    return n;
}

static void list_destroy(list_t l) {
    node_destroy(l);
}

typedef struct {
    int now;
    list_t wait;
    int max;
} monitor_t;

static monitor_t* monitor_create(int max) {
    monitor_t* m = (monitor_t*) malloc(sizeof(monitor_t));

    m->now = 0;
    m->wait = list_create();
    m->max = max;

    return m;
}

typedef enum {
    INIT,
    TERM
} phase_t;

struct _client_t {
    int name;
    monitor_t* m;
    int done;
    int waiting;
    phase_t phase;
};

static void client_puttosleep(client_t* c) {
    c->waiting = true;
}

static void client_wakeup(client_t* c) {
    c->waiting = false;
}

static void monitor_tick(monitor_t* m) {
    m->now = m->now + 1;

    while (!list_empty(m->wait) && list_first_wake_time(m->wait) <= m->now) {
        client_t* wakeup = list_first(m->wait);
        m->wait = list_remove_first(m->wait);
        client_wakeup(wakeup);
    }
}

static void monitor_wake_me(monitor_t* m, client_t* c, int interval) {
    int waketime = m->now + interval;

    if (waketime >= m->max) {
        return;
    }

    m->wait = list_add(m->wait, waketime, c);

    client_puttosleep(c);
}

static int monitor_time(monitor_t* m) {
    return m->now;
}

static void monitor_destroy(monitor_t* m) {
    list_destroy(m->wait);
    free(m);
}

static client_t* client_create(int n, monitor_t* m) {
    client_t* c = (client_t*) malloc(sizeof(client_t));

    c->name = n;
    c->m = m;
    c->done = false;
    c->waiting = false;
    c->phase = INIT;

    return c;
}

static void client_step(client_t* c) {
    assert(!c->done);
    assert(!c->waiting);

    switch (c->phase) {
        case INIT: {
            int t = unknown();

            while (t >= 5) {
                t = t - 5;
            }

            monitor_wake_me(c->m, c, t);

            c->phase = TERM;
        }
        break;

        case TERM: {
            c->done = true;
        }
        break;
    }
}

static int client_done(client_t* c) {
    return c->done;
}

static int client_waiting(client_t* c) {
    return c->waiting;
}

static void client_destroy(client_t* c) {
    free(c);
}

typedef struct {
    monitor_t* m;
    int max;
    int done;
    int sleep;
} alarmclock_t;

static alarmclock_t* clock_create(monitor_t* m, int max) {
    alarmclock_t* c = (alarmclock_t*) malloc(sizeof(alarmclock_t));

    c->m = m;
    c->max = max;
    c->done = false;
    c->sleep = 0;

    return c;
}

static void clock_step(alarmclock_t* c) {
    assert(!c->done);
    assert(c->sleep <= 0);

    c->sleep = 0;

    if (monitor_time(c->m) < c->max) {
        monitor_tick(c->m);

        c->sleep += 3;
    } else {
        c->done = true;
    }
}

static void alarmclock_time_quantum_elapsed(alarmclock_t* c) {
    c->sleep = c->sleep - 1;
}

static int clock_done(alarmclock_t* c) {
    return c->done;
}

static int clock_sleeping(alarmclock_t* c) {
    return c->sleep > 0;
}

static void clock_destroy(alarmclock_t* c) {
    free(c);
}

static void wake_clock(alarmclock_t* c) {
    clock_step(c);
}

static void wake_client(client_t** cl, int n, int i) {
    int count = 0;

    while (client_done(cl[i]) || client_waiting(cl[i])) {
        assert(count < n);

        count = count + 1;
        i = i + 1;

        while (i >= n) {
            i = i - n;
        }
    }

    client_step(cl[i]);
}

static int schedule(monitor_t* m, alarmclock_t* c, client_t** cl, int n) {
    alarmclock_time_quantum_elapsed(c);

    int c_done = clock_done(c);
    int c_sleeping = clock_sleeping(c);
    int c_ready = !c_done && !c_sleeping;

    int cl_done = true;
    int cl_ready = false;

    int k;

    for (k = 0; k < n; ++k) {
        if (!client_done(cl[k])) {
            cl_done = false;

            if (!client_waiting(cl[k])) {
                cl_ready = true;

                break;
            }
        }
    }

    if (!cl_done) {
        //assert(!c_done || cl_ready);

        int i = unknown();
        int mod = (c_ready ? 1 : 0) + n;

        while (i >= mod) {
            i = i - mod;
        }

        if (c_ready && cl_ready) {
            if (i == 0) {
                wake_clock(c);
            } else {
                wake_client(cl, n, i - 1);
            }
        } else if (c_ready) {
            wake_clock(c);
        } else if (cl_ready) {
            wake_client(cl, n, i);
        }

        return true;
    }

    return false;
}

int main () {
    int maxTime = 20;
    monitor_t* m = monitor_create(maxTime);
    alarmclock_t* c = clock_create(m, maxTime);

    client_t* cl[2];

    cl[0] = client_create(1, m);
    cl[1] = client_create(2, m);

    while (schedule(m, c, cl, 2)) {
    }

    client_destroy(cl[1]);
    client_destroy(cl[0]);
    clock_destroy(c);
    monitor_destroy(m);
}
