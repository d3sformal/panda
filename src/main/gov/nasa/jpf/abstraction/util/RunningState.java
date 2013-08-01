package gov.nasa.jpf.abstraction.util;

public class RunningState implements Cloneable {
	private enum State {
		JUST_BEGAN_RUNNING,
		RUNNING,
		NOT_RUNNING,
		JUST_CEASED_RUNNING
	}
	
	private State state;
	
	protected RunningState(State state) {
		this.state = state;
	}
	
	public RunningState() {
		this(State.NOT_RUNNING);
	}
	
	public void running() {
		switch (state) {
		case NOT_RUNNING:
		case JUST_CEASED_RUNNING:
			state = State.JUST_BEGAN_RUNNING;
			break;
		default:
			state = State.RUNNING;
		}
	}
	
	public void notRunning() {
		switch (state) {
		case JUST_BEGAN_RUNNING:
		case RUNNING:
			state = State.JUST_CEASED_RUNNING;
			break;
		default:
			state = State.NOT_RUNNING;
		}
	}
	
	public boolean isRunning() {
		switch (state) {
		case JUST_BEGAN_RUNNING:
		case RUNNING:
			return true;
		default:
			return false;
		}
	}
	
	public boolean hasBeenRunning() {
		switch (state) {
		case NOT_RUNNING:
			return false;
		default:
			return true;
		}
	}
	
	public boolean hasNotBeenRunning() {
		switch (state) {
		case RUNNING:
			return false;
		default:
			return true;
		}
	}
	
	public boolean isNotRunning() {
		return !isRunning();
	}
	
	@Override
	public RunningState clone() {
		return new RunningState(state);
	}

	public void touch() {
		switch (state) {
		case JUST_BEGAN_RUNNING:
			state = State.RUNNING;
			break;
		case JUST_CEASED_RUNNING:
			state = State.NOT_RUNNING;
			break;
		}
	}
}
