package gov.nasa.jpf.abstraction.common;

public abstract class BytecodeRange implements Iterable<Integer> {
    public BytecodeRange merge(BytecodeRange r) {
        if (r instanceof BytecodeUnlimitedRange) {
            return merge((BytecodeUnlimitedRange) r);
        }

        if (r instanceof BytecodeInterval) {
            return merge((BytecodeInterval) r);
        }

        if (r instanceof BytecodeIntervals) {
            return merge((BytecodeIntervals) r);
        }

        throw new RuntimeException("Unsupported bytecode range: `" + r + "`");
    }

    public BytecodeUnlimitedRange merge(BytecodeUnlimitedRange ur) {
        return ur;
    }

    public abstract BytecodeRange merge(BytecodeInterval i);
    public abstract BytecodeRange merge(BytecodeIntervals is);

    public abstract boolean contains(int pc);
}
