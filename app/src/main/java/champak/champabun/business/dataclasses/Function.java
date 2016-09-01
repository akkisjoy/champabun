package champak.champabun.business.dataclasses;

/**
 * Simple Function, designed after Google Guava's Function object.
 * @author nolan
 *
 * @param <E>
 * @param <T>
 */
public interface Function<E,T> {

	T apply(E input);

}
