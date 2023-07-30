package br.eng.rodrigogml.rfw.kernel.location;

import br.eng.rodrigogml.rfw.kernel.vo.RFWVO_;

// Esta classe foi gerada utilizando o MetaObjectGenerator em 30/07/2023 10:33:06. Não edite!
public class LocationCountryDefaultVO_ extends RFWVO_ {
	private static final long serialVersionUID = 5053934057657155145L;

	public static final LocationCountryDefaultVO_ vo() {
		return new LocationCountryDefaultVO_();
	}

	public final static String _id = "id";

	private LocationCountryDefaultVO_() {
	}

	public LocationCountryDefaultVO_(String basepath) {
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
	public String bacenCode() {
		return getAttributePath("bacenCode");
	}
}