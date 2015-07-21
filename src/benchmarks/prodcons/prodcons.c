#include <stdlib.h>

#define assert(c) {if(!(c)) __VERIFIER_error();}

#define PRODS 2
#define CONS 2
#define TOTAL (PRODS * CONS)
#define PRODS_ITEMS (TOTAL / PRODS)
#define CONS_ITEMS (TOTAL / CONS)

static const int false = 0;
static const int true = 1;

static int total = 0;

int __VERIFIER_nondet_int();

static int unknown() {
    int u = __VERIFIER_nondet_int();

    if (u < 0) {
        u = -u;
    }

    return u;
}

typedef struct {
    int size;
    void** array;
    int put;
    int get;
    int used;
} buffer_t;

static buffer_t* buffer_create(int s) {
    buffer_t* b = (buffer_t*) malloc(sizeof(buffer_t));

    b->size = s;
    b->array = (void**) malloc(s * sizeof(void*));
    b->put = 0;
    b->get = 0;
    b->used = 0;

    return b;
}

static int buffer_empty(buffer_t* b) {
    return b->used == 0;
}

static int buffer_full(buffer_t* b) {
    return b->used == b->size;
}

static void buffer_put(buffer_t* b, void* x) {
    assert(!buffer_full(b));

    b->array[b->put] = x;

    b->put = b->put + 1;

    while (b->put > b->size) {
        b->put = b->put - b->size;
    }

    b->used = b->used + 1;
}

static void* buffer_get(buffer_t* b) {
    assert(!buffer_empty(b));

    void* x = b->array[b->get];
    b->array[b->get] = NULL;

    b->get = b->get + 1;

    while (b->get >= b->size) {
        b->get = b->get - b->size;
    }

    b->used = b->used - 1;

    return x;
}

static void buffer_destroy(buffer_t* b) {
    free(b->array);
    free(b);
}

static void* payload = NULL;

typedef struct {
    buffer_t* b;
    int count;
    int done;
} producer_t;

static producer_t* producer_create(buffer_t* b) {
    producer_t* p = (producer_t*) malloc(sizeof(producer_t));

    p->b = b;
    p->count = 0;
    p->done = false;

    return p;
}

static int producer_done(producer_t* p) {
    return p->done;
}

static void producer_step(producer_t* p) {
    assert(!producer_done(p));

    if (p->count < PRODS_ITEMS) {
        payload = payload + 1;

        buffer_put(p->b, payload);
    }

    p->count = p->count + 1;

    if (p->count >= PRODS_ITEMS) {
        p->done = true;
    }
}

static int producer_count(producer_t* p) {
    return p->count;
}

static void producer_destroy(producer_t* p) {
    free(p);
}

typedef struct {
    buffer_t* b;
    int count;
    int done;
} consumer_t;

static consumer_t* consumer_create(buffer_t* b) {
    consumer_t* c = (consumer_t*) malloc(sizeof(consumer_t));

    c->b = b;
    c->count = 0;
    c->done = false;

    return c;
}

static int consumer_done(consumer_t* c) {
    return c->done;
}

static void consumer_step(consumer_t* c) {
    assert(!consumer_done(c));

    if (c->count < CONS_ITEMS) {
        void* x = buffer_get(c->b);

        if (x == NULL) {
            c->done = true;
        }

        total = total + 1;
    }

    c->count = c->count + 1;

    if (c->count >= CONS_ITEMS) {
        c->done = true;
    }
}

static int consumer_count(consumer_t* c) {
    return c->count;
}

static void consumer_destroy(consumer_t* c) {
    free(c);
}

static void wake_producer(producer_t** prods, int i) {
    int count = 0;

    while (producer_done(prods[i])) {
        assert(count < PRODS);

        count = count + 1;

        i = i + 1;

        while (i >= PRODS) {
            i = i - PRODS;
        }
    }

    producer_step(prods[i]);
}

static void wake_consumer(consumer_t** cons, int i) {
    int count = 0;

    while (consumer_done(cons[i])) {
        assert(count < CONS);

        count = count + 1;

        i = i + 1;

        while (i >= CONS) {
            i = i - CONS;
        }
    }

    consumer_step(cons[i]);
}

static int schedule(buffer_t* b, producer_t** prods, consumer_t** cons) {
    int prods_done = true;
    int cons_done = true;
    int k;

    for (k = 0; k < PRODS; k = k + 1) {
        if (!producer_done(prods[k])) {
            prods_done = false;

            break;
        }
    }

    for (k = 0; k < CONS; k = k + 1) {
        if (!consumer_done(cons[k])) {
            cons_done = false;

            break;
        }
    }

    assert(!cons_done || !(buffer_full(b) && !prods_done));
    assert(!prods_done || !(buffer_empty(b) && !cons_done));

    if (!prods_done || !cons_done) {
        int i = unknown();

        if (!buffer_empty(b) && !buffer_full(b) && !prods_done && !cons_done) {
            int all = PRODS + CONS;

            while (i >= all) {
                i = i - all;
            }

            if (i < PRODS) {
                wake_producer(prods, i);
            } else {
                wake_consumer(cons, i - PRODS);
            }
        } else if (!buffer_empty(b) && !cons_done) {
            while (i >= CONS) {
                i = i - CONS;
            }

            wake_consumer(cons, i);
        } else if (!buffer_full(b) && !prods_done) {
            while (i >= PRODS) {
                i = i - PRODS;
            }

            wake_producer(prods, i);
        }

        return true;
    }

    return false;
}

int main() {
    producer_t* prods[PRODS];
    consumer_t* cons[CONS];
    buffer_t* b = buffer_create(5);

    int i;

    for (i = 0; i < PRODS; i = i + 1) {
        prods[i] = producer_create(b);
    }

    for (i = 0; i < CONS; i = i + 1) {
        cons[i] = consumer_create(b);
    }

    while (schedule(b, prods, cons)) {
    }

    assert(total == PRODS_ITEMS * PRODS);

    for (i = 0; i < CONS; i = i + 1) {
        consumer_destroy(cons[i]);
    }

    for (i = 0; i < PRODS; i = i + 1) {
        producer_destroy(prods[i]);
    }

    buffer_destroy(b);
}
