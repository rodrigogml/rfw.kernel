package br.eng.rodrigogml.rfw.kernel.location;

import br.eng.rodrigogml.rfw.kernel.vo.RFWVO_;

// Esta classe foi gerada utilizando o MetaObjectGenerator em 30/07/2023 10:33:06. N�o edite!
public class LocationAddressDefaultVO_ extends RFWVO_ {
	private static final long serialVersionUID = 5355976060439907724L;

	public static final LocationAddressDefaultVO_ vo() {
		return new LocationAddressDefaultVO_();
	}

	public final static String _id = "id";

	private LocationAddressDefaultVO_() {
	}

	public LocationAddressDefaultVO_(String basepath) {
		super(basepath);
	}

	/**
	 * Este m�todo foi gerado a partir de um m�todo GET. � poss�vel que ele seja apenas um m�todo somente de leitura.
	 */
	public LocationCityDefaultVO_ locationCityVO() {
		return new LocationCityDefaultVO_(getAttributePath("locationCityVO"));
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
	public String neighborhood() {
		return getAttributePath("neighborhood");
	}

	/**
	 * Este m�todo foi gerado a partir de um m�todo GET. � poss�vel que ele seja apenas um m�todo somente de leitura.
	 */
	public String cep() {
		return getAttributePath("cep");
	}
}