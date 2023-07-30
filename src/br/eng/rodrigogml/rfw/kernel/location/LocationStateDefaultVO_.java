package br.eng.rodrigogml.rfw.kernel.location;

import br.eng.rodrigogml.rfw.kernel.vo.RFWVO_;

// Esta classe foi gerada utilizando o MetaObjectGenerator em 30/07/2023 10:33:06. Não edite!
public class LocationStateDefaultVO_ extends RFWVO_ {
	private static final long serialVersionUID = -5032971990664681669L;

	public static final LocationStateDefaultVO_ vo() {
		return new LocationStateDefaultVO_();
	}

	public final static String _id = "id";

	private LocationStateDefaultVO_() {
	}

	public LocationStateDefaultVO_(String basepath) {
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
	public String acronym() {
		return getAttributePath("acronym");
	}

	/**
	 * Este método foi gerado a partir de um método GET. É possível que ele seja apenas um método somente de leitura.
	 */
	public LocationCountryDefaultVO_ locationCountryVO() {
		return new LocationCountryDefaultVO_(getAttributePath("locationCountryVO"));
	}

	/**
	 * Este método foi gerado a partir de um método GET. É possível que ele seja apenas um método somente de leitura.
	 */
	public String ibgeCode() {
		return getAttributePath("ibgeCode");
	}
}