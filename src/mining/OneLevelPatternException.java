package mining;

public class OneLevelPatternException extends Exception {
	public OneLevelPatternException(String pattern) {
		super("La lunghezza di "+pattern+" � 1");
	}
}
