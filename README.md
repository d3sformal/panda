**Abstract Pathfinder** (APF) is an extension for **Java Pathfinder** [\[1\]](http://babelfish.arc.nasa.gov/trac/jpf), which introduces support for data abstraction.
Abstract Pathfinder supports predicate abstraction and various basic abstractions for numeric data domains, such as signs and intervals.

The project was started as a Google Summer of Code (GSoC) project in 2012 [\[2\]](https://bitbucket.org/artkhyzha/jpf-abstraction) with the goal to implement basic abstractions of numeric data domains. Support for predicate abstraction was added in the scope of another GSoC project in 2013 [\[3\]](https://bitbucket.org/jd823592/jpf-abstraction).


### Authors: ###

* Jakub Daniel
* Pavel Parizek (http://d3s.mff.cuni.cz/~parizek)
* Corina Pasareanu


## Prerequisites ##

1. **Java 7**  
To be able to run Abstract Pathfinder with predicate abstraction, it is necessary to have JDK 7 installed on your system.
You can download the JDK 7 directly from the [Oracle web site](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html).

2. **MathSAT 5**  
Currently only the Linux x86-64 binaries of MathSAT are included within the project repository.
For platforms other than Linux x86-64, you also need to obtain the appropriate binary of MathSAT 5 at the [MathSAT web site](http://mathsat.fbk.eu/download.html).


## Installation ##

The installation guide is mostly specific to Linux/Unix. Modify the commands appropriately for other platforms (Windows, Mac OS).

Create an empty directory (e.g. ``~/workspace``) where you would like to install the project
```
mkdir -p ~/workspace
```

Obtain the latest _apf_
```
git clone https://github.com/d3sformal/apf.git
```

Build _apf_
```
cd ~/workspace/apf
ant clean build
```

Make sure that ``bin/mathsat`` is executable on your platform before continuing.


## Configuration ##

To perform abstract execution of a target program, it is necessary to provide a configuration file (``.jpf``). Assuming that the target class is ``target.Target`` and that it is stored in ``src/examples``, then the content of the file ``Target.jpf`` (typically in the same directory) would look like:
```
@using=jpf-abstraction

target=target.Target

classpath=build/examples
sourcepath=src/examples

abstract.domain=PREDICATES src/examples/target/Target.pred

listener=gov.nasa.jpf.abstraction.AbstractListener
```

Notable configuration attributes are:

1. **target**  
The target program (Java class with the ``main`` method).

2. **abstract.domain**  
The abstraction to be used - in our case ``PREDICATES`` - followed by a path to the file with input predicates. Other supported abstractions are described [here](https://bitbucket.org/artkhyzha/jpf-abstraction/wiki/Running_Abstract_Pathfinder).

3. **listener**  
The ``gov.nasa.jpf.abstraction.AbstractListener`` listener is mandatory for the predicate abstraction to work properly. 
It is also possible to specify additional listeners to get more verbose output:
    * ``gov.nasa.jpf.abstraction.predicate.util.PredicateValuationMonitor``  
    prints the values of all predicates after each instruction.

    * ``gov.nasa.jpf.listener.ExecTracker`` or ``gov.nasa.jpf.abstraction.util.InstructionTracker``  
    to put the output of other listeners into the context of executed bytecode instructions.

4. **vm.serializer.class**  
It is necessary to set this option to ``gov.nasa.jpf.abstraction.predicate.PredicateAbstractionSerializer`` to enable abstract state matching.

More configuration options are described on the [_jpf-core_ configuration page](http://babelfish.arc.nasa.gov/trac/jpf/wiki/user/config).


## Input predicates ##

The file with input predicates (usually having the suffix ``.pred``) is divided into sections that we call _contexts_.
```
[static]
...

[object ...]
...

[method ...]
...
```

In _static_ contexts, it is possible to define predicates over static fields and numeric constants.
In _object_ contexts, it is possible to define predicates that refer also to object fields. The scope is defined by the type of objects which is specified in the header of the section (e.g. ``[object target.Target]``).
In _method_ contexts, it is possible to define predicates that refer also to local variables. The scope is once again determined by the method full name supplied in the header of the section (e.g. ``[method target.Target.main]``).
The number and order of occurrences of the sections is not limited in any way.
Each of the sections may contain an arbitrary number of predicates.

### Predicates ###

The predicates must be defined in the form of equalities and inequalities of arithmetic expressions over numeric constants and access expressions.

For example:
```
a.b = c + d - 1
```

### Access Expressions ###

An access expression can be a local variable, static field, an object field, or array element access.
```
o
o.f
a[0]
class(pkg.Class).f
alength(arrlen, a)
```

Here, the ``class(...)`` symbol distinguishes static field access from an object field access. The expression wrapped in ``class(...)`` must refer to a Java class name. The ``alength(arrlen, ...)`` symbol is a special accessor for array length that distinguishes it from a field access. These special symbols are needed because the predicate language is not typed and has no information about runtime classes.

There are two notations for specifying the predicates:

1. Java-like dot notation demonstrated above
2. Function notation:
    * ``sfread(f, pkg.Class)`` for ``class(pkg.Class).f``
    * ``fread(f, o)`` for ``o.f``
    * ``aread(arr, a, i)`` for ``a[i]``

Method context may define predicates over the keyword ``return``, which are used for propagation of truth values of predicates over method call boundaries.

## Running ##

To run Abstract Pathfinder, simply issue the following command within the directory containing _apf_ 
```
bin/run.sh {path-to-a-jpf-file}
```

## Example ##

The following example can be found in the project repository and can be run using the command mentioned below. The example consists of three files.

### Source program ``src/example/arraylength/ALength.java`` ###

```
package arraylength;

public class ALength {
    public static void main(String[] args) {
        int i = 3;
        int a[] = new int[i];
    }
}
```

### File with input predicates ``src/example/arraylength/ALength.pred`` ###

```
[method arraylength.ALength.main]
alength(arrlen, a) = -1
alength(arrlen, a) = i
alength(arrlen, a) = 3
i = 3
```

### Configuration file of JPF ``src/example/arraylength/ALength.jpf`` ###

```
@using=jpf-abstraction

target=arraylength.ALength

classpath=build/examples
sourcepath=src/examples

abstract.domain=PREDICATES src/examples/arraylength/ALength.pred

listener=gov.nasa.jpf.abstraction.AbstractListener
```

For the purpose of this example, we omitted some of the non-essential listeners, which print only debugging information. 
### Running ###

To run the example, simply issue the following command within the directory containing _apf_.
```
bin/run.sh src/examples/arraylength/ALength.jpf
```

### Output ###

The output of the command should look like this:

```
Running Abstract PathFinder ...
[method arraylength.ALength.main]
alength(arrlen, a) = -1
alength(arrlen, a) = i
alength(arrlen, a) = 3
i = 3

JavaPathfinder v7.0 (rev 1117) - (C) RIACS/NASA Ames Research Center


====================================================== system under test
arraylength.ALength.main()

====================================================== search started: 20/09/13 16:39

====================================================== results
no errors detected

====================================================== statistics
elapsed time:       00:00:00
states:             new=1, visited=0, backtracked=1, end=1
search:             maxDepth=1, constraints hit=0
choice generators:  thread=1 (signal=0, lock=1, shared ref=0), data=0
heap:               new=349, released=12, max live=0, gc-cycles=1
instructions:       3276
max memory:         120MB
loaded code:        classes=56, methods=1112

====================================================== search finished: 20/09/13 16:39
```

Abstract Pathfinder parses the input file and prints all the collected predicates in the default function notation. If some other listeners were added, then a lot of output may follow. In the end, there is the expected statement ``no errors detected`` and statistics provided by _jpf-core_ (number of choices, etc).

# Links #
1. http://babelfish.arc.nasa.gov/trac/jpf
2. https://bitbucket.org/artkhyzha/jpf-abstraction
3. https://bitbucket.org/jd823592/jpf-abstraction
