package br.eng.rodrigogml.rfw.kernel.location;

import br.eng.rodrigogml.rfw.kernel.vo.RFWVO_;

// Esta classe foi gerada utilizando o MetaObjectGenerator em 30/07/2023 10:33:06. Não edite!
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
	 * Este método foi gerado a partir de um método GET. É possível que ele seja apenas um método somente de leitura.
	 */
	public String name() {
		return getAttributePath("name");
	}

	/**
	 * Este método foi gerado a partir de um método GET. É possível que ele seja apenas um método somente de leitura.
	 */
	public LocationStateDefaultVO_ locationStateVO() {
		return new LocationStateDefaultVO_(getAttributePath("locationStateVO"));
	}

	/**
	 * Este método foi gerado a partir de um método GET. É possível que ele seja apenas um método somente de leitura.
	 */
	public String ibgeCode() {
		return getAttributePath("ibgeCode");
	}
}