package br.eng.rodrigogml.rfw.kernel.file.vo;

import br.eng.rodrigogml.rfw.kernel.vo.RFWVO_;

// Esta classe foi gerada utilizando o MetaObjectGenerator em 08/08/2023 13:46:35. Não edite!
public class FileContentVO_ extends RFWVO_ {
	private static final long serialVersionUID = 3302186106595703387L;

	public static final FileContentVO_ vo() {
		return new FileContentVO_();
	}

	public final static String _id = "id";
	public final static String _content = "content";
	public final static String _fileVO = "fileVO";

	private FileContentVO_() {
	}

	public FileContentVO_(String basepath) {
		super(basepath);
	}

	public String content() {
		return getAttributePath("content");
	}

	public FileVO_ fileVO() {
		return new FileVO_(getAttributePath("fileVO"));
	}
}