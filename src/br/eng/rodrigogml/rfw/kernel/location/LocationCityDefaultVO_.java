package br.eng.rodrigogml.rfw.kernel.location;

import br.eng.rodrigogml.rfw.kernel.vo.RFWVO_;

// Esta classe foi gerada utilizando o MetaObjectGenerator em 30/07/2023 10:33:06. N�o edite!
public class LocationCityDefaultVO_ extends RFWVO_ {
	private static final long serialVersionUID = 1220254158188968736L;

	public static final LocationCityDefaultVO_ vo() {
		return new LocationCityDefaultVO_();
	}

	public final static String _id = "id";

	private LocationCityDefaultVO_() {
	}

	public LocationCityDefaultVO_(String basepath) {
		super(basepath);
	}

	/**
	 * Este m�todo foi gerado a partir de um m�todo GET. � poss�vel que ele seja apenas um m�todo somente de leitura.
	 */
	public String name() {
		return getAttributePath("name");
	}

	/**
	 * Este m�todo foi gerado a partir de um m�todo GET. � poss�vel que ele seja apenas um m�todo somente de leitura.
	 */
	public LocationStateDefaultVO_ locationStateVO() {
		return new LocationStateDefaultVO_(getAttributePath("locationStateVO"));
	}

	/**
	 * Este m�todo foi gerado a partir de um m�todo GET. � poss�vel que ele seja apenas um m�todo somente de leitura.
	 */
	public String ibgeCode() {
		return getAttributePath("ibgeCode");
	}
}